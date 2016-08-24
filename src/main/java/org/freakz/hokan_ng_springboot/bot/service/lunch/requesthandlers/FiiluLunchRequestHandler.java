package org.freakz.hokan_ng_springboot.bot.service.lunch.requesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.service.annotation.LunchPlaceHandler;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchRequestHandler;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
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

  private List<String> fetchLunch(String url) {
    WebDriver driver = new HtmlUnitDriver(true); //the param true turns on javascript.
    driver.get(url);
    String output = driver.getPageSource();
    driver.quit();
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
      lunch += menu.get(idx++);
      lunch += " " + menu.get(idx++);
      lunch += " " + menu.get(idx++);
      idx++;
      lunch = beautifyLunch(lunch);
      LunchMenu lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.MONDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += menu.get(idx++);
      lunch += " " + menu.get(idx++);
      lunch += " " + menu.get(idx++);
      idx++;
      lunch = beautifyLunch(lunch);
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.TUESDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += menu.get(idx++);
      lunch += " " + menu.get(idx++);
      lunch += " " + menu.get(idx++);
      idx++;
      lunch = beautifyLunch(lunch);
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.WEDNESDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += menu.get(idx++);
      lunch += " " + menu.get(idx++);
      lunch += " " + menu.get(idx++);
      idx++;
      lunch = beautifyLunch(lunch);
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.THURSDAY, lunchMenu);

      idx++;
      lunch = "";
      lunch += menu.get(idx++);
      lunch += " " + menu.get(idx++);
      lunch += " " + menu.get(idx++);
      idx++;
      lunch = beautifyLunch(lunch);
      lunchMenu = new LunchMenu(lunch);
      response.getMenu().put(LunchDay.FRIDAY, lunchMenu);


      int foo = 0;
    }
  }

  private String beautifyLunch(String lunch) {
    lunch = lunch.replaceFirst("Street gourmet grilli .....", "Grilli: ");
    lunch = lunch.replaceFirst("Nordic Buffet ....", "Buffet: ");
    lunch = lunch.replaceFirst("Päivän keittolounas ....", "Keitto: ");
    return lunch;
  }

}
