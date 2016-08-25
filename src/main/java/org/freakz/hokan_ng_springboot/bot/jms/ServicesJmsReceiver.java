package org.freakz.hokan_ng_springboot.bot.jms;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.jms.api.JmsServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by petria on 5.2.2015.
 */
@Component
@Slf4j
public class ServicesJmsReceiver extends SpringJmsReceiver {

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private JmsServiceMessageHandler jmsServiceMessageHandler;


    @Override
    public String getDestinationName() {
        return "HokanNGServicesQueue";
    }


    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        jmsServiceMessageHandler.handleJmsEnvelope(envelope);
    }

}
