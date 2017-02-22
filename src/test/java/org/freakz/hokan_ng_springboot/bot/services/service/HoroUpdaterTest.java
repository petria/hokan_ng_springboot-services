package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.models.HoroHolder;
import org.freakz.hokan_ng_springboot.bot.services.updaters.horo.HoroUpdater;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Petri Airio on 22.2.2017.
 */
public class HoroUpdaterTest {

    @Test
    public void testHoroUpdater() throws Exception {

        HoroUpdater updater = new HoroUpdater();
        List<HoroHolder> hous = updater.updateIL();
        Assert.assertNotNull(hous);
        HoroHolder holder = hous.get(0);
        String oinasExpected = "Oinas 21.3.-19.4. Tapaat sattumalta vanhan tuttusi. Huomaat teillä olevan vieläkin paljon yhteistä, vaikka ette ole pitkään aikaan toisianne tavanneetkaan. Juttua ainakin riittää.";
        Assert.assertEquals(oinasExpected, holder.getHoroscopeText());
    }

}
