package org.freakz.hokan_ng_springboot.bot.services.service.topcounter;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.*;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.DataValuesService;
import org.freakz.hokan_ng_springboot.bot.common.models.DataValuesModel;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;


@Service
@Slf4j
public class TopCountService {

    private final DataValuesService dataValuesService;
    private final JmsSender jmsSender;

    @Autowired
    public TopCountService(DataValuesService dataValuesService, JmsSender jmsSender) {
        this.dataValuesService = dataValuesService;
        this.jmsSender = jmsSender;
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.TOP_COUNT_REQUEST)
    public void calculateTopCounters(ServiceRequest request, ServiceResponse response) {

        if (doCalc(request, ".*(\\*glugga\\*|\\*glug\\*).*", "GLUGGA_COUNT")) {
            handleLastGluggaTime(request);
        }
        doCalc(request, "\\s*(\\*korina*\\*|\\*kuo*len\\*|\\*tappakaa\\*).*", "KORINA_COUNT");
        doCalc(request, "puuh", "PUUH_COUNT");
        doCalc(request, ".*(kaleeri).*", "KALEERI_COUNT");
        doCalc(request, ".*(jäätävä).*", "JÄÄTÄVÄ_COUNT");

    }

    private void handleLastGluggaTime(ServiceRequest request) {
        String nick = request.getIrcMessageEvent().getSender().toLowerCase();
        String channel = request.getIrcMessageEvent().getChannel().toLowerCase();
        String network = request.getIrcMessageEvent().getNetwork().toLowerCase();
        String key = String.format("%s_LAST_GLUGGA", nick.toUpperCase());
        dataValuesService.setValue(nick, channel, network, key, System.currentTimeMillis() + "");
    }

    private boolean doCalc(ServiceRequest request, String regex, String key) {
        String message = request.getIrcMessageEvent().getMessage().toLowerCase();
        if (message.matches(regex)) {
            String nick = request.getIrcMessageEvent().getSender().toLowerCase();
            String channel = request.getIrcMessageEvent().getChannel().toLowerCase();
            String network = request.getIrcMessageEvent().getNetwork().toLowerCase();

            log.debug("Got  {} from: {}", key, nick);

            String value = dataValuesService.getValue(nick, channel, network, key);
            if (value == null) {
                value = "1";
            } else {
                int count = Integer.parseInt(value);
                count++;
                value = "" + count;
            }

            int oldPos = getNickPosition(channel, network, key, nick);

            log.debug("{} {} count: {}", key, nick, value);
            dataValuesService.setValue(nick, channel, network, key, value);

            int newPos = getNickPosition(channel, network, key, nick);

            log.debug("key: {} - oldPos: {} <-> newPos {}", key, oldPos, newPos);
            if (oldPos != -1 && newPos != -1) {
                if (oldPos != newPos) {
                    IrcMessageEvent iEvent = request.getIrcMessageEvent();
                    String positionChange = String.format("%s: %d. -> %d. !!", key, oldPos, newPos);
                    processReply(iEvent, iEvent.getSender() + ": " + positionChange);
                }
            }
            return true;
        }
        return false;
    }

    private void processReply(IrcMessageEvent iEvent, String reply) {

        NotifyRequest notifyRequest = new NotifyRequest();
        notifyRequest.setNotifyMessage(reply);
        notifyRequest.setTargetChannelId(iEvent.getChannelId());
        jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
    }

    private int getNickPosition(String channel, String network, String key, String nick) {
        List<DataValuesModel> dataValues = dataValuesService.getDataValues(channel, network, key);
        if (dataValues.size() > 0) {
            Comparator<? super DataValuesModel> comparator = (Comparator<DataValuesModel>) (o1, o2) -> {
                Integer i1 = Integer.parseInt(o1.getValue());
                Integer i2 = Integer.parseInt(o2.getValue());
                return i2.compareTo(i1);
            };
            dataValues.sort(comparator);
            int c = 1;
            for (DataValuesModel model : dataValues) {
                if (model.getNick().equalsIgnoreCase(nick)) {
                    return c;
                }
                c++;
            }
        }
        return -1;
    }

}
