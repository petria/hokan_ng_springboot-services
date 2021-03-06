package org.freakz.hokan_ng_springboot.bot.services.service.timeservice;

import org.freakz.hokan_ng_springboot.bot.common.models.TimeDifferenceData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TimeDifferenceServiceImpl implements TimeDifferenceService {

    @Override
    public TimeDifferenceData getTimeDifference(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        TimeDifferenceData differenceData = new TimeDifferenceData();

        LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);

        long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);


        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

        long[] diffs = new long[]{seconds, minutes, hours, days, months, years};

        differenceData.setDiffs(diffs);
        return differenceData;
    }

}
