package org.freakz.hokan_ng_springboot.bot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.freakz.hokan_ng_springboot.bot.common.enums.CommandLineArgs;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandLineArgsParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.ConnectionFactory;
import java.util.Map;

@SpringBootApplication
@EnableJms
@EnableJpaRepositories
@EnableTransactionManagement
@Slf4j
public class HokanNgSpringBootServices {

    private static String JMS_BROKER_URL = "tcp://localhost:61616";

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(JMS_BROKER_URL);
        activeMQConnectionFactory.setTrustAllPackages(true);
        return activeMQConnectionFactory;
    }

    public static void main(String[] args) {
        CommandLineArgsParser parser = new CommandLineArgsParser(args);
        Map<CommandLineArgs, String> parsed = parser.parseArgs();
        String url = parsed.get(CommandLineArgs.JMS_BROKER_URL);
        if (url != null) {
            JMS_BROKER_URL = url;
        }
        log.debug("JMS_BROKER_URL: {}", JMS_BROKER_URL);

        SpringApplication.run(HokanNgSpringBootServices.class, args);
    }

}
