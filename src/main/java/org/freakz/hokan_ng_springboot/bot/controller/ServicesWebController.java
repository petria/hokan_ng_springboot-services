package org.freakz.hokan_ng_springboot.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.enums.LunchDay;
import org.freakz.hokan_ng_springboot.bot.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.events.IrcEventFactory;
import org.freakz.hokan_ng_springboot.bot.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.jms.JmsMessage;
import org.freakz.hokan_ng_springboot.bot.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.jpa.service.PropertyService;
import org.freakz.hokan_ng_springboot.bot.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.models.LunchMenu;
import org.freakz.hokan_ng_springboot.bot.service.lunch.LunchServiceImpl;
import org.freakz.hokan_ng_springboot.bot.util.CommandArgs;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

/**
 * Created by Petri Airio on 7.4.2016.
 * -
 */
@Controller
@Slf4j
public class ServicesWebController {

  @Autowired
  private JmsSender jmsSender;

  @Autowired
  private LunchServiceImpl lunchService;

  @Autowired
  PropertyService propertyService;

  @RequestMapping("/message")
  public String greeting(@RequestParam(value = "key", required = false) String key, Model model) {
    model.addAttribute("message", "tfufufufuf" + key);
    return "message";
  }

  @RequestMapping("/lunch")
  public String lunch(@RequestParam(value = "place", required = false) String placeKey, Model model) {
    LunchPlace place = LunchPlace.LOUNAS_INFO_HARMOONI;
    if (placeKey != null) {
      place = LunchPlace.getLunchPlace(placeKey);
      if (place == null) {
        model.addAttribute("place", "Unknown place: " + placeKey);
        model.addAttribute("lunch", "");
        return "lunch";
      }
    }
    DateTime day = DateTime.now();
    LunchData lunchData = lunchService.getLunchForDay(place, day);
    LunchDay lunchDay = LunchDay.getFromDateTime(day);
    LunchMenu lunchMenu = lunchData.getMenu().get(lunchDay);

    model.addAttribute("place", place.getName());
    model.addAttribute("lunch", lunchMenu.getMenu());
    return "lunch";
  }

  @RequestMapping("/command")
  public String command(@RequestParam(value = "line", required = true) String line, Model model) {
    log.debug("Handling line: {}", line);
    boolean doWebCommands = propertyService.getPropertyAsBoolean(PropertyName.PROP_SYS_DO_WEB_COMMANDS, false);
    if (!doWebCommands) {

      model.addAttribute("message", "doWebCommands = false");

    } else {
      String botName = "_Hokan_";
      String networkName = "DevNET";
      String channel = "@privmsg";
      String sender = "webuser";
      String login = "webuser";
      String hostname = "webhost";
      IrcMessageEvent ircMessageEvent = (IrcMessageEvent) IrcEventFactory.createIrcMessageEvent(botName, networkName, channel, sender, login, hostname, line);
      ircMessageEvent.setPrivate(true);
      ircMessageEvent.setWebMessage(true);

      try {
        String serviceResponse = doServicesRequest(ServiceRequestType.ENGINE_REQUEST, ircMessageEvent, "");
        model.addAttribute("message", serviceResponse);
        int foo = 0;
      } catch (HokanException e) {
        e.printStackTrace();
      }
    }
    return "command";
  }


  public String doServicesRequest(ServiceRequestType requestType, IrcMessageEvent ircEvent, Object... parameters) throws HokanException {
    ServiceRequest request = new ServiceRequest(requestType, ircEvent, new CommandArgs(ircEvent.getMessage()), parameters);
    ObjectMessage objectMessage = jmsSender.sendAndGetReply(HokanModule.HokanEngine.getQueueName(), "ENGINE_REQUEST", request, false);
    if (objectMessage == null) {
      throw new HokanException("ServiceResponse null, is Engine module running?");
    }
    try {
      JmsMessage jmsMessage = (JmsMessage) objectMessage.getObject();
      return jmsMessage.getEngineResponse();
    } catch (JMSException e) {
      log.error("jms", e);
    }
    return "n/a";

  }


}
