package org.freakz.hokan_ng_springboot.bot.services.jms;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.SpringJmsReceiver;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by petria on 5.2.2015.
 * -
 */
@Component
@Slf4j
public class ServicesJmsReceiver extends SpringJmsReceiver {

    private final JmsSender jmsSender;

    private final JmsServiceMessageHandler jmsServiceMessageHandler;

    @Autowired
    public ServicesJmsReceiver(JmsSender jmsSender, JmsServiceMessageHandler jmsServiceMessageHandler) {
        this.jmsSender = jmsSender;
        this.jmsServiceMessageHandler = jmsServiceMessageHandler;
    }


    @Override
    public String getDestinationName() {
        return "HokanNGServicesQueue";
    }


    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        jmsServiceMessageHandler.handleJmsEnvelope(envelope);
    }

}
