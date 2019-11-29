package org.freakz.hokan_ng_springboot.bot.services.service.horo;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class GetHoroRequestHandler {


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.HORO_REQUEST)
    public void handleHoroRequest(ServiceRequest request, ServiceResponse response) {

    }
}
