package org.freakz.hokan_ng_springboot.bot.services.controller;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.services.service.lunch.LunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@Slf4j
@Transactional
public class ServicesRestController {


    private static final String BASE_PATH = "/rest/lunch_request";

    @Autowired
    private LunchService lunchService;

    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceRestResponse doLunchRequest(@RequestBody ServiceRestRequest serviceRestRequest) {
        ServiceRestResponse response = new ServiceRestResponse();

        String place = serviceRestRequest.getRequest();
        Set<LunchPlace> matchingLunchPlaces = LunchPlace.getMatchingLunchPlaces(place);
        if (matchingLunchPlaces.size() == 0) {
            response.setResponse("No matching lunch place found with: " + place);
            return response;
        }

        String responseString = "";
        for (LunchPlace lunchPlace : matchingLunchPlaces) {
            final LunchData lunchForDay = lunchService.getLunchForDay(lunchPlace, LocalDateTime.now());
            LunchDay lunchDay = LunchDay.getFromDateTime(LocalDateTime.now());
            final LunchMenu lunchMenu = lunchForDay.getMenu().get(lunchDay);
            responseString += String.format("%s :: %s", lunchPlace.getName(), lunchMenu.getMenu());
        }

        response.setResponse(responseString);

        return response;

    }


}