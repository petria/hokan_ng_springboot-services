package org.freakz.hokan_ng_springboot.bot.services.service;


import org.freakz.hokan_ng_springboot.bot.common.models.TimeDifferenceData;
import org.freakz.hokan_ng_springboot.bot.services.service.timeservice.TimeDifferenceService;
import org.freakz.hokan_ng_springboot.bot.services.service.timeservice.TimeDifferenceServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class TimeDifferenceServiceTest {

    @Test
    public void testTimeDifferenceService() {
        LocalDateTime from = LocalDateTime.of(2019, 1, 1, 12, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2019, 1, 1, 13, 10, 35, 0);
        TimeDifferenceService sut = new TimeDifferenceServiceImpl();
        TimeDifferenceData timeDifference = sut.getTimeDifference(from, to);
        long[] diffs = timeDifference.getDiffs();
        Assert.assertEquals(35, diffs[0]);
        Assert.assertEquals(10, diffs[1]);
        Assert.assertEquals(1, diffs[2]);
        Assert.assertEquals(0, diffs[3]);
        Assert.assertEquals(0, diffs[4]);
        Assert.assertEquals(0, diffs[5]);

    }


}
