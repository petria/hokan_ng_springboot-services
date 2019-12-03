package org.freakz.hokan_ng_springboot.bot.services.service.horo;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class HoroFetchServiceImpl implements HoroFetchService {

    public void setProxy(boolean useProxy) {
        String httpProxyHost = "";
        String httpProxyPort = "";
        String httpsProxyHost = "";
        String httpsProxyPort = "";
        String httpsProxyUser = "";
        String httpsProxyPassword = "";
        if (useProxy) {
            httpProxyHost = "fi.client-swg.oneadr.net";
            httpProxyPort = "8080";
            httpsProxyHost = "fi.client-swg.oneadr.net";
            httpsProxyPort = "8080";
            httpsProxyUser = "Z003322";
            httpsProxyPassword = "Dii69Paa";

        }
        System.setProperty("http.proxyHost", httpProxyHost);
        System.setProperty("http.proxyPort", httpProxyPort);

        System.setProperty("https.proxyHost", httpsProxyHost);
        System.setProperty("https.proxyPort", httpsProxyPort);
        System.setProperty("https.proxyUser", httpsProxyUser);
        System.setProperty("https.proxyPassword", httpsProxyPassword);
    }

    String[] days = {"", "maanantai", "tiistai", "keskiviikko", "torstai", "perjantai", "launtai", "sunnuntai"};

    private String fetchHoroData(String horo) throws IOException {
        LocalDate now = LocalDate.now();
        int day = now.getDayOfMonth();
        int month = now.getMonth().getValue();
        String weekday = days[now.getDayOfWeek().getValue()];
        String url = String.format("https://anna.fi/horoskoopit/paivahoroskoopit/paivan-horoskooppi-%s-%d-%d", weekday, day, month);
        log.debug("Horo url: {}", url);
        setProxy(true);
        Document document = Jsoup.connect(url).get();
        Elements horoTitles = document.getElementsByClass("content__body").select("h2");
        Elements horoTexts = document.getElementsByClass("content__body").select("p");
        for (int i = 0; i < 12; i++) {
            String title = horoTitles.get(i).text();
            String text = horoTexts.get(i).text();
            System.out.printf("%s : %s\n", title, text);
            if (title.toLowerCase().contains(horo.toLowerCase())) {
                return title + " :: " + text;
            }
        }
        // https://anna.fi/horoskoopit/paivahoroskoopit/paivan-horoskooppi-perjantai-1-11
        return null;
    }


    @Override
    public String getHoro(String horo) {
        try {
            return fetchHoroData(horo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
