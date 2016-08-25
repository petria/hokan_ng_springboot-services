package org.freakz.hokan_ng_springboot.bot.updaters.horo;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.models.HoroHolder;
import org.freakz.hokan_ng_springboot.bot.updaters.Updater;
import org.freakz.hokan_ng_springboot.bot.util.StaticStrings;
import org.freakz.hokan_ng_springboot.bot.util.StringStuff;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: petria
 * Date: 11/21/13
 * Time: 1:32 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
//@Scope("singleton")
@Slf4j
public class HoroUpdater extends Updater {

    public final static String[] HORO_NAMES =
            {"Oinas", "Härkä", "Kaksoset", "Rapu", "Leijona", "Neitsyt",
                    "Vaaka", "Skorpioni", "Jousimies", "Kauris",
                    "Vesimies", "Kalat"};

    private List<HoroHolder> horos;

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
        List<HoroHolder> horos = updateIL();
        if (horos != null) {
            this.horos = horos;
        }
    }

    @Override
    public Object doGetData(Object... args) {
        return this.getHoro((String) args[0]);
    }


    public List<HoroHolder> updateIL() throws Exception {
        List<HoroHolder> horos = new ArrayList<>();
        Document doc = Jsoup.connect("http://www.iltalehti.fi/viihde/horoskooppi1_ho.shtml").userAgent(StaticStrings.HTTP_USER_AGENT).get();
        Elements container = doc.getElementsByAttributeValue("id", "container_keski");
        Elements pees = container.select("p");
        int i = 0;
        for (int horo = 1; horo < 25; horo += 2) {
//      Element ee1 = pees.get(horo);
            Element ee2 = pees.get(horo + 1);
            HoroHolder hh = new HoroHolder(i, ee2.text());
            i++;
            horos.add(hh);
        }
        return horos;
    }

    private HoroHolder generateHolder(int horoIdx) {
        String horoTxt = "Mugalabuglala baubuaagugug tsimszalabimpero!";
        if (horos.size() != 0) {
            List<String> textList = new ArrayList<>();
            for (HoroHolder hh : horos) {
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
        return new HoroHolder(horoIdx, horoTxt);
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
                holder = horos.get(horoIdx);
            } catch (IndexOutOfBoundsException ex) {
                holder = generateHolder(horoIdx);
            }
            return holder;
        }
        return null;
    }


}
