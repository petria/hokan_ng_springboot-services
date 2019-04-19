package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.services.service.locations.LocationsService;
import org.freakz.hokan_ng_springboot.bot.services.service.locations.LocationsServiceImpl;
import org.junit.Assert;

public class LocationsServiceTest {

    private final int expected = 3173959;

    //@Test
    public void testFetchLocations() {
        LocationsService sut = new LocationsServiceImpl();
        int count = sut.fetchLocations();

        Assert.assertEquals("Should have " + expected + " locations", expected, count);
    }

}
