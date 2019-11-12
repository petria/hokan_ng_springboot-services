package org.freakz.hokan_ng_springboot.bot.services.service.sms;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;

@Service
public class SMSSenderServiceImpl implements SMSSenderService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SMSSenderServiceImpl.class);

    @Override
    public String sendSMS(String from, String to, String message) {
        log.debug("from: {} - to: {} -> {}", from, to, message);

        try {
            String url = String.format("https://api.budgetsms.net/sendsms/?Credit=1&username=petria&userid=18436&handle=c11bc789efcd53818aedd7020c2dc5c6&msg=%s&from=%s&to=%s", URLEncoder.encode(message, "UTF-8"), from, to);
            log.debug("URL: {}", url);
            Document doc = Jsoup.connect(url).get();
            Elements body = doc.getElementsByTag("body");
            String answer = body.text();
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
