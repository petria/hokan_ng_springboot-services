package org.freakz.hokan_ng_springboot.bot.services.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 11.5.2016.
 * -
 */
public class DayChangedServiceTest {

    @Test
    public void testGetSunsetText() {
        DayChangedService service = new DayChangedServiceImpl();
        List<String> cityList = new ArrayList<>();
        cityList.add("Turku");
        String texts = service.getSunriseTexts(cityList);
        Assert.assertTrue("Must have Sunset text", texts.contains("Sunset"));
    }

    @Test
    public void testGetSunrisesText() {
        DayChangedService service = new DayChangedServiceImpl();
        List<String> cityList = new ArrayList<>();
        cityList.add("Turku");
        String texts = service.getSunriseTexts(cityList);
        Assert.assertTrue("Must have Sunrise text", texts.contains("Sunrise"));
    }

    @Test
    public void testGetLengthOfDayText() {
        DayChangedService service = new DayChangedServiceImpl();
        List<String> cityList = new ArrayList<>();
        cityList.add("Turku");
        String texts = service.getSunriseTexts(cityList);
        Assert.assertTrue("Must have Day length text", texts.contains("Length of day"));
    }

}
