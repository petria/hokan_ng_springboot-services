package org.freakz.hokan_ng_springboot.bot.services.service.lunch.requesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.LunchPlaceHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.lunch.LunchRequestHandler;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 2.2.2016.
 * -
 */
@Component
@Slf4j
public class HelsinkiTerminaali2RequestHandler implements LunchRequestHandler {


    private static final String BASE_ULR = "http://www.sspfinland.fi/";
    private static final String[] DAYS = {"MAANANTAI", "TIISTAI", "KESKIVIIKKO", "TORSTAI", "PERJANTAI", "LAUANTAI", "SUNNUNTAI", "HINNASTO"};

    private List<String> getWeeklyPDFMenuUrls(String url) {
        List<String> urlList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).timeout(1000 * 10).get();
            Elements links = doc.select("a[href]").select(":contains(Lounaslista viikolle)");
            for (Element href : links) {
                String hrefUrl = BASE_ULR + href.attributes().get("href");
                urlList.add(hrefUrl);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return urlList;
    }


    private String readPDFMenu(String pdfUrl) {
        Parser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        BodyContentHandler handler = new BodyContentHandler(1000000);
        InputStream is;
        try {
            URL oracle = new URL(pdfUrl);
            is = oracle.openStream();
            parser.parse(is, handler, metadata, context);
            is.close();
            return handler.toString();

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_HKI_TERMINAALI2)
    public void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, DateTime day) {
        response.setLunchPlace(lunchPlaceRequest);
        List<String> urlList = getWeeklyPDFMenuUrls(lunchPlaceRequest.getUrl());
        for (String url : urlList) {
            String menu = readPDFMenu(url);
            if (menu != null) {
                if (isMenuThisWeek(menu)) {
                    parseMenu(menu, response);
                }
            }
        }
    }

    private boolean isMenuThisWeek(String menuText) {
        String[] lines = menuText.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains(DAYS[0].toLowerCase())) {
                // "MAANANTAI 1.2 "
                String day = line.split(" ")[1].split("\\.")[0];
                String month = line.split(" ")[1].split("\\.")[1];
                int dayOfMonth = Integer.valueOf(day);
                int monthOfYear = Integer.valueOf(month);
                DateTime now = DateTime.now();
                DateTime parsed = now.withDayOfMonth(dayOfMonth).withMonthOfYear(monthOfYear);
                int week1 = now.getWeekOfWeekyear();
                int week2 = parsed.getWeekOfWeekyear();
                return week1 == week2;
            }
        }
        return false;
    }

    private void parseMenu(String menuText, LunchData response) {
        String[] days = DAYS;
        String[] lines = menuText.split("\n");
        int dayIdx = 0;
        int lineIdx = 0;
        while (lineIdx < lines.length) {
            String line = lines[lineIdx];
            lineIdx++;
            if (line.startsWith(days[dayIdx])) {
                LunchDay lunchDay = LunchDay.getFromWeekdayString(days[dayIdx]);
                dayIdx++;
                if (dayIdx == 7) {
                    break;
                }
                String lunchForDay = "";
                while (true) {
                    line = lines[lineIdx].trim();
                    lineIdx++;
                    if (line.contains(days[dayIdx])) {
                        LunchMenu lunchMenu = new LunchMenu(lunchForDay);
                        response.getMenu().put(lunchDay, lunchMenu);
                        lineIdx--;
                        break;
                    } else {
                        if (line.length() > 0) {
                            lunchForDay += line + "  ";
                        }
                    }
                }
            }
        }
    }

}
