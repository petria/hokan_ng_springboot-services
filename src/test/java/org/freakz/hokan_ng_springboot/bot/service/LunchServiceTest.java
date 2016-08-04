package org.freakz.hokan_ng_springboot.bot.service;

import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchRequestHandler;
import org.freakz.hokan_ng_springboot.bot.service.lunch.requesthandlers.*;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Petri Airio on 25.1.2016.
 * -
 */
public class LunchServiceTest {

  @Test
  public void testHarmooniFetch() {
    LunchRequestHandler lunchRequestHandler  = new HarmooniLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_HARMOONI, response, DateTime.now().minusDays(1));
    assertEquals(LunchPlace.LOUNAS_INFO_HARMOONI, response.getLunchPlace());
  }

  @Test
  public void testHelsinkiTerminaali2() {
    LunchRequestHandler lunchRequestHandler  = new HelsinkiTerminaali2RequestHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_HKI_TERMINAALI2, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_HKI_TERMINAALI2, response.getLunchPlace());

  }

  @Test
  public void testHerkkupiste() {
    LunchRequestHandler lunchRequestHandler = new HerkkupisteLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_HERKKUPISTE, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_HERKKUPISTE, response.getLunchPlace());
  }

  @Test
  public void testQulkuri() {
    LunchRequestHandler lunchRequestHandler = new QulkuriLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_QULKURI, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_QULKURI, response.getLunchPlace());
  }

  @Test
  public void testEnergiaKeidas() {
    LunchRequestHandler lunchRequestHandler = new EnergiaKeidasLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_ENERGIA_KEIDAS, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_ENERGIA_KEIDAS, response.getLunchPlace());
  }

  @Test
  public void testEnergiaKeidasMenuList() {
    EnergiaKeidasLunchPlaceHandler lunchRequestHandler = new EnergiaKeidasLunchPlaceHandler();
    List<String> menuLists = lunchRequestHandler.getWeeklyMenuUrls();
    assertNotNull(menuLists);
  }

  @Test
  public void testEnergiaKeidasIsUrlThisWeek() {
    String url = "http://acloud.bukkake.fi/~petria/ruokalistat/Rlista%20vko%2015%20EN-16.doc";
    EnergiaKeidasLunchPlaceHandler lunchRequestHandler = new EnergiaKeidasLunchPlaceHandler();
    int weekNow = 15;
    boolean b1 = lunchRequestHandler.isMenuThisWeek(url, weekNow);
    assertTrue(b1);

    int weekNotNow = 14;
    boolean b2 = lunchRequestHandler.isMenuThisWeek(url, weekNotNow);
    assertFalse(b2);

  }

	@Test
	public void testVesilinna() {
		LunchRequestHandler lunchRequestHandler = new VesilinnaLunchPlaceHandler();
		LunchData response = new LunchData();
		lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_VESILINNA, response, DateTime.now());
		assertEquals(LunchPlace.LOUNAS_INFO_VESILINNA, response.getLunchPlace());
	}

  @Test
  public void testAlvari() {
    LunchRequestHandler lunchRequestHandler = new AlvariLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_ALVARI, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_ALVARI, response.getLunchPlace());
  }

}
