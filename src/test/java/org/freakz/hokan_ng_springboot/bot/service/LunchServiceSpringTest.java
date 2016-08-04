package org.freakz.hokan_ng_springboot.bot.service;

import org.freakz.hokan_ng_springboot.bot.HokanNgSpringBootServices;
import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchServiceImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HokanNgSpringBootServices.class)

public class LunchServiceSpringTest {

  @Autowired
  private LunchServiceImpl lunchService;


  @Test
  public void testHarmooni() {
    LunchData data = lunchService.getLunchForDay(LunchPlace.LOUNAS_INFO_HARMOONI, DateTime.now());
    Assert.assertNotNull(data);
    Assert.assertEquals(LunchPlace.LOUNAS_INFO_HARMOONI, data.getLunchPlace());
  }

}
