package org.freakz.hokan_ng_springbootservices.IT;

import org.freakz.hokan_ng_springboot.bot.services.config.RuntimeConfig;
import org.freakz.hokan_ng_springboot.bot.services.service.horo.HoroFetchServiceImpl;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderService;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderServiceImpl;
import org.junit.jupiter.api.Test;

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
        int foo = 0;
    }


}
