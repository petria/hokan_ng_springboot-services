package org.freakz.hokan_ng_springboot.bot.service.annotation;

import org.freakz.hokan_ng_springboot.bot.events.ServiceRequestType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Petri Airio on 17.11.2015.
 * -
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceMessageHandler {

  ServiceRequestType ServiceRequestType();

}
