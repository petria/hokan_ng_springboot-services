package org.freakz.hokan_ng_springboot.bot.service.topics;

import org.freakz.hokan_ng_springboot.bot.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.models.ChannelSetTopic;

/**
 * Created by Petri Airio (petri.airio@gmail.com) 09/11/2016 / 11.18
 */
public interface TopicService {

    void channelTopicSet(IrcMessageEvent ircMessageEvent);

    ChannelSetTopic channelTopicGet(IrcMessageEvent ircMessageEvent);

}
