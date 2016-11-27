package org.freakz.hokan_ng_springboot.bot.service.lunch.requesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.service.annotation.LunchPlaceHandler;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchRequestHandler;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Petri Airio on 2.3.2016.
 * -
 */
@Component
@Slf4j
public class QulkuriLunchPlaceHandler implements LunchRequestHandler {

    @Override
    @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_QULKURI)
    public void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, DateTime day) {
        response.setLunchPlace(lunchPlaceRequest);
        String url = lunchPlaceRequest.getUrl();
        Document doc;
        try {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("foo", "bar".toCharArray());
                }
            });
            doc = Jsoup.connect(url).timeout(0).userAgent(StaticStrings.HTTP_USER_AGENT).get();
        } catch (IOException e) {
            log.error("Could not fetch lunch from {}", url, e);
            return;
        }
        Elements elements = doc.getElementsByClass("art-postcontent");
        Elements days = elements.select("h4[style=text-align: center;]");
        Elements lunches = elements.select("p[style=text-align: center;]");
        for (int xx = 0; xx < 5; xx++) {
            LunchDay lunchDay = LunchDay.getFromWeekdayString(days.get(xx).text());
            LunchMenu lunchMenu = new LunchMenu(lunches.get(xx).text().replace(" – – – – – – Tai – – – – – –", ","));
            response.getMenu().put(lunchDay, lunchMenu);
        }
    }

}
