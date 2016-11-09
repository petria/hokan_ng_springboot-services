package org.freakz.hokan_ng_springboot.bot.service.nimipaiva;

import org.freakz.hokan_ng_springboot.bot.models.NimipaivaData;
import org.joda.time.DateTime;

/**
 * Created by Petri Airio on 5.10.2015.
 */
public interface NimipaivaService {

    NimipaivaData getNamesForDay(DateTime day);

    NimipaivaData findDayForName(String name);

    void loadNames();

}
