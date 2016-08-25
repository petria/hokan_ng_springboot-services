package org.freakz.hokan_ng_springboot.bot.service.scripting;

import org.freakz.hokan_ng_springboot.bot.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.models.ScriptResult;

/**
 * Created by Petri Airio on 5.4.2016.
 * -
 */
public interface ScriptingService {

    ScriptResult evalScript(String script, ServiceRequest request);

}
