package org.freakz.hokan_ng_springboot.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.cmdpool.CommandPool;
import org.freakz.hokan_ng_springboot.bot.cmdpool.CommandRunnable;
import org.freakz.hokan_ng_springboot.bot.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.exception.HokanException;
import org.freakz.hokan_ng_springboot.bot.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.jpa.service.PropertyService;
import org.freakz.hokan_ng_springboot.bot.jpa.service.UrlLoggerService;
import org.freakz.hokan_ng_springboot.bot.models.StatsData;
import org.freakz.hokan_ng_springboot.bot.models.StatsMapper;
import org.freakz.hokan_ng_springboot.bot.service.nimipaiva.NimipaivaService;
import org.freakz.hokan_ng_springboot.bot.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.util.TimeUtil;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Created by Petri Airio on 22.9.2015.
 * -
 */
@Service
@Slf4j
public class DayChangedServiceImpl implements DayChangedService, CommandRunnable {

    @Autowired
    private ChannelPropertyService channelPropertyService;

    @Autowired
    private CommandPool commandPool;

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private NimipaivaService nimipaivaService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private UrlLoggerService urlLoggerService;

    private Map<String, String> daysDone = new HashMap<>();

    @PostConstruct
    private void startRunner() {
        commandPool.startRunnable(this, "<system>");
    }

    @Override
    public void handleRun(long myPid, Object args) throws HokanException {
        while (true) {
            try {
                checkDayChanged();
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                log.error("Error", e);
                break;
            }
        }
    }

    public String getDailyStats(Channel channel) {
        DateTime yesterday = DateTime.now().minusDays(1);
        StatsMapper statsMapper = statsService.getDailyStatsForChannel(yesterday, channel.getChannelName());

        if (!statsMapper.hasError()) {
            List<StatsData> statsDatas = statsMapper.getStatsData();
            String res = StringStuff.formatTime(yesterday.toDate(), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " word stats:";
            int i = 1;
            int countTotal = 0;
            for (StatsData statsData : statsDatas) {
                res += " " + i + ") " + statsData.getNick() + "=" + statsData.getWords();
                countTotal += statsData.getWords();
                i++;
            }
            res += " - Word count = " + countTotal;
            return res;
        } else {
            log.warn("Could not create stats notify request!");
        }
        return null;
    }

    private void checkDayChanged() {
        String dayChangedTime = propertyService.getPropertyAsString(PropertyName.PROP_SYS_DAY_CHANGED_TIME, "00:00");
        String time = StringStuff.formatTime(new Date(), StringStuff.STRING_STUFF_DF_HHMM);
        if (time.equals(dayChangedTime)) {
            String dayCheck = StringStuff.formatTime(new Date(), StringStuff.STRING_STUFF_DF_DDMMYYYY);
            boolean dayDone = daysDone.containsKey(dayCheck);
            if (!dayDone) {
                if (handleDayChangedTo(dayCheck)) {
                    daysDone.put(dayCheck, "done");
                }
            }
        }
    }

    private boolean handleDayChangedTo(String dayChangedTo) {
        log.debug("Day changed to: {}", dayChangedTo);
        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_DAY_CHANGED, ".*");
        for (Channel channel : channelList) {
            String property = channelPropertyService.getChannelPropertyAsString(channel, PropertyName.PROP_CHANNEL_DO_DAY_CHANGED, "");

            String topic = "";
            if (parseProperty(property, "topic") != null) {
                String dailyNimip = getNimipäivät();
                int week = DateTime.now().getWeekOfWeekyear();
                topic = String.format("---=== Day changed to: %s (W%d) :: %s ===---", dayChangedTo, week, dailyNimip);
            }

            String sunRises = "";
            List<String> sunRisesCities = parseProperty(property, "sunRises");
            if (sunRisesCities != null) {
                sunRises = getSunriseTexts(sunRisesCities);
            }
            String dailyStats = "";
            if (parseProperty(property, "dailyStats") != null) {
                dailyStats = getDailyStats(channel);
            }
            String dailyUrls = "";
            if (parseProperty(property, "dailyUrls") != null) {
                dailyUrls = getDailyUrls(channel.getChannelName());
            }

            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setNotifyMessage(String.format("%s\n%s\n%s\n%s", topic, sunRises, dailyStats, dailyUrls));
            notifyRequest.setTargetChannelId(channel.getId());
            jmsSender.send(HokanModule.HokanIo.getQueueName(), "STATS_NOTIFY_REQUEST", notifyRequest, false);
        }
        return true;
    }

    private List<String> parseProperty(String property, String keyword) {
        String[] split = property.split(" ");
        for (String str : split) {
            if (str.startsWith(keyword)) {
                if (str.contains(":")) {
                    String[] split2 = str.split(":");
                    List<String> list = new LinkedList<>(Arrays.asList(split2));
                    if (list.size() > 1) {
                        list.remove(0);
                    }
                    return list;
                } else {
                    return new ArrayList<>();
                }
            }
        }
        return null;
    }


    public String getSunriseTexts(List<String> sunRisesCities) {
        String ret = null;
        String baseUrl = "http://en.ilmatieteenlaitos.fi/weather/";

        for (String city : sunRisesCities) {
            String url = baseUrl + city;
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                continue;
            }
            if (ret == null) {
                ret = "";
            } else {
                ret += "\n";
            }
            Elements elements1 = doc.getElementsByAttributeValue("class", "col-xs-12");
            String place = elements1.get(1).text().split(" ")[2];
            ret += place + ": ";

            Elements elementsT = doc.getElementsByAttributeValue("class", "celestial-status-text");
            String tt = elementsT.get(1).text();
            ret += tt;
        }
        return ret;
    }

    private String getNimipäivät() {
        List<String> nimiPvmList = nimipaivaService.getNamesForDay(DateTime.now()).getNames();
        String ret = "";
        for (String nimi : nimiPvmList) {
            if (ret.length() > 0) {
                ret += ", ";
            }
            ret += nimi;
        }
        return ret;
    }

    private String getDailyUrls(String channelName) {
        DateTime time = DateTime.now().minusDays(1);
        List counts = urlLoggerService.findTopSenderByChannelAndCreatedBetween(channelName, TimeUtil.getStartAndEndTimeForDay(time));
        String ret = StringStuff.formatTime(time.toDate(), StringStuff.STRING_STUFF_DF_DDMMYYYY) + " url  stats: ";
        if (counts.size() == 0) {
            ret += "no urls!!";
            return ret;
        }
        int max_count = 9;
        for (int i = 0; i < counts.size(); i++) {
            Object[] counter = (Object[]) counts.get(i);
            Url url = (Url) counter[0];
            Long count = (Long) counter[1];
            if (i > 0) {
                ret += ", ";
            }
            ret += (i + 1) + ") " + url.getSender() + "=" + count;
            if (i == max_count) {
                break;
            }
        }
        long countTotal = 0;
        for (Object count1 : counts) {
            Object[] counter = (Object[]) count1;
            Long count = (Long) counter[1];
            countTotal += count;
        }
        ret += " - Url count = " + countTotal;

        return ret;
    }

}
