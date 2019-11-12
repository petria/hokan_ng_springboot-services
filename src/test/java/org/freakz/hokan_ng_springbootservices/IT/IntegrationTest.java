package org.freakz.hokan_ng_springbootservices.IT;

import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderService;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderServiceImpl;
import org.junit.jupiter.api.Test;

public class IntegrationTest {

    @Test
    public void testSMS() {
        SMSSenderService smsSenderService = new SMSSenderServiceImpl();
        smsSenderService.sendSMS("FROM", "12345", "Test message");
    }

}
