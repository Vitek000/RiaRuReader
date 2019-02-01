package newscrawler.old;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RiaRuLenta {

    public static void main(String[] args) throws IOException {

        /*

        План:
        1. Читаем из ria.ru
        2. находим наше последнее время читки:
            2.1 Либо больше последнего записанного в бд
            2.2. но не более самой старой новости (пока 1 день)
        3. Находим все новости больше времени последнего времени читки
        4. Отображаем это красиво :)
        4.1. сделать слайдер влево/вправо, попробовать JavaFX


         */
        String urlGet = "https://ria.ru/lenta/";

        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(urlGet);

        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
//                new InputStreamReader(response.getEntity().getContent(), "cp1251"));
                new InputStreamReader(response.getEntity().getContent(), "utf-8"));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result);


        // extract news from page


    }



}
