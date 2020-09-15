package org.freakz.hokan_ng_springboot.bot.services.service.topcounter;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.DataValuesService;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TopCountService {

    private final DataValuesService dataValuesService;

    @Autowired
    public TopCountService(DataValuesService dataValuesService) {
        this.dataValuesService = dataValuesService;
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
            log.debug("{} {} count: {}", key, nick, value);
            dataValuesService.setValue(nick, channel, network, key, value);
            return true;
        }
        return false;
    }

}
