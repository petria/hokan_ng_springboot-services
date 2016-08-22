package org.freakz.hokan_ng_springboot.bot.service;

import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchRequestHandler;
import org.freakz.hokan_ng_springboot.bot.service.lunch.requesthandlers.*;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

/*  @Test
  public void testQulkuri() {
    LunchRequestHandler lunchRequestHandler = new QulkuriLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_QULKURI, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_QULKURI, response.getLunchPlace());
  }*/

  @Test
  public void testFiilu() {
    LunchRequestHandler lunchRequestHandler = new FiiluLunchRequestHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_FIILU, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_FIILU, response.getLunchPlace());
  }

	@Test
	public void testVesilinna() {
		LunchRequestHandler lunchRequestHandler = new VesilinnaLunchPlaceHandler();
		LunchData response = new LunchData();
		lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_VESILINNA, response, DateTime.now());
		assertEquals(LunchPlace.LOUNAS_INFO_VESILINNA, response.getLunchPlace());
	}

/*  @Test
  public void testAlvari() {
    LunchRequestHandler lunchRequestHandler = new AlvariLunchPlaceHandler();
    LunchData response = new LunchData();
    lunchRequestHandler.handleLunchPlace(LunchPlace.LOUNAS_INFO_ALVARI, response, DateTime.now());
    assertEquals(LunchPlace.LOUNAS_INFO_ALVARI, response.getLunchPlace());
  }*/

}
