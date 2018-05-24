package org.freakz.hokan_ng_springboot.bot.services.service.locations;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

@Service
public class LocationsServiceImpl implements LocationsService {

    private static final String LOCATIONS_FILE_URL = "http://download.maxmind.com/download/worldcities/worldcitiespop.txt.gz";

    @Override
    public int fetchLocations() {
        int count = -1;
        try {
            StringBuilder buffer = new StringBuilder();
            count = downloadLocationsFile(buffer, LOCATIONS_FILE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private int downloadLocationsFile(final StringBuilder buffer, String remoteUrl) throws Exception {
        URL url = new URL(remoteUrl);
        //open the connection to the above URL.
        URLConnection urlConnection = url.openConnection();
        GZIPInputStream gzipInputStream = new GZIPInputStream(urlConnection.getInputStream());
        Reader decoder = new InputStreamReader(gzipInputStream, "US-ASCII");
        BufferedReader buffered = new BufferedReader(decoder);
        int linesRead = 0;
        do {
            String line = buffered.readLine();
            if (line != null) {
                linesRead++;
                buffer.append(line);
            } else {
                break;
            }
        } while (true);
        return linesRead;
    }

}
