package org.freakz.hokan_ng_springboot.bot.service.alko;

import org.freakz.hokan_ng_springboot.bot.models.AlkoSearchResults;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 22/11/2016 / 8.54
 */
public interface AlkoSearchService {

    AlkoSearchResults alkoSearch(String search);

}
