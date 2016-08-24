package org.freakz.hokan_ng_springboot.bot.service.lunch.requesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.service.annotation.LunchPlaceHandler;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchRequestHandler;
import org.freakz.hokan_ng_springboot.bot.service.wwwfetcher.WWWPageFetcher;
import org.freakz.hokan_ng_springboot.bot.service.wwwfetcher.WWWPageFetcherImpl;
import org.freakz.hokan_ng_springboot.bot.util.StringStuff;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by petria on 22/08/16.
 * -
 */
@Component
@Slf4j
public class FiiluLunchRequestHandler implements LunchRequestHandler {

  @Autowired
  private WWWPageFetcher wwwPageFetcher;

  private List<String> fetchLunch(String url) {
    String output;
    if (wwwPageFetcher == null) {
      // TODO fix
      output = WWWPageFetcherImpl.fetchUrl(url);
    } else {
      output = wwwPageFetcher.fetchWWWPage(url);
    }

    Document doc = Jsoup.parse(output);

    Elements e = doc.getElementsByTag("strong");
    List<String> rows = new ArrayList<>();

    for (Element element : e) {
      rows.add(element.parent().text());
    }
    return rows;
  }

  @Override
  @LunchPlaceHandler(LunchPlace = LunchPlace.LOUNAS_INFO_FIILU)
  public void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, DateTime day) {
    response.setLunchPlace(lunchPlaceRequest);
    List<String> menu = fetchLunch(lunchPlaceRequest.getUrl());
    parseMenu(menu, response);
  }

  private void parseMenu(List<String> menu, LunchData response) {
    boolean firstFound = false;
    int idx = 0;
    while (!firstFound) {
      String row = menu.get(idx);
      if (row.contains("Maanantai")) {
        firstFound = true;
        break;
      }
      idx++;
      if (idx == menu.size()) {
        break;
      }
    }
    if (firstFound) {
      idx++;
      String lunch = "";
      lunch += stripLunchRow(menu.get(idx++), "Grilli:");
      lunch += stripLunchRow(menu.get(idx++), " Buffet:");
      lunch += stripLunchRow(menu.get(idx++), " Keitto:");
      idx++;
      LunchMenu lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.MONDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += stripLunchRow(menu.get(idx++), "Grilli:");
      lunch += stripLunchRow(menu.get(idx++), " Buffet:");
      lunch += stripLunchRow(menu.get(idx++), " Keitto:");
      idx++;
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.TUESDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += stripLunchRow(menu.get(idx++), "Grilli:");
      lunch += stripLunchRow(menu.get(idx++), " Buffet:");
      lunch += stripLunchRow(menu.get(idx++), " Keitto:");
      idx++;
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.WEDNESDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += stripLunchRow(menu.get(idx++), "Grilli:");
      lunch += stripLunchRow(menu.get(idx++), " Buffet:");
      lunch += stripLunchRow(menu.get(idx++), " Keitto:");
      idx++;
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.THURSDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += stripLunchRow(menu.get(idx++), "Grilli:");
      lunch += stripLunchRow(menu.get(idx++), " Buffet:");
      lunch += stripLunchRow(menu.get(idx++), " Keitto:");
      idx++;
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.FRIDAY, lunchMenu);
    }

  }

  private String stripLunchRow(String row, String title) {
    String g = row.replaceAll("Street gourmet grilli ..... |Nordic Buffet .... |Päivän keittolounas .... ", "");
    String[] meals = g.split(" \\(.*?\\) ?");
    String tt = StringStuff.arrayToString(meals, ", ");

    return String.format("%s %s", title, tt);
  }

}
