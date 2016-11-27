package org.freakz.hokan_ng_springboot.bot.service.imdb;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.common.models.IMDBSearchResults;
import org.springframework.stereotype.Service;

/**
 * Created by Petri Airio on 17.11.2015.
 * -
 */
@Service
@Slf4j
public class IMDBServiceImpl implements IMDBService {

    @Override
    public IMDBSearchResults findByTitle(String title) {
        return null;
    }

    @Override
    public IMDBDetails getDetailedInfo(String name) {
        return null;
    }
}
