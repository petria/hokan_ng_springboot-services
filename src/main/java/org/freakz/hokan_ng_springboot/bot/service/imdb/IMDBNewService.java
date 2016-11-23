package org.freakz.hokan_ng_springboot.bot.service.imdb;

import org.freakz.hokan_ng_springboot.bot.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.models.IMDBSearchResults;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 14/11/2016 / 14.52
 */
public interface IMDBNewService {


    String parseSceneMovieName(String sceneName);

    IMDBSearchResults imdbSearch(String title);

    IMDBDetails getDetailedInfo(String name);

}
