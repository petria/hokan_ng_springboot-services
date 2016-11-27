package org.freakz.hokan_ng_springboot.bot.services.service.scripting;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.ScriptResult;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Petri Airio on 5.4.2016.
 * -
 */
@Service
@Slf4j
public class ScriptingServiceImpl {

    private static final String SCRIPT_DIR = "scripts/";
    private static final int TIMEOUT_SEC = 60;

    private ScriptResult execWithThread(final ScriptEngine engine, final String script, boolean isFile, ServiceRequest request) {
        ScriptResult scriptResult = new ScriptResult();
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);

                    engine.getContext().setWriter(pw);
                    engine.put("ims", request.getIrcMessageEvent());

                    Object res;
                    if (isFile) {
                        log.debug("File eval()");
                        res = engine.eval(new java.io.FileReader(script));
                    } else {
                        log.debug("Script eval()");
                        res = engine.eval(script);
                    }
                    log.debug("Script result: {}", res);
                    scriptResult.setScriptOutput(sw.getBuffer().toString());
                } catch (Exception e) {
                    scriptResult.setScriptOutput(e.getMessage());
                    log.debug("Java: Caught exception from eval(): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        log.debug("Java: Starting thread...");
        final Thread t = new Thread(r);
        t.start();

        long startTime = System.currentTimeMillis();
        boolean doWait = true;
        while (doWait) {
            try {
                t.join(333L);
                if (t.isAlive()) {
                    long currentTime = System.currentTimeMillis();
                    long diff = (currentTime - startTime) / 1000L;
                    if (diff > 10) {
                        scriptResult.setScriptOutput("Script timed out!");
                        log.debug("Timeout waiting scrip to finish!");
                        t.stop();
                        doWait = false;
                    }
                } else {
                    doWait = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                doWait = false;
            }
        }

        log.debug("Script run ended!");

        return scriptResult;
    }

    public ScriptResult evalScript(ScriptEngine engine, String script, ServiceRequest request) {
        ScriptResult result = execWithThread(engine, script, false, request);
        return result;
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.SCRIPT_SERVICE_REQUEST)
    public void handleScriptServiceRequest(ServiceRequest request, ServiceResponse response) {
        String script = (String) request.getParameters()[0];
        String engineName = (String) request.getParameters()[1];
        log.debug("script: {}", script);
        log.debug("engineName: {}", engineName);
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(engineName);
        log.debug("engine: {}", engine);
        ScriptResult result;
        if (engine == null) {
            result = new ScriptResult();
            result.setScriptOutput("No engine found for: " + engineName);
            return;
        }
        if (script.startsWith("@")) {
            result = evalScriptFile(engine, script, request);
        } else {
            result = evalScript(engine, script, request);
        }
        response.setResponseData(request.getType().getResponseDataKey(), result);
    }

    private ScriptResult evalScriptFile(ScriptEngine engine, String script, ServiceRequest request) {
        String fileName = script.substring(1);
        String toRun = SCRIPT_DIR + "/" + fileName;
        File f = new File(toRun);
        if (!f.exists()) {
            return new ScriptResult("Script not found: " + f.getAbsolutePath());
        }
        ScriptResult result = execWithThread(engine, f.getAbsolutePath(), true, request);
        return result;
    }

}