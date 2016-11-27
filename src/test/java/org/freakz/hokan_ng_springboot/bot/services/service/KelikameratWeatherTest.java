package org.freakz.hokan_ng_springboot.bot.services.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Petri Airio on 18.6.2015.
 */
@Slf4j
public class KelikameratWeatherTest {

    String[] urls =
            {
                    "http://www.kelikamerat.info/kelikamerat/Etel%C3%A4-Karjala",
                    "http://www.kelikamerat.info/kelikamerat/Etel%C3%A4-Pohjanmaa",
                    "http://www.kelikamerat.info/kelikamerat/Etel%C3%A4-Savo",
                    "http://www.kelikamerat.info/kelikamerat/Kainuu",
                    "http://www.kelikamerat.info/kelikamerat/Kanta-H%C3%A4me",
                    "http://www.kelikamerat.info/kelikamerat/Keski-Pohjanmaa",
                    "http://www.kelikamerat.info/kelikamerat/Keski-Suomi",
                    "http://www.kelikamerat.info/kelikamerat/Kymenlaakso",
                    "http://www.kelikamerat.info/kelikamerat/Lappi",
                    "http://www.kelikamerat.info/kelikamerat/P%C3%A4ij%C3%A4t-H%C3%A4me",
                    "http://www.kelikamerat.info/kelikamerat/Pirkanmaa",
                    "http://www.kelikamerat.info/kelikamerat/Pohjanmaa",
                    "http://www.kelikamerat.info/kelikamerat/Pohjois-Karjala",
                    "http://www.kelikamerat.info/kelikamerat/Pohjois-Pohjanmaa",
                    "http://www.kelikamerat.info/kelikamerat/Pohjois-Savo",
                    "http://www.kelikamerat.info/kelikamerat/Satakunta",
                    "http://www.kelikamerat.info/kelikamerat/Uusimaa",
                    "http://www.kelikamerat.info/kelikamerat/Varsinais-Suomi"
            };

    @Test
    public void foo() {
        Assert.assertTrue(true);
    }

}
