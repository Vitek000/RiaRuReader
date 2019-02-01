package newscrawler.old;

import newscrawler.model.RiaArticle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiaRuLentaJsoup {


    static List<RiaArticle> articles;

    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    static final String NEWS_URL = "https://ria.ru/lenta/";


    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect(NEWS_URL)
                .userAgent(USER_AGENT)
                .get();
        Elements masthead = doc.select("div.b-list");

        //masthead.select()
        System.out.println(masthead);

        System.out.println("===========================");
//
//                Elements elements = masthead.select("div.b-list__item")
//                        .select("div.b-list__item-info")
//                        .select("div.b-list__item-time")
//                .select("span")
//                        ;
//        //);
//
//
//        int i=0;
//        for (Element element : elements) {
//            i++;
//            System.out.println(element.ownText());
//        }
//        //System.out.println(i);

        List<RiaArticle> articles = new ArrayList<>();

        for (Element element : masthead.select("div.b-list__item")) {
            String articleLink = element.select("a").attr("href");
            System.out.println("articleLink=" + articleLink);

            String imageLink = element.select("span.b-list__item-img-ind").select("img").attr("src");
            System.out.println("imageLink=" + imageLink);

            String title = element.select("span.b-list__item-title").select("span").first().text();
            System.out.println("title=" + title);

            String time = element.select("div.b-list__item-info").select("div.b-list__item-time").select("span").first().text();
            System.out.println("time=" + time);

            String date = element.select("div.b-list__item-date").select("span").first().text();
            System.out.println("date=" + date);


            // create model
            RiaArticle riaArticle = new RiaArticle();
            riaArticle.articleLink = articleLink;
            riaArticle.setDate(date + " " + time);
            riaArticle.setTitle(title);
            riaArticle.imageLink = imageLink;

            articles.add(riaArticle);

        }


        System.out.println("====");
        System.out.println(articles.size());


    }
}
