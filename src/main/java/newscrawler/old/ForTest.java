package newscrawler.old;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class ForTest {
    public static void main(String[] args) throws IOException {
//        String url = "https://ria.ru/20181220/1548369668.html"; // rtsport!
        String url = "https://ria.ru/20181220/1548370002.html";


        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.connect();

        switch (conn.getResponseCode())
        {
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
                String location = conn.getHeaderField("Location");
                location = URLDecoder.decode(location, "UTF-8");
                URL base     = new URL(url);
                URL next     = new URL(base, location);  // Deal with relative URLs
                url      = next.toExternalForm();
                System.out.println(url);

        }
        conn.disconnect();

    }
}
