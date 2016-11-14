package org.freakz.hokan_ng_springboot.bot.updaters.telkku;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.enums.HostOS;
import org.freakz.hokan_ng_springboot.bot.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.exception.HokanHostOsNotSupportedException;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.jpa.service.PropertyService;
import org.freakz.hokan_ng_springboot.bot.models.TelkkuData;
import org.freakz.hokan_ng_springboot.bot.models.TelkkuProgram;
import org.freakz.hokan_ng_springboot.bot.updaters.Updater;
import org.freakz.hokan_ng_springboot.bot.util.CmdExecutor;
import org.freakz.hokan_ng_springboot.bot.util.FileUtil;
import org.freakz.hokan_ng_springboot.bot.util.HostOsDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * User: petria
 * Date: 11/22/13
 * Time: 12:43 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Service
@Slf4j
@SuppressWarnings("unchecked")
public class TelkkuUpdater extends Updater {

    @Autowired
    private CommandPool commandPool;

    @Autowired
    private PropertyService propertyService;

    private static final String XMLTV_DTD_FILE = "xmltv.dtd";

    private static final String TVGRAB_CONF_RESOURCE = "/tv_grab_fi.conf";

    private static final String TVGRAB_BIN = "/usr/bin/tv_grab_fi";

    private static final String TVGRAB_BIN_OSX = "/sw/bin/tv_grab_fi";

    private static final String FETCH_FILE = "telkku_progs.xml";
    private static final String OLD_FETCH_FILE = "old_telkku_progs.xml";
    private static final String FETCH_CHARSET = "UTF-8";

    private FileUtil fileUtil;

    private List<TelkkuProgram> programList;

    private List<String> channelNames;

    private HostOsDetector osDetector = new HostOsDetector();

    public TelkkuUpdater() {
    }

    @Autowired
    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    private String runTvGrab(HostOS hostOS) throws Exception {
        String tmpDir = fileUtil.getTmpDirectory();
        File dtdFile = new File(tmpDir + XMLTV_DTD_FILE);
        if (!dtdFile.exists()) {
            fileUtil.copyResourceToFile("/" + XMLTV_DTD_FILE, dtdFile);
        }
        String tmpConf = fileUtil.copyResourceToTmpFile(TVGRAB_CONF_RESOURCE);
        File outputFile = File.createTempFile(FETCH_FILE, "");
        String binary;
        if (hostOS == HostOS.OSX) {
            binary = TVGRAB_BIN_OSX;
        } else {
            binary = TVGRAB_BIN;
        }
        String cmd = binary + " --config-file " + tmpConf + " --output " + outputFile.getAbsolutePath();
        log.info("Running: {}", cmd);
        CmdExecutor cmdExecutor = new CmdExecutor(cmd, FETCH_CHARSET);
        log.info("Run done: {}", (Object[]) cmdExecutor.getOutput());

        return outputFile.getAbsolutePath();
    }

    @PostConstruct
    private void startTvXmlDataWatcher() {

        String watchFolder = propertyService.getPropertyAsString(PropertyName.PROP_SYS_TV_XML_DATA_WATCH_DIR, null);
        if (watchFolder == null) {
            log.error("Can't start telkku data watcher!");
            return;
        }
        final Path path = Paths.get(watchFolder);

        log.debug("Watching path: " + path);

        // We obtain the file system of the Path


        CommandRunnable runnable = new CommandRunnable() {
            @Override
            public void handleRun(long myPid, Object args) throws HokanException {
                WatchService service;
                try {
                    FileSystem fs = path.getFileSystem();
                    service = fs.newWatchService();
                    path.register(service, ENTRY_CREATE);
                } catch (Exception e) {
                    log.error("watch", e);
                    return;
                }

                // Start the infinite polling loop
                WatchKey key = null;
                while (true) {
                    try {
                        key = service.poll(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        //
                    }
                    if (key == null) {
                        continue;
                    }

                    // Dequeueing events
                    WatchEvent.Kind<?> kind;
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        // Get the type of the event
                        kind = watchEvent.kind();
                        if (ENTRY_CREATE == kind) {
                            Path newPath = ((WatchEvent<Path>) watchEvent).context();
                            String file = path.toString() + "/" + newPath.getFileName();
                            try {
                                loadFetchFile(file);
                            } catch (Exception e) {
                                log.error("Load telkku data", e);
                            }
                        }
                    }

                    if (!key.reset()) {
                        break; //loop
                    }
                }

            }
        };
        commandPool.startRunnable(runnable, "<system>");
    }


