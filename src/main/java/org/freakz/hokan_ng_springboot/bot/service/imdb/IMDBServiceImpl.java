package org.freakz.hokan_ng_springboot.bot.service.imdb;


import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.models.IMDBSearchResults;
import org.springframework.stereotype.Service;

/**
 * Created by Petri Airio on 17.11.2015.
 * -
 */
@Service
@Slf4j
public class IMDBServiceImpl implements IMDBService {

//  private static final OmdbApi omdb = new OmdbApi();

  private static final int IMDB_ID = 0;
  private static final int IMDB_TITLE= 1;


  public String parseSceneMovieName(String sceneName) {
    if (sceneName == null) {
      return null;
    }
    String[] parts = sceneName.split("\\.");
    String name = "";
    for (String part : parts) {
      if (part.toLowerCase().matches("\\d\\d\\d\\d|s\\d\\d?e\\d\\d?|\\d\\d?x\\d\\d?|\\d\\d\\d\\d?p")) {
        return name;
      }
      if (name.length() > 0) {
        name += " ";
      }
      name += part;
    }
    return null;
  }

  public IMDBSearchResults findByTitle(String title) {
/*    try {
      String parsed = parseSceneMovieName(title);
      if (parsed != null) {
        log.debug("Using parsed name: {}", parsed);
        title = parsed;
      }
      SearchResults results = omdb.search(title);
      return new IMDBSearchResults(results.getResults());
    } catch (OMDBException e) {
      e.printStackTrace();
    }*/
    // TODO
    return null;
  }

  @Override
  public IMDBDetails getDetailedInfo(String name) {
    int mode = -1;
    String imdbIdSearch = null;
    String titleSearch = null;
    if (name.matches("tt\\d{7}")) {
      mode = IMDB_ID;
      imdbIdSearch = name;
    } else  if (name.toLowerCase().matches("http://www.imdb.com/title/tt\\d{7}/")) {
      mode = IMDB_ID;
      String[] split = name.split("/");
      imdbIdSearch = split[split.length - 1];
    } else {
      mode = IMDB_TITLE;
      String parsed = parseSceneMovieName(name);
      if (parsed != null) {
        titleSearch = parsed;
      } else {
        titleSearch = name;
      }
    }

    switch (mode) {
      case IMDB_ID:
/*        try {
        OmdbVideoFull info = null;
        try {
          info = omdb.getInfo(new OmdbBuilder().setImdbId(imdbIdSearch).build());
        } catch (OMDBException e) {
          e.printStackTrace();
        }
        return new IMDBDetails(info);
        } catch (OMDBException e) {
         // e.printStackTrace();
        }
        */
        break;
      case IMDB_TITLE:
/*        OmdbVideoFull info = omdb.getInfo(new OmdbBuilder().setTitle(titleSearch).build());


        return new IMDBDetails(info); */
    }

    return new IMDBDetails();
  }
}
