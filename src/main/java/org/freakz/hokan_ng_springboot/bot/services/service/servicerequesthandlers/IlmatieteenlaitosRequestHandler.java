package org.freakz.hokan_ng_springboot.bot.services.service.servicerequesthandlers;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.HourlyWeatherData;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Petri Airio on 11.8.2016.
 * -
 */
@Component
public class IlmatieteenlaitosRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(IlmatieteenlaitosRequestHandler.class);

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.ILMATIETEENLAITOS_HOURLY_REQUEST)
    public void handleHourlyRequest(ServiceRequest request, ServiceResponse response) {
        try {
            String place = (String) request.getParameters()[0];
            String url = String.format("http://ilmatieteenlaitos.fi/saa/%s?forecast=short", place);
            log.debug("Hourly forecast from: {}", url);
            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
            Elements times = doc.getElementsByClass("meteogram-times");
            Elements symbols = doc.getElementsByClass("meteogram-weather-symbols");
            Elements temperatures = doc.getElementsByClass("meteogram-temperatures");

            HourlyWeatherData hourly = new HourlyWeatherData();
            response.setResponseData(request.getType().getResponseDataKey(), hourly);
            String timez = times.get(0).text();
            String tempz = temperatures.get(0).text();
            String symbolz = symbols.get(0).text();
            hourly.setTimes(timez.split(" "));
            hourly.setTemperatures(tempz.split(" "));
            hourly.setSymbols(symbolz.split(" "));


        } catch (Exception e) {
            //
        }

    }

}
