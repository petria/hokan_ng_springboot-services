package org.freakz.hokan_ng_springbootservices.IT;

import org.freakz.hokan_ng_springboot.bot.services.config.RuntimeConfig;
import org.freakz.hokan_ng_springboot.bot.services.service.horo.HoroFetchServiceImpl;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderService;
import org.freakz.hokan_ng_springboot.bot.services.service.sms.SMSSenderServiceImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IntegrationTest {

    @Test
    public void testSMS() {
        RuntimeConfig config = new RuntimeConfig();
        SMSSenderService smsSenderService = new SMSSenderServiceImpl(config);
        smsSenderService.sendSMS("FROM", "12345", "Test message");
    }

    @Test
    public void testHoro() {
        HoroFetchServiceImpl horoFetchService = new HoroFetchServiceImpl();
        String horo = horoFetchService.getHoro("kakso");
        int foo = 0;
    }

    private static Map<String, Integer> valueMap = new HashMap<>();

    static {
        valueMap.put("infected", 0);
        valueMap.put("healed", 0);
        valueMap.put("dead", 0);
    }

    @Test
    public void testCorona() {
        String title = "";
        try {
            String url = "https://korona.kans.io/";
            Document doc = Jsoup.connect(url).get();
            Elements body = doc.getElementsByTag("title");
            title = body.text();
            extractNumbers(title);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int foo = 0;
    }

    private Integer[] extractNumbers(String title) {
        String[] split = title.split(" ");
        Integer infected = Integer.parseInt(split[6]);
        Integer healed = Integer.parseInt(split[9]);
        Integer dead = Integer.parseInt(split[12]);
        Integer[] diffs = {0, 0, 0, 0, 0, 0};

        if (infected > valueMap.get("infected")) {
            diffs[0] = infected - valueMap.get("infected");
        }
        if (healed > valueMap.get("healed")) {
            diffs[1] = healed - valueMap.get("healed");
        }
        if (dead > valueMap.get("dead")) {
            diffs[2] = dead - valueMap.get("dead");
        }
        valueMap.put("infected", infected);
        valueMap.put("healed", healed);
        valueMap.put("dead", dead);
        diffs[3] = infected;
        diffs[4] = healed;
        diffs[5] = dead;

        return diffs;
    }


    @Test
    public void testDiffCalc() {
        String title = "Suomen koronavirus-tartuntatilanne - Tartunnat : 400 - Parantuneet: 10 - Menehtyneet: 0";
        Integer[] diffs = extractNumbers(title);

        String title2 = "Suomen koronavirus-tartuntatilanne - Tartunnat : 410 - Parantuneet: 13 - Menehtyneet: 5";
        Integer[] diffs2 = extractNumbers(title2);

        String title3 = "Suomen koronavirus-tartuntatilanne - Tartunnat : 410 - Parantuneet: 14 - Menehtyneet: 6";
        Integer[] n = extractNumbers(title3);

        String res =
                String.format("Suomen koronavirus-tartuntatilanne - Tartunnat : %d (+%d) - Parantuneet: %d (+%d) - Menehtyneet: %d (+%d)",
                        n[3], n[0],
                        n[4], n[1],
                        n[5], n[2]
                );
        int foo = 0;
    }

}
