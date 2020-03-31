package org.freakz.hokan_ng_springboot.bot.services.service.timer;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyService;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.jibble.pircbot.Colors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KoronaCheckService {

    private final ChannelPropertyService channelPropertyService;
    private final JmsSender jmsSender;

    private Map<String, InfectedStats> ownerToStatsMap = new HashMap<>();
    private int currentInfected = 0;
    private int currentHealed = 0;
    private int currentDead = 0;

    @Autowired
    public KoronaCheckService(ChannelPropertyService channelPropertyService, JmsSender jmsSender) {
        this.channelPropertyService = channelPropertyService;
        this.jmsSender = jmsSender;
    }

    private InfectedStats calcInfectedStatsDiffs(int currentInfected, int currentHealed, int currentDead, InfectedStats oldStats) {
        InfectedStats stats = new InfectedStats();
        stats.infected = currentInfected;
        stats.healed = currentHealed;
        stats.dead = currentDead;

        if (stats.infected > oldStats.infected) {
            stats.infectedDiff = stats.infected - oldStats.infected;
            stats.d0 = true;
            stats.d1 = true;
        }
        if (stats.healed > oldStats.healed) {
            stats.healedDiff = stats.healed - oldStats.healed;
            stats.d0 = true;
            stats.d2 = true;
        }
        if (stats.dead > oldStats.dead) {
            stats.deadDiff = stats.dead - oldStats.dead;
            stats.d0 = true;
            stats.d3 = true;
        }
        return stats;
    }

    private String getKoronas() {
        try {
            String url = "https://korona.kans.io/";
//            String url = "http://62.78.224.147/korona.html";

            // 62.78.224.147
            Document doc = Jsoup.connect(url).get();
            Elements body = doc.getElementsByTag("title");
            return body.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean sendNotify(String notify) {

        List<Channel> channelList = channelPropertyService.getChannelsWithProperty(PropertyName.PROP_CHANNEL_DO_KORONA, "true");
        for (Channel channel : channelList) {
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setNotifyMessage(notify);
            notifyRequest.setTargetChannelId(channel.getId());
            jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "WHOLE_LINE_TRIGGER_NOTIFY_REQUEST", notifyRequest, false);
        }
        return true;
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void updateTimer() {
        updateCurrentStats();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void notifyTimer() {
        InfectedStats oldStats = getInfectedStats("INTERNAL");
        InfectedStats ns = calcInfectedStatsDiffs(currentInfected, currentHealed, currentDead, oldStats);
        setInfectedStats("INTERNAL", ns);

        if (ns.d0) {
            log.debug("c1: {} - c2: {} - c3: {} -- d1: {} - d2: {} - d3: {}", ns.infected, ns.healed, ns.dead, ns.infectedDiff, ns.healedDiff, ns.deadDiff);
            String d1 = "";
            String d2 = "";
            String d3 = "";

            String b1 = "";
            if (ns.d1) {
                b1 = Colors.BOLD;
            }
            String b2 = "";
            if (ns.d2) {
                b2 = Colors.BOLD;
            }
            String b3 = "";
            if (ns.d3) {
                b3 = Colors.BOLD;
            }

            d1 = String.format(" - infected: %d (%s+%d%s)", ns.infected, b1, ns.infectedDiff, b1);

//            d2 = String.format(" - healed: %d (%s+%d%s)", ns.healed, b2, ns.healedDiff, b2);

            d3 = String.format(" - dead: %d (%s+%d%s)", ns.dead, b3, ns.deadDiff, b3);

            String notify = String.format("Korona update%s%s%s", d1, d2, d3);
            log.debug("Korona notify: {}", notify);
            sendNotify(notify);
        }
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.KORONA_REQUEST)
    public void handleKoronaCmdRequest(ServiceRequest request, ServiceResponse response) {
        String nickId = (String) request.getParameters()[0];
        log.debug("Handle KoronaCMD: {}", nickId);
        InfectedStats oldStats = getInfectedStats(nickId);
        InfectedStats ns = calcInfectedStatsDiffs(currentInfected, currentHealed, currentDead, oldStats);
        setInfectedStats(nickId, ns);
        Integer[] ret = {ns.infected, ns.healed, ns.dead, ns.infectedDiff, ns.healedDiff, ns.deadDiff};
        response.setResponseData(request.getType().getResponseDataKey(), ret);
    }

    private void setInfectedStats(String key, InfectedStats newStats) {
        this.ownerToStatsMap.put(key, newStats);
    }

    private InfectedStats getInfectedStats(String key) {
        InfectedStats stats = this.ownerToStatsMap.get(key);
        if (stats == null) {
            stats = new InfectedStats();
            stats.infected = currentInfected;
            stats.healed = currentHealed;
            stats.dead = currentDead;
            this.ownerToStatsMap.put(key, stats);
        }
        return stats;
    }

    private void updateCurrentStats() {
        String title = getKoronas();
        if (title != null) {
            String[] split = title.split(" ");
            currentInfected = Integer.parseInt(split[6]);
            currentHealed = Integer.parseInt(split[10]);
            currentDead = Integer.parseInt(split[13]);
//            log.debug("currentInfected: {} - currentHealed: {} -  currentDead: {}", currentInfected, currentHealed, currentDead);
        }
    }

    public void test() {
        notifyTimer();
//        updateCurrentStats();
    }

    class InfectedStats {
        int infected = 0;
        int healed = 0;
        int dead = 0;

        int infectedDiff = 0;
        int healedDiff = 0;
        int deadDiff = 0;

        boolean d0 = false;
        boolean d1 = false;
        boolean d2 = false;
        boolean d3 = false;
    }


}
