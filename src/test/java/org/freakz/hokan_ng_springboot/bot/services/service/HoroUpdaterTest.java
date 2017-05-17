package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.models.HoroHolder;
import org.freakz.hokan_ng_springboot.bot.services.updaters.horo.HoroUpdater;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Petri Airio on 22.2.2017.
 */
public class HoroUpdaterTest {

    @Test
    public void testHoroUpdater() throws Exception {

        HoroUpdater updater = new HoroUpdater() {
            @Override
            public Document getDocument() throws IOException {
                File input = new File("Horoskooppi.htm"); // saved 06.03.2017
                return Jsoup.parse(input, "ISO-8859-1");
            }
        };
        List<HoroHolder> hous = updater.updateIL();
        Assert.assertNotNull(hous);
        HoroHolder holder = hous.get(0);
        String oinasExpected = "OINAS 21.3.-19.4. Arki ei ole nyt sinua varten. Haaveilet jostakin unelmasta, joka liittyy matkailuun. Haluaisit olla omasta elämästäsi enemmän vastuussa, nyt jokin asia ei vain toimi.";
        Assert.assertEquals(oinasExpected, holder.getHoroscopeText());
    }

}