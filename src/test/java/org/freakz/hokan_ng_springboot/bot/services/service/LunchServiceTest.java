package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.services.service.lunch.LunchRequestHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.lunch.requesthandlers.*;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by Petri Airio on 25.1.2016.
 * -
 */
public class LunchServiceTest {

    @Test
    public void testHarmooniFetch() {
        LunchRequestHandler lunchRequestHandler = new HarmooniLunchPlaceHandler();
        LunchData response = new LunchData();
        lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_HARMOONI, response, LocalDateTime.now().minusDays(1));
        assertEquals(LunchPlace.LOUNAS_INFO_HARMOONI, response.getLunchPlace());
    }

    @Test
    public void testHelsinkiTerminaali2() {
        LunchRequestHandler lunchRequestHandler = new HelsinkiTerminaali2RequestHandler();
        LunchData response = new LunchData();
        lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_HKI_TERMINAALI2, response, LocalDateTime.now());
        assertEquals(LunchPlace.LOUNAS_INFO_HKI_TERMINAALI2, response.getLunchPlace());

    }

    @Test
    public void testHerkkupiste() {
        LunchRequestHandler lunchRequestHandler = new HerkkupisteLunchPlaceHandler();
        LunchData response = new LunchData();
        lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_HERKKUPISTE, response, LocalDateTime.now());
        assertEquals(LunchPlace.LOUNAS_INFO_HERKKUPISTE, response.getLunchPlace());
    }

/*  @Test
  public void testQulkuri() {
    LunchRequestHandler lunchRequestHandler = new QulkuriLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_QULKURI, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_QULKURI, response.getLunchPlace());
  }*/

    //    @Test
    public void testFiilu() {
        LunchRequestHandler lunchRequestHandler = new FiiluLunchRequestHandler();
        LunchData response = new LunchData();
        lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_FIILU, response, LocalDateTime.now());
        assertEquals(LunchPlace.LOUNAS_INFO_FIILU, response.getLunchPlace());
    }

    @Test
    public void testVesilinna() {
        LunchRequestHandler lunchRequestHandler = new VesilinnaLunchPlaceHandler();
        LunchData response = new LunchData();
        lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_VESILINNA, response, LocalDateTime.now());
        assertEquals(LunchPlace.LOUNAS_INFO_VESILINNA, response.getLunchPlace());
    }

/*  @Test
  public void testAlvari() {
    LunchRequestHandler lunchRequestHandler = new AlvariLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_ALVARI, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_ALVARI, response.getLunchPlace());
  }*/

    private String stripLunchRow(String row, String title) {
        String g = row.replaceAll("Street gourmet grilli ..... |Nordic Buffet .... |Päivän keittolounas .... ", "");
        String[] meals = g.split(" \\(.*?\\) ?");
        String tt = StringStuff.arrayToString(meals, ", ");

        return String.format("%s %s", title, tt);
    }

    @Test
    public void testF11luParse() {
        String grill = "Street gourmet grilli 10,20 Juustolla ja paprikalla täytetty broilerinfilee (A, G, VL)   Bearnaisekastiketta (A, G, L)   Pähkinäistä täysjyvävehnää (A, L, M) Pulled pork hot dog (A, VL)";
        String buffet = "Nordic Buffet 9,60 Jauhelihapyöryköitä (A, L, M)   Ruskeaa kastiketta (A, L, M) Tatti-pinaattikiusausta (A, G, L, Veg)";
        String soup = "Päivän keittolounas 7,20 Broileri-kookoskeittoa (A, L, M, VS)";

//    String ff = stripLunchRow(grill, "Grilli:");
//    String ff = stripLunchRow(buffet, "Buffet:");
        String ff = stripLunchRow(soup, "Keitto:");

        int foo = 0;

    }

}
