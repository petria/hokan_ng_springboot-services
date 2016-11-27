package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.UserFlag;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Petri Airio on 25.2.2016.
 * -
 */
public class UserFlagTest {

    @Test
    public void testFromStringToUserFlag() {
        String str = "ADMIN";
        UserFlag userFlag = UserFlag.getUserFlagFromString(str);
        Assert.assertEquals(UserFlag.ADMIN, userFlag);
    }

    @Test
    public void testFromExactShortStringToUserFlag() {
        String str = "WL";
        UserFlag userFlag = UserFlag.getUserFlagFromString(str);
        Assert.assertEquals(UserFlag.WEB_LOGIN, userFlag);
    }

    @Test
    public void testFromShortAbbreviationStringToUserFlag() {
        String str = "igNoR";
        UserFlag userFlag = UserFlag.getUserFlagFromString(str);
        Assert.assertEquals(UserFlag.IGNORE_ON_CHANNEL, userFlag);
    }


}
