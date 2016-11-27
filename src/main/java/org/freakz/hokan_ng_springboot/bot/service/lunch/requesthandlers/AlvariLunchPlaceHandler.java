package org.freakz.hokan_ng_springboot.bot.service.lunch.requesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.service.annotation.LunchPlaceHandler;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchRequestHandler;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Petri Airio on 14.4.2016.
 * -
 */
@Component
@Slf4j
public class AlvariLunchPlaceHandler implements LunchRequestHandler {

    private String fetchLunch() {
        String url = "http://fi-fi.facebook.com/Cafe-Alvar-278892385487649/";
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("fetch error", e);
            return null;
        }
        Elements test = doc.getElementsByClass("text_exposed_root");
        DateTime now = DateTime.now();
        String weekNow = now.getWeekOfWeekyear() + "";
        for (Element element : test) {
            String txt = element.text();
            if (txt.toLowerCase().contains("louna") && txt.contains(weekNow)) {
                return txt;
            }
        }
        return null;
    }

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_ALVARI)
    public void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, DateTime day) {
        response.setLunchPlace(lunchPlaceRequest);
        String menuText = fetchLunch();
        if (menuText != null) {
            parseMenu(menuText, response);
        }

    }

    private void parseMenu(String menuText, LunchData response) {
        int idx1;
        int idx2;
        String lunch;

        response.getMenu().put(LunchDay.MONDAY, new LunchMenu("Museo suljettu, ei lounasta."));

        idx1 = menuText.indexOf("Tiistaina:");
        idx2 = menuText.indexOf("Keskiviikkona:");
        lunch = menuText.substring(idx1, idx2).trim();

        LunchMenu lunchMenu = new LunchMenu(lunch);
        response.getMenu().put(LunchDay.TUESDAY, lunchMenu);

        idx1 = menuText.indexOf("Keskiviikkona:");
        idx2 = menuText.indexOf("Torstaina:");
        lunch = menuText.substring(idx1, idx2).trim();

        lunchMenu = new LunchMenu(lunch);
        response.getMenu().put(LunchDay.WEDNESDAY, lunchMenu);

        idx1 = menuText.indexOf("Torstaina:");
        idx2 = menuText.indexOf("Perjantaina:");
        lunch = menuText.substring(idx1, idx2).trim();

        lunchMenu = new LunchMenu(lunch);
        response.getMenu().put(LunchDay.THURSDAY, lunchMenu);

        idx1 = menuText.indexOf("Perjantaina:");
        lunch = menuText.substring(idx1).trim();

        lunchMenu = new LunchMenu(lunch);
        response.getMenu().put(LunchDay.FRIDAY, lunchMenu);

    }

}
