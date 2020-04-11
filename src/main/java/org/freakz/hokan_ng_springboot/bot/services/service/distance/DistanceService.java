package org.freakz.hokan_ng_springboot.bot.services.service.distance;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class DistanceService {


    public String getDistance(String city1, String city2) {
        String url = String.format("https://www.vaelimatka.org/%s/%s", city1, city2);

        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            Elements element = doc.getElementsByClass("caption panel-footer");
            return element.text();

        } catch (IOException e) {
            log.error("Korona data fetch failed", e);
        }
        return null;
    }

}
