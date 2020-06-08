package org.freakz.hokan_ng_springbootservices.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;
import org.freakz.hokan_ng_springboot.bot.services.config.RuntimeConfig;
import org.freakz.hokan_ng_springboot.bot.services.service.distance.DistanceService;
import org.freakz.hokan_ng_springboot.bot.services.service.horo.HoroFetchServiceImpl;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderService;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderServiceImpl;
import org.freakz.hokan_ng_springboot.bot.services.service.timer.KoronaCheckService;
import org.freakz.hokan_ng_springboot.bot.services.service.timer.KoronaJSONReader;
import org.freakz.hokan_ng_springboot.bot.services.service.weather.ForecaWeatherRequestHandler;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class IntegrationTest {

    @Test
    public void testSMS() {
        RuntimeConfig config = new RuntimeConfig();
        SMSSenderService smsSenderService = new SMSSenderServiceImpl(config);
        smsSenderService.sendSMS("FROM", "12345", "Test message");
    }

    @Test
    public void testHoro() {
        HoroFetchServiceImpl horoFetchService = new HoroFetchServiceImpl();
        String horo = horoFetchService.getHoro("kakso");
    }

    private static Map<String, Integer> valueMap = new HashMap<>();

    static {
        valueMap.put("infected", 0);
        valueMap.put("healed", 0);
        valueMap.put("dead", 0);
    }

    @Test
    public void testJsonReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        KoronaJSONReader jsonReader = new KoronaJSONReader(objectMapper);
        Integer[] json = jsonReader.readKoronaJSON();
        int foo = 0;
    }

    //    @Test
    public void testCorona() {
        KoronaCheckService service = new KoronaCheckService(null, null, null);
        service.test();
    }


    @Test
    public void testDistanceService() {
      DistanceService sut = new DistanceService();
      String distance1 = sut.getDistance("Oulu", "Kempele");
      String distance2 = sut.getDistance("Oulu", "Turku");
      String distance3 = sut.getDistance("Oulu", "Lontoo");
      String distance4 = sut.getDistance("Oufdflu", "Lontoo");

      int foo = 0;
    }

  @Test
  public void testForecaWeather() {
    ForecaWeatherRequestHandler sut = new ForecaWeatherRequestHandler();
    ServiceRequest req = createServiceRequest(ServiceRequestType.FORECA_WEATHER_HOURLY_REQUEST, "!hsaa oulu", "oulu");
    ServiceResponse resp = new ServiceResponse(req.getType());
    sut.handleHourlyRequest(req, resp);
    int foo = 0;
  }

  private ServiceRequest createServiceRequest(ServiceRequestType type, String message, Object... parameters) {
    IrcMessageEvent ircEvent = new IrcMessageEvent();
    ircEvent.setMessage(message);
    ServiceRequest req = new ServiceRequest(type, ircEvent, new CommandArgs(ircEvent.getMessage()), parameters);
    return req;

  }

}
