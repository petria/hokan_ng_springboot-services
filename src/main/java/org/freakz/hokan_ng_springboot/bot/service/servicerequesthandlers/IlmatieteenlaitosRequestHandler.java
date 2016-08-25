package org.freakz.hokan_ng_springboot.bot.service.servicerequesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.models.HourlyWeatherData;
import org.freakz.hokan_ng_springboot.bot.service.annotation.ServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.util.StaticStrings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * Created by Petri Airio on 11.8.2016.
 * -
 */
@Component
@Slf4j
public class IlmatieteenlaitosRequestHandler {

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
