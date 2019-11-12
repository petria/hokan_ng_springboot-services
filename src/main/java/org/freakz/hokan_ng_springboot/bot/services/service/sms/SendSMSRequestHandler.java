package org.freakz.hokan_ng_springboot.bot.services.service.sms;

import org.freakz.hokan_ng_springboot.bot.common.events.SendSMSRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSRequestHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SendSMSRequestHandler.class);

    private final SMSSenderService smsSenderService;

    @Autowired
    public SendSMSRequestHandler(SMSSenderService smsSenderService) {
        this.smsSenderService = smsSenderService;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.SEND_SMS_SERVICE_REQUEST)
    public void handleIrcChannelLogRequest(ServiceRequest request, ServiceResponse response) {
        SendSMSRequest smsRequest = (SendSMSRequest) request.getParameters()[0];
        String answer = smsSenderService.sendSMS("_Hokan_", smsRequest.getTarget(), smsRequest.getMessage());
        log.debug("SMS answer: {}", answer);
        if (answer != null) {
            response.setResponseData(request.getType().getResponseDataKey(), answer);
        }
    }

}
