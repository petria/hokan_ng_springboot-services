package org.freakz.hokan_ng_springboot.bot.service;


import org.freakz.hokan_ng_springboot.bot.util.StaticStrings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Petri Airio on 12.5.2015.
 *
 */
public class IlmatieteenlaitosTest {


  public static void main(String[] args) throws Exception {

    Document doc = Jsoup.connect("http://en.ilmatieteenlaitos.fi/weather/sastamala").userAgent(StaticStrings.HTTP_USER_AGENT).get();
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
    System.out.println(sb.toString());
  }

}
