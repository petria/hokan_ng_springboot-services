package org.freakz.hokan_ng_springboot.bot.services.service.wwwfetcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by petria on 24/08/16.
 * -
 */
@Service
public class WWWPageFetcherImpl implements WWWPageFetcher {

    private static final Logger log = LoggerFactory.getLogger(WWWPageFetcherImpl.class);

    private final LoadingCache<CacheParameters, String> cache;

    public WWWPageFetcherImpl() {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(
                new CacheLoader<CacheParameters, String>() {
                    @Override
                    public String load(CacheParameters parameters) throws Exception {
                        return fetchWWWPageNoCache(parameters.url, parameters.useJavaScript);
                    }
                }
        );
    }

    @Override
    public String fetchWWWPageNoCache(String url, boolean useJavaScript) {
        log.debug("Fetching url: {}", url);
        WebDriver driver = new HtmlUnitDriver(useJavaScript);
        driver.get(url);
        String output = driver.getPageSource();
        driver.quit();
        return output;
    }

    @Override
    public String fetchWWWPage(String url, boolean useJavaScript) {
        try {
            CacheParameters parameters = new CacheParameters();
            parameters.url = url;
            parameters.useJavaScript = useJavaScript;
            return cache.get(parameters);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "n/a";
        }
    }

    public static class CacheParameters {
        public String url;
        public boolean useJavaScript;
    }
}
