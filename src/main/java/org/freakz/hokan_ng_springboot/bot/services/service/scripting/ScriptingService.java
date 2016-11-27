package org.freakz.hokan_ng_springboot.bot.services.service.scripting;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.models.ScriptResult;

/**
 * Created by Petri Airio on 5.4.2016.
 * -
 */
public interface ScriptingService {

    ScriptResult evalScript(String script, ServiceRequest request);

}
