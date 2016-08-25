package org.freakz.hokan_ng_springboot.bot.service.wwwfetcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by petria on 24/08/16.
 * -
 */
@Service
@Slf4j
public class WWWPageFetcherImpl implements WWWPageFetcher {

    private final LoadingCache<String, String> cache;

    public WWWPageFetcherImpl() {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(
                new CacheLoader<String, String>() {
                    @Override
                    public String load(String url) throws Exception {
                        return fetchUrl(url);
                    }
                }
        );
    }

    public static String fetchUrl(String url) {
        log.debug("Fetching url: {}", url);
        WebDriver driver = new HtmlUnitDriver(true); //the param true turns on javascript.
        driver.get(url);
        String output = driver.getPageSource();
        driver.quit();
        return output;
    }

    @Override
    public String fetchWWWPage(String url) {
        try {
            return cache.get(url);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "n/a";
        }
    }

}
