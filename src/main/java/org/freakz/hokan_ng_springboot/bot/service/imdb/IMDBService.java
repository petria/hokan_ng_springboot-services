package org.freakz.hokan_ng_springboot.bot.service.imdb;


import org.freakz.hokan_ng_springboot.bot.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.models.IMDBSearchResults;

/**
 * Created by Petri Airio on 17.11.2015.
 * -
 */
public interface IMDBService {

    IMDBSearchResults findByTitle(String title);

    IMDBDetails getDetailedInfo(String name);

}
