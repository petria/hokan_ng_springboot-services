package org.freakz.hokan_ng_springboot.bot.updaters.telkku;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.TvNotify;
import org.freakz.hokan_ng_springboot.bot.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.jpa.service.TvNotifyService;
import org.freakz.hokan_ng_springboot.bot.models.TelkkuData;
import org.freakz.hokan_ng_springboot.bot.models.TelkkuProgram;
import org.freakz.hokan_ng_springboot.bot.models.TvNowData;
import org.freakz.hokan_ng_springboot.bot.updaters.UpdaterManagerService;
import org.freakz.hokan_ng_springboot.bot.util.StringStuff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * User: petria
 * Date: 11/26/13
 * Time: 2:30 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Service
@Slf4j
public class TelkkuServiceImpl implements TelkkuService, CommandRunnable {

  @Autowired
  private ChannelPropertyService channelPropertyService;

  @Autowired
  private CommandPool commandPool;

  @Autowired
  private JmsSender jmsSender;

  @Autowired
  private TvNotifyService notifyService;

  @Autowired
  private UpdaterManagerService updaterManagerService;


  public TelkkuServiceImpl() {

  }

  @Override
  public TelkkuProgram getCurrentProgram(Date time, String channelMatcher) {
    TelkkuData telkkuData = (TelkkuData) updaterManagerService.getUpdater("telkkuUpdater").getData().getData();
    if (telkkuData.getPrograms() == null) {
      return null;
    }
    for (TelkkuProgram tp : telkkuData.getPrograms()) {
      if (tp.getChannel().equalsIgnoreCase(channelMatcher)) {
        Date start = tp.getStartTimeD();
        Date end = tp.getEndTimeD();
        if (start.getTime() < time.getTime() && end.getTime() > time.getTime())
          return tp;
      }
    }
    return null;
  }

  @Override
  public TelkkuProgram getNextProgram(TelkkuProgram current, String channel) {
    TelkkuData telkkuData = (TelkkuData) updaterManagerService.getUpdater("telkkuUpdater").getData().getData();
    if (telkkuData.getPrograms() == null) {
      return null;
    }
    TelkkuProgram tp;
    Enumeration eNum = Collections.enumeration(telkkuData.getPrograms());
    boolean found = false;
    while (eNum.hasMoreElements()) {
      tp = (TelkkuProgram) eNum.nextElement();
      if (tp == current) {
        found = true;
        break;
      }
    }
    if (found) {
      while (eNum.hasMoreElements()) {
        tp = (TelkkuProgram) eNum.nextElement();
        if (tp.getChannel().equalsIgnoreCase(current.getChannel())) {
          return tp;
        }
      }
    }
    return null;
  }

  @Override
  public List<TelkkuProgram> findPrograms(String program) {
    List<TelkkuProgram> matches = new ArrayList<>();
    TelkkuData telkkuData = (TelkkuData) updaterManagerService.getUpdater("telkkuUpdater").getData().getData();
    if (telkkuData.getPrograms() == null) {
      return matches;
    }
    for (TelkkuProgram tp : telkkuData.getPrograms()) {
      if (StringStuff.match(tp.getProgram(), ".*" + program + ".*", true)) {
        matches.add(tp);
      }
      if (StringStuff.match(tp.getDescription(), ".*" + program + ".*", true)) {
        matches.add(tp);
      }
    }
    return matches;
  }

  @Override
  public List<TelkkuProgram> findDailyPrograms(Date theDay) {
    List<TelkkuProgram> daily = new ArrayList<>();
    TelkkuData telkkuData = (TelkkuData) updaterManagerService.getUpdater("telkkuUpdater").getData().getData();
    if (telkkuData.getPrograms() == null) {
      return daily;
    }
    String day1 = StringStuff.formatTime(theDay, StringStuff.STRING_STUFF_DF_DDMMYYYY);
    for (TelkkuProgram prg : telkkuData.getPrograms()) {
      String day2 = StringStuff.formatTime(prg.getStartTimeD(), StringStuff.STRING_STUFF_DF_DDMMYYYY);
      if (day1.equals(day2)) {
        daily.add(prg);
      }
    }
    return daily;
  }

