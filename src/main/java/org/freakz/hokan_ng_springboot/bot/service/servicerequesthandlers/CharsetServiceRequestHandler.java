package org.freakz.hokan_ng_springboot.bot.service.servicerequesthandlers;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.models.SystemScriptResult;
import org.freakz.hokan_ng_springboot.bot.service.SystemScript;
import org.freakz.hokan_ng_springboot.bot.service.SystemScriptRunnerService;
import org.freakz.hokan_ng_springboot.bot.service.annotation.ServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Petri Airio on 29.3.2016. -
 */
@Component
@Slf4j
public class CharsetServiceRequestHandler {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private SystemScriptRunnerService systemScriptRunnerService;

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CHARSET_REQUEST)
    public void handleCharsetServiceRequest(ServiceRequest request, ServiceResponse response) {
        byte[] messageBytes = request.getIrcMessageEvent().getMessage().getBytes();
        try {
            String tmpFile = fileUtil.copyBytesToTmpFile(request.getIrcMessageEvent().getOriginal());
            // tmpFile = "D:\\TEMP\\bytes";
            SystemScriptResult result = systemScriptRunnerService.runAndGetResult(SystemScript.FILE_SCRIPT, tmpFile);
            response.setResponseData(request.getType().getResponseDataKey(), result.getOriginalOutput());
            int foo = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
