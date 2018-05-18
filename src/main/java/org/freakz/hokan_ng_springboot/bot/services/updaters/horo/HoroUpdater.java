package org.freakz.hokan_ng_springboot.bot.services.updaters.horo;

import org.freakz.hokan_ng_springboot.bot.common.models.HoroHolder;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.common.util.StringStuff;
import org.freakz.hokan_ng_springboot.bot.services.updaters.Updater;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: petria
 * Date: 11/21/13
 * Time: 1:32 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
public class HoroUpdater extends Updater {

    public final static String[] HORO_NAMES =
            {"Oinas", "Härkä", "Kaksoset", "Rapu", "Leijona", "Neitsyt",
                    "Vaaka", "Skorpioni", "Jousimies", "Kauris",
                    "Vesimies", "Kalat"};

    private final String horoRowMatcher = "oinas.*|h.rk..*|kaksonen.*|rapu.*|leijona.*|neitsyt.*|vaaka.*|skorpioni.*|jousimies.*|kauris.*|vesimies.*|kalat.*";

    private Map<String, HoroHolder> horos;

    public HoroUpdater() {
    }

    @Override
    public Calendar calculateNextUpdate() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, 60);
        return cal;
    }

    @Override
    public void doUpdateData() throws Exception {
        Map<String, HoroHolder> horos = updateIL();
        if (horos != null) {
            this.horos = horos;
        }
    }

    @Override
    public Object doGetData(Object... args) {
        return this.getHoro((String) args[0]);
    }

    public Document getDocument() throws IOException {
        return Jsoup.connect("http://www.iltalehti.fi/horoskooppi/?ref=kn").userAgent(StaticStrings.HTTP_USER_AGENT).get();
    }

    public Map<String, HoroHolder> updateIL() throws Exception {
        Map<String, HoroHolder> horos = new HashMap<>();

        Document doc = getDocument();
        Elements container = doc.getElementsByAttributeValue("id", "container_keski");
        Elements pees = container.select("p");
        for (Element pee : pees) {
            String text = pee.text();
            if (StringStuff.match(text, horoRowMatcher, true)) {
                String firstWord = text.split(" ")[0].toLowerCase();
                horos.put(firstWord, new HoroHolder(text));
            }
        }
        return horos;
    }

    private HoroHolder generateHolder(int horoIdx) {
        String horoTxt = "Mugalabuglala baubuaagugug tsimszalabimpero!";
        if (horos.size() != 0) {
            List<String> textList = new ArrayList<>();
            for (HoroHolder hh : horos.values()) {
                String txt = hh.getHoroscopeText();
                String[] split = txt.split("\\. ");
                for (String ss : split) {
                    if (ss.matches("\\d.*")) {
                        continue;
                    }
                    textList.add(ss);
                }
            }
            Collections.shuffle(textList);
            int rnd = 2 + (int) (Math.random() * 3);
            horoTxt = "";
            while (rnd > 0) {
                horoTxt += textList.get(0) + ". ";
                textList.remove(0);
                rnd--;
                if (textList.size() == 0) {
                    break;
                }
            }
        }
        return new HoroHolder(horoTxt);
    }

    public HoroHolder getHoro(String horo) {

        int horoIdx = -1;
        int idx = 0;
        for (String horoName : HORO_NAMES) {
            if (StringStuff.match(horoName, ".*" + horo + ".*", true)) {
                horoIdx = idx;
                break;
            }
            idx++;
        }
        if (horoIdx != -1 && horos != null) {
            HoroHolder holder;
            try {
                holder = horos.get(HORO_NAMES[horoIdx].toLowerCase());
            } catch (IndexOutOfBoundsException ex) {
                holder = generateHolder(horoIdx);
            }
            return holder;
        }
        return null;
    }


}