  @Override
  public TelkkuProgram findProgramById(int id) {
    TelkkuData telkkuData = (TelkkuData) updaterManagerService.getUpdater("telkkuUpdater").getData().getData();
    if (telkkuData.getPrograms() == null) {
      return null;
    }
    for (TelkkuProgram tp : telkkuData.getPrograms()) {
      if (tp.getId() == id) {
        return tp;
      }
    }
    return null;
  }

  @Override
  public boolean isReady() {
    return updaterManagerService.getUpdater("telkkuUpdater").getUpdateCount() > 0;
  }

  @Override
  public String[] getChannels() {
    return new String[]{"YLE TV1", "YLE TV2", "MTV3", "Nelonen", "Kutonen", "JIM", "Sub", "YLE Teema", "TV5", "Fox", "Yle Fem"};
  }

  @Override
  public TvNowData getTvNowData() {
    Map<String, TelkkuProgram> nowData = new HashMap<>();
    Map<String, TelkkuProgram> nextData = new HashMap<>();
    Date date = new Date();
    for (String channel : getChannels()) {
      TelkkuProgram current = getCurrentProgram(date, channel);
      TelkkuProgram next = getNextProgram(current, channel);
      nowData.put(channel, current);
      nextData.put(channel, next);

    }
    return new TvNowData(getChannels(), nowData, nextData);
  }

  @Override
  public List<TelkkuProgram> getChannelDailyNotifiedPrograms(Channel channel, Date day) {
    List<TelkkuProgram> matches = new ArrayList<>();
    List<TelkkuProgram> daily = findDailyPrograms(day);
    List<TvNotify> notifies = notifyService.getTvNotifies(channel);
    for (TelkkuProgram prg : daily) {
      for (TvNotify notify : notifies) {
        if (StringStuff.match(prg.getProgram(), ".*" + notify.getNotifyPattern() + ".*", true)) {
          matches.add(prg);
        }
      }
    }
    return matches;
  }


  @PostConstruct
  private void startRunner() {
    commandPool.startRunnable(this, "<system>");
  }

  @Override
  public void handleRun(long myPid, Object args) throws HokanException {
    while (true) {
      try {
        notifyWatcher();
        Thread.sleep(30 * 1000);
      } catch (Exception e) {
        log.debug("interrupted");
        break;
      }
    }
  }

  private static class Notify {
    public Channel channel;
    public TvNotify notify;
    public TelkkuProgram program;
 }


  public void notifyWatcher() {
    List<Channel> channels = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_TVNOTIFY, "true|1");
    Calendar future = new GregorianCalendar();
    future.add(Calendar.MINUTE, 5);
    List<Notify> toNotify = new ArrayList<>();
    for (String tvChannel : getChannels()) {
      for (Channel channel : channels) {
        List<TvNotify> notifyList = notifyService.getTvNotifies(channel);
        TelkkuProgram current = getCurrentProgram(future.getTime(), tvChannel);
        if (current != null) {
          for (TvNotify notify : notifyList) {
            if (StringStuff.match(current.getProgram(), ".*" + notify.getNotifyPattern() + ".*", true)) {
              if (!current.isNotifyDone()) {
                Notify n = new Notify();
                n.channel = channel;
                n.notify = notify;
                n.program = current;
                toNotify.add(n);
              }
            }
          }
        }
      }
    }
    if (toNotify.size() > 0) {
      sendNotifies(toNotify);
    }

  }

  private void sendNotifies(List<Notify> toNotify) {

    for (Notify n : toNotify) {
      String note = String.format("Kohta alkaa -> [%s] %s %s (%s)",
          n.program.getChannel(),
          StringStuff.formatTime(n.program.getStartTimeD(), StringStuff.STRING_STUFF_DF_HHMM),
          n.program.getProgram(),
          n.program.getId()
      );
      NotifyRequest request = new NotifyRequest();
      request.setTargetChannelId(n.channel.getId());
      request.setNotifyMessage(note);
      jmsSender.send(HokanModule.HokanIo.getQueueName(), "TV_NOTIFY_REQUEST", request, false);
      n.program.setNotifyDone(true);
    }
  }

}
