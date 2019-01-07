package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsMessage;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.services.service.timeservice.TimeDifferenceService;
import org.freakz.hokan_ng_springboot.bot.services.service.timeservice.TimeDifferenceServiceImpl;
import org.freakz.hokan_ng_springboot.bot.services.service.wholelinetricker.WholeLineTriggersImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.Destination;
import javax.jms.ObjectMessage;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WholeLineTriggersTest {

    @Mock
    private IrcMessageEvent iEvent;

    @Mock
    private JmsSender jmsSender;

//    @Mock
//    private TimeDifferenceService timeDifferenceService;

    @Test
    public void testJouluTime() {

        WholeLineTriggersImpl wholeLineTriggers = new WholeLineTriggersImpl(null, null);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), 12, 25, 12, 0);

        LocalDateTime jouluTime = wholeLineTriggers.getJouluTime(start);
        System.out.printf("%s\n", jouluTime.toString());


    }

    @Test
    public void testJoulu() {

        when(iEvent.getSender()).thenReturn("s0meOne");
        when(iEvent.getMessage()).thenReturn("onks joulu joulu");

        TimeDifferenceService timeDifferenceService = new TimeDifferenceServiceImpl();
        WholeLineTriggersImpl wholeLineTriggers = new WholeLineTriggersImpl(jmsSender, timeDifferenceService);
        for (int i = 0; i < 100; i++) {
            wholeLineTriggers.checkJoulu(iEvent);
        }
    }

    @Test
    public void testDruks() {


        WholeLineTriggersImpl wholeLineTriggers = new WholeLineTriggersImpl(new JmsSender() {
            @Override
            public ObjectMessage sendAndGetReply(String destination, String key, Object object, boolean deliveryPersistent) {
                return null;
            }

            @Override
            public void send(HokanModule hokanModule, String destination, String key, Object object, boolean deliveryPersistent) {
                NotifyRequest request = (NotifyRequest) object;
                System.out.printf("notifyMessage: %s\n", request.getNotifyMessage());
            }

            @Override
            public void sendJmsMessage(Destination destination, JmsMessage jmsMessage) {

            }
        }, null);

        when(iEvent.getSender()).thenReturn("s0meOne");
        when(iEvent.getMessage()).thenReturn("onks huumeit ?");

        wholeLineTriggers.checkDrugs(iEvent);
        wholeLineTriggers.checkDrugs(iEvent);
        wholeLineTriggers.checkDrugs(iEvent);
    }


}
