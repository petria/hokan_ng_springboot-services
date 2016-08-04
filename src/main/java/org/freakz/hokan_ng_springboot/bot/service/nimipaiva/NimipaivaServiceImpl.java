package org.freakz.hokan_ng_springboot.bot.service.nimipaiva;

/**
 * Created by Petri Airio on 5.10.2015.
 * -
 */

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.models.NimipaivaData;
import org.freakz.hokan_ng_springboot.bot.util.FileUtil;
import org.freakz.hokan_ng_springboot.bot.util.StringStuff;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class NimipaivaServiceImpl implements NimipaivaService {

  private static final String NIMIPAIVAT_TXT = "/Nimipaivat.txt";

  private Map<DateTime, NimipaivaData> dateTimeNamesMap = new HashMap<>();

  @PostConstruct
  public void loadNames() {
    FileUtil fileUtil = new FileUtil();
    StringBuilder contents = new StringBuilder();
    try {
      fileUtil.copyResourceToTmpFile(NIMIPAIVAT_TXT, contents);
      String[] rows = contents.toString().split("\n");
      for (String row : rows) {
        String[] split1 = row.split("\\. ");
        if (split1.length == 2) {
          String[] date = split1[0].split("\\.");
          try {
            DateTime dateTime = DateTime.now().withDayOfMonth(Integer.parseInt(date[0])).withMonthOfYear(Integer.parseInt(date[1]));
            String[] names = split1[1].split(", ");
            NimipaivaData nimipaivaData = new NimipaivaData(dateTime, Arrays.asList(names));
            dateTimeNamesMap.put(dateTime, nimipaivaData);
          } catch (IllegalFieldValueException e) {
            log.error("??");
          }
        }
      }
    } catch (IOException e) {
      log.error("Can't load {}", NIMIPAIVAT_TXT);
    }
  }

  private NimipaivaData findByDay(DateTime day) {
    int dayOfMont = day.getDayOfMonth();
    int monthOfYear = day.getMonthOfYear();
    for (NimipaivaData nimipaivaData : dateTimeNamesMap.values()) {
      int dayOfMont2 = nimipaivaData.getDay().getDayOfMonth();
      int monthOfYear2 = nimipaivaData.getDay().getMonthOfYear();
      if (dayOfMont == dayOfMont2 && monthOfYear == monthOfYear2) {
        return nimipaivaData;
      }
    }
    return null;
  }

  private NimipaivaData findByName(String nameToFind) {
    for (NimipaivaData nimipaivaData : dateTimeNamesMap.values()) {
      for (String name : nimipaivaData.getNames()) {
        if (StringStuff.match(name, nameToFind, true)) {
          return nimipaivaData;
        }
      }
    }
    return null;
  }

  @Override
  public NimipaivaData getNamesForDay(DateTime day) {
    NimipaivaData nimipaivaData = findByDay(day);
    if (nimipaivaData != null) {
      return nimipaivaData;
    }
    return null;
  }

  @Override
  public NimipaivaData findDayForName(String name) {
    NimipaivaData nimipaivaData = findByName(name);
    if (nimipaivaData != null) {
      return nimipaivaData;
    }
    return null;
  }
}
