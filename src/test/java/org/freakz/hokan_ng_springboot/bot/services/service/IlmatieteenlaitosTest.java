package org.freakz.hokan_ng_springboot.bot.services.service;


import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by Petri Airio on 12.5.2015.
 * -
 */
public class IlmatieteenlaitosTest {


    public void testShortForecast() throws IOException {

        Document doc = Jsoup.connect("http://ilmatieteenlaitos.fi/saa/jyv%C3%A4skyl%C3%A4?forecast=short").userAgent(StaticStrings.HTTP_USER_AGENT).get();
        Elements times = doc.getElementsByClass("meteogram-times");
        Elements symbols = doc.getElementsByClass("meteogram-weather-symbols");
        Elements temperatures = doc.getElementsByClass("meteogram-temperatures");

        String timez = times.get(0).text();
        String temps = temperatures.get(0).text();
        int foo = 0;

    }

    public void testAmppari() throws IOException {


        String line = "\"Hollywood-tähti Channing Tatum esittelee lihaksiaan suihkussa ilman rihman kiertämää - kuva sai julkkikset sekaisin: \"Rikoit juuri internetin\"\"";

        if (line.matches("\".*\"")) {

            System.out.println("Search   : " + line + "");

            String encode = URLEncoder.encode(line, "UTF-8");

            String url = "https://www.ampparit.com/haku?q=" + encode;

            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
            Elements elementsByClass = doc.getElementsByClass("news-item-headline");
            for (Element sub : elementsByClass) {
                String text = sub.text();
                if (text.equals(line)) {
                    Attributes attributes = sub.attributes();
                    String href = attributes.get("href");
                    System.out.println("Found Url: " + href);
                    int foo = 0;
                }
            }
        }

    }

    public static void main(String[] args) throws Exception {
        IlmatieteenlaitosTest instance = new IlmatieteenlaitosTest();
        instance.testAmppari();
//        instance.testShortForecast();

/*    Document doc = Jsoup.connect("http://en.ilmatieteenlaitos.fi/weather/sastamala").userAgent(StaticStrings.HTTP_USER_AGENT).get();
    Elements elements = doc.getElementsByClass("time-stamp");
    Element data = elements.get(0);
    String txt = data.text();

    Elements values = doc.getElementsByClass("parameter-value");
    Elements names = doc.getElementsByClass("parameter-name");
    int i = 0;
    StringBuilder sb = new StringBuilder();
    for (Element name : names) {
      sb.append(name.text() + " :: " + values.get(i).text() + "  ");
      i++;
    }
    int foo = 0;
    System.out.println(sb.toString());*/
    }

}
