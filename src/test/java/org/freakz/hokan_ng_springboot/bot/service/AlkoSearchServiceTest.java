package org.freakz.hokan_ng_springboot.bot.service;

import org.freakz.hokan_ng_springboot.bot.models.AlkoSearchResults;
import org.freakz.hokan_ng_springboot.bot.service.alko.AlkoSearchServiceRequestHandler;
import org.freakz.hokan_ng_springboot.bot.service.wwwfetcher.WWWPageFetcherImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 22/11/2016 / 9.20
 */
public class AlkoSearchServiceTest {

    private AlkoSearchServiceRequestHandler searchService;

    @Before
    public void setup() {
        searchService = new AlkoSearchServiceRequestHandler();
        searchService.setWwwPageFetcher(new WWWPageFetcherImpl());
    }

    @Test
    public void testSearch() {
        AlkoSearchResults alkoSearchResults = searchService.alkoSearch("apa");
        Assert.assertNotNull(alkoSearchResults.getResults());
        Assert.assertEquals(12, alkoSearchResults.getResults().size());
    }

    @Test
    public void testSearchNoVolumeForAllProducts() {
        AlkoSearchResults alkoSearchResults = searchService.alkoSearch("kalia");
        Assert.assertNotNull(alkoSearchResults.getResults());
    }


}
