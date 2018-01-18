package org.freakz.hokan_ng_springboot.bot.services.service.urls;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;
import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.NotifyRequest;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Channel;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.PropertyName;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Url;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.UrlRepository;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyRepositoryService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.NetworkService;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Petri Airio on 27.8.2015.
 * -
 */
@Service
@Slf4j
public class UrlCatchServiceImpl implements UrlCatchService {

    private final ChannelPropertyRepositoryService channelPropertyService;

    private final ChannelService channelService;

    private final JmsSender jmsSender;

    private final NetworkService networkService;

    private final UrlRepository urlRepository;

    @Autowired
    public UrlCatchServiceImpl(ChannelPropertyRepositoryService channelPropertyService, ChannelService channelService, JmsSender jmsSender, NetworkService networkService, UrlRepository urlRepository) {
        this.channelPropertyService = channelPropertyService;
        this.channelService = channelService;
        this.jmsSender = jmsSender;
        this.networkService = networkService;
        this.urlRepository = urlRepository;
    }

    @Override
    public void catchUrls(IrcMessageEvent ircMessageEvent) {
        Network network = networkService.getNetwork(ircMessageEvent.getNetwork());
        Channel channel = channelService.findByNetworkAndChannelName(network, ircMessageEvent.getChannel());
        catchUrls(ircMessageEvent, channel);
    }

    private long logUrl(IrcMessageEvent iEvent, String url) {
        long isWanha = 0;

        Url entity = urlRepository.findFirstByUrlLikeOrUrlTitleLikeOrderByCreatedDesc(url, url);
        if (entity != null) {
            entity.addWanhaCount(1);
            isWanha = entity.getWanhaCount();
        } else {
            entity = new Url(url, iEvent.getSender(), iEvent.getChannel(), new Date());
        }
        urlRepository.save(entity);
        log.info("Logged URL: {}", entity);
        return isWanha;

    }


    private void catchUrls(IrcMessageEvent iEvent, Channel channel) {
        String msg = iEvent.getMessage();
        boolean askWanha = false;
        if (msg.contains("wanha?")) {
            askWanha = true;
        }

        String regexp = "(https?://|www\\.)\\S+";

        Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);
        while (m.find()) {
            String url = m.group();
            long isWanha = logUrl(iEvent, url);
            String ignoreTitles = "fbcdn-sphotos.*|.*(avi|bz|gz|gif|exe|iso|jpg|jpeg|mp3|mp4|mkv|mpeg|mpg|mov|pdf|png|torrent|zip|7z|tar)";

            StringBuilder wanhaAdd = new StringBuilder();
            for (int i = 0; i < isWanha; i++) {
                wanhaAdd.append("!");
            }
            if (!StringStuff.match(url, ignoreTitles, true)) {
                log.info("Finding title: " + url);
                getTitleNew(url, channel, isWanha > 0, wanhaAdd.toString());
            } else {
                log.info("SKIP finding title: " + url);
                if (isWanha > 0) {
                    NotifyRequest notifyRequest = new NotifyRequest();
                    if (askWanha) {
                        notifyRequest.setNotifyMessage(url + " | joo oli wanha!");
                    } else {
                        notifyRequest.setNotifyMessage(url + " | wanha" + wanhaAdd);
                    }
                    notifyRequest.setTargetChannelId(channel.getId());
                    jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "URLS_NOTIFY_REQUEST", notifyRequest, false);
                }
            }
        }
    }

    private String getIMDBData(String url) throws Exception {
        org.jsoup.nodes.Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
        String rating = doc.getElementsByAttributeValue("itemprop", "ratingValue").get(0).text();
        String users = doc.getElementsByAttributeValue("itemprop", "ratingCount").get(0).text();
        return String.format("Ratings: %s/10 from %s users", rating, users);
    }

    private void getTitleNew(final String url, final Channel ch, final boolean isWanha, final String wanhaAadd) {
        org.jsoup.nodes.Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Can't get title for: {}", url);
            return;
        }
        Elements titleElements = doc.getElementsByTag("title");
        String title = null;
        if (titleElements.size() > 0) {
            title = titleElements.get(0).text();
            title = title.replaceAll("[\n\t]", "");
        }
        if (title != null) {
            title = StringStuff.htmlEntitiesToText(title);
            title = title.replaceAll("\t", "");

            log.debug("title: '{}'", title);

            Url entity = urlRepository.findFirstByUrlLikeOrUrlTitleLikeOrderByCreatedDesc(url, url);
            entity.setUrlTitle(title);
            urlRepository.save(entity);
            if (title.length() == 0) {
                return;
            }

            if (url.contains("http://www.imdb.com/title/")) {
                try {
                    String ratings = getIMDBData(url);
                    if (ratings != null) {
                        title += " | " + ratings;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isWanha) {
                title = title + " | wanha" + wanhaAadd;
            }
            boolean titles = channelPropertyService.getChannelPropertyAsBoolean(ch, PropertyName.PROP_CHANNEL_DO_URL_TITLES, false);
            if (titles) {
                NotifyRequest notifyRequest = new NotifyRequest();
                notifyRequest.setNotifyMessage(title);
                notifyRequest.setTargetChannelId(ch.getId());
                jmsSender.send(HokanModule.HokanServices, HokanModule.HokanIo.getQueueName(), "URLS_NOTIFY_REQUEST", notifyRequest, false);
            }
        } else {
            log.info("Could not find title for url: " + url);
        }
    }

}