    private void loadFetchFile(String file) throws Exception {
        File f = new File(file);
        if (!f.exists()) {
            log.info("No {} found!", file);
            return;
        }
        log.info("Loading {}", file);

        List<String> channelNames = new ArrayList<>();
        this.programList = readXmlFile(file, channelNames);
        this.channelNames = channelNames;
        this.updateCount++;

    }

    @Override
    public Calendar calculateNextUpdate() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.HOUR_OF_DAY, 4);
        return cal;
    }


    @Override
    public void doUpdateData() throws Exception {
        if (getUpdateCount() == 0) {
            loadFetchFile(OLD_FETCH_FILE);
        }

        HostOS hostOS = osDetector.detectHostOs();
        if (hostOS == HostOS.WINDOWS) {
            throw new HokanHostOsNotSupportedException("Telkku updater not supported on Windows");
        }

        String fileName = runTvGrab(hostOS);
        log.info("Tv data file: {}", fileName);

        List<String> channelNames = new ArrayList<>();
        this.programList = readXmlFile(fileName, channelNames);
        this.fileUtil.copyFile(fileName, OLD_FETCH_FILE);
        this.fileUtil.deleteTmpFile(fileName);
        this.channelNames = channelNames;
    }

    @Override
    public Object doGetData(Object... args) {
        return new TelkkuData(channelNames, programList, getLastUpdateTime());
    }

    private List<TelkkuProgram> readXmlFile(String fileName, List<String> channels)
            throws ParserConfigurationException, IOException, SAXException {

        File file = new File(fileName);
        if (!file.exists()) {
            log.error("File not found: {}", file);
            return new ArrayList<>();
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nodeLst = doc.getElementsByTagName("channel");

        Map<String, String> idDisplayNameMap = new HashMap<>();

        for (int s = 0; s < nodeLst.getLength(); s++) {

            Node fstNode = nodeLst.item(s);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                String id;
                String displayName;
                Element fstElmnt = (Element) fstNode;

                id = fstElmnt.getAttribute("id");

                NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("display-name");
                Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                NodeList fstNm = fstNmElmnt.getChildNodes();
                displayName = fstNm.item(0).getNodeValue();

                idDisplayNameMap.put(id, displayName);
                channels.add(displayName);
            }
        }

        nodeLst = doc.getElementsByTagName("programme");

        List<TelkkuProgram> programs = new ArrayList<>();
        TelkkuProgram.resetIdCounter();

        for (int s = 0; s < nodeLst.getLength(); s++) {

            Node fstNode = nodeLst.item(s);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                String startStr;
                String stopStr;
                String channel;
                String title;
                String desc = null;

                Element fstElmnt = (Element) fstNode;

                startStr = fstElmnt.getAttribute("start");
                stopStr = fstElmnt.getAttribute("stop");
                if (stopStr == null || stopStr.length() == 0) {
                    continue;
                }
                channel = fstElmnt.getAttribute("channel");

                NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("title");
                Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                NodeList fstNm = fstNmElmnt.getChildNodes();
                title = fstNm.item(0).getNodeValue();

                fstNmElmntLst = fstElmnt.getElementsByTagName("desc");
                fstNmElmnt = (Element) fstNmElmntLst.item(0);
                if (fstNmElmnt != null) {
                    fstNm = fstNmElmnt.getChildNodes();
                    desc = fstNm.item(0).getNodeValue();
                }

                // 2009 01 14 06 25 00 +0200
                DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss Z");
                try {
                    Date startD, endD;

                    startD = formatter.parse(startStr);
                    endD = formatter.parse(stopStr);

                    TelkkuProgram program =
                            new TelkkuProgram(startD, endD, title, desc, idDisplayNameMap.get(channel));


                    Calendar start = Calendar.getInstance();
                    start.setTime(program.getStartTimeD());

                    Calendar end = Calendar.getInstance();
                    end.setTime(program.getEndTimeD());

                    programs.add(program);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } // for

        return programs;
    }


}
