package org.freakz.hokan_ng_springboot.bot.service.alko;

import lombok.extern.slf4j.Slf4j;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.models.AlkoSearchResults;
import org.freakz.hokan_ng_springboot.bot.service.annotation.ServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.service.wwwfetcher.WWWPageFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio (petri.airio@soikea.com) 22/11/2016 / 8.55
 */
@Service
@Slf4j
public class AlkoSearchServiceRequestHandler implements AlkoSearchService {

    private WWWPageFetcher wwwPageFetcher;

    @Autowired
    public void setWwwPageFetcher(WWWPageFetcher wwwPageFetcher) {
        this.wwwPageFetcher = wwwPageFetcher;
    }

    private String fetchAlkoPage(String url) {
        String output;
        output = wwwPageFetcher.fetchWWWPage(url, false);
        return output;
    }

    @Override
    public AlkoSearchResults alkoSearch(String search) {
        AlkoSearchResults alkoSearchResults = new AlkoSearchResults();
        List<String> results = new ArrayList<>();
        alkoSearchResults.setResults(results);

        try {
            String term = URLEncoder.encode(search, "UTF-8");
            String url = "https://www.alko.fi/tuotehaku?SortingAttribute=random_185&SearchParameter=&SelectedFilter=&SearchTerm=" + term;
            String page = fetchAlkoPage(url);
            Document doc = Jsoup.parse(page);
            Elements products = doc.getElementsByClass("product-name-wrap");
            Elements prices = doc.getElementsByClass("product-price");
            Elements tastes = doc.getElementsByClass("taste-txt");
            Elements volumes = doc.getElementsByClass("product-volume");


            for (int i = 0; i < products.size(); i++) {
                Element element = products.get(i);
                String product = element.text();
                String price = prices.get(i).text().replaceFirst(" ", ".");
                String taste = tastes.get(i).text();
                String volume = volumes.get(i).text();
                results.add(String.format("%s %s , %s, %s€", product, volume, taste, price));

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return alkoSearchResults;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.ALKO_SEARCH_REQUEST)
    public void alkoSearchRequestHandler(ServiceRequest request, ServiceResponse response) {
        String search = (String) request.getParameters()[0];
        AlkoSearchResults alkoSearchResults = alkoSearch(search);
        response.setResponseData(request.getType().getResponseDataKey(), alkoSearchResults);

    }

}
