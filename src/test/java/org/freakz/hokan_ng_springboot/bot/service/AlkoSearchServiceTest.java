package org.freakz.hokan_ng_springboot.bot.service;

import org.freakz.hokan_ng_springboot.bot.models.AlkoSearchResults;
import org.freakz.hokan_ng_springboot.bot.service.alko.AlkoSearchServiceRequestHandler;
import org.freakz.hokan_ng_springboot.bot.service.wwwfetcher.WWWPageFetcher;
import org.freakz.hokan_ng_springboot.bot.service.wwwfetcher.WWWPageFetcherImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Petri Airio (petri.airio@soikea.com) 22/11/2016 / 9.20
 */
public class AlkoSearchServiceTest {

    private WWWPageFetcher wwwPageFetcher = new WWWPageFetcherImpl();

    @Test
    public void testSearch() {
        AlkoSearchServiceRequestHandler searchService = new AlkoSearchServiceRequestHandler();
        searchService.setWwwPageFetcher(wwwPageFetcher);
        AlkoSearchResults alkoSearchResults = searchService.alkoSearch("black ipa");
        Assert.assertNotNull(alkoSearchResults.getResults());
        Assert.assertEquals(8, alkoSearchResults.getResults().size());

    }


}
