package org.freakz.hokan_ng_springboot.bot.service.lunch;

import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
public interface LunchService {

    LunchData getLunchForDay(LunchPlace place, DateTime day);

    List<LunchPlace> getLunchPlaces();

}
