package newscrawler;

import newscrawler.model.RiaArticle;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RiaRuPageByPageFromArchiveUrl {

    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    //    static final String NEWS_URL = "https://ria.ru/lenta/";
    static final String NEWS_URL = "https://ria.ru/archive/more.html?date=";

    static final String DATE_FORMAT_HTML_PARAM_VALUE = "yyyyMMdd'T'HHmmss";
    static final String DATE_FORMAT_RIA_ARTICLE = "dd.MM.yyyy HHmm";

    private static Logger logger = Logger.getLogger(RiaRuPageByPageFromArchiveUrl.class);


    public static void main(String[] args) throws IOException, IllegalAccessException, ParseException, NoSuchFieldException {

        //getArticlesFromToTEST();
        getArticlesFromToGetCurrentNewsForLastHours(1);


    }

    public static void main0(String[] args) throws IOException, ParseException, NoSuchFieldException, IllegalAccessException {


        Map<String, RiaArticle> articles = getArticlesTo(new Date());

        for (RiaArticle article : articles.values()) {
            logger.debug(article);
        }


//        String format = new SimpleDateFormat(DATE_FORMAT_HTML_PARAM_VALUE).format(new Date());
//        logger.debug("FORMAT IS:" + format);
//        Document doc = Jsoup.connect(NEWS_URL + format)
////        Document doc = Jsoup.connect(NEWS_URL + "20180317T145600")
//                .userAgent(USER_AGENT)
//                .get();
//        Elements masthead = doc.select("div.b-list");
//
//        //masthead.select()
//        //logger.debug(masthead);
//
//        logger.debug("===========================");
//
//        //logger.debug(new SimpleDateFormat(DATE_FORMAT_HTML_PARAM_VALUE).format(new Date()));
//
//
//        List<RiaArticle> articles = new ArrayList<>();
//
//        for (Element element : masthead.select("div.b-list__item")) {
//            String articleLink = element.select("a").attr("href");
//            logger.debug("articleLink=" + articleLink);
//
//            String imageLink = element.select("span.b-list__item-img-ind").select("img").attr("src");
//            logger.debug("imageLink=" + imageLink);
//
//            String title = element.select("span.b-list__item-title").select("span").first().text();
//            logger.debug("title=" + title);
//
//            String time = element.select("div.b-list__item-info").select("div.b-list__item-time").select("span").first().text();
//            logger.debug("time=" + time);
//
//            String date = element.select("div.b-list__item-date").select("span").first().text();
//            logger.debug("date=" + date);
//
//
//            // create model
//            RiaArticle riaArticle = new RiaArticle();
//            riaArticle.articleLink = articleLink;
//            riaArticle.date = date + " " + time;
//            riaArticle.title = title;
//            riaArticle.imageLink = imageLink;
//
//            articles.add(riaArticle);
//
//        }

        Map.Entry<String, RiaArticle> lastViaReflection = getLastViaReflection(articles);
        Date lastArticleDate = new SimpleDateFormat().parse(lastViaReflection.getValue().getDate());

        logger.debug(lastArticleDate);
        logger.debug(new SimpleDateFormat(DATE_FORMAT_HTML_PARAM_VALUE).format(lastArticleDate));


        Map<String, RiaArticle> articles2 = getArticlesTo(lastArticleDate);

        for (RiaArticle article : articles2.values()) {
            logger.debug(article);
        }

        logger.debug("============");

        mergeArticles(articles, articles2);

        for (RiaArticle article : articles.values()) {
            logger.debug(article);
        }
        logger.debug(articles.size());

    }

    public static void mergeArticles(Map<String, RiaArticle> articles, Map<String, RiaArticle> articles2) {
        for (RiaArticle riaArticle : articles2.values()) {
            if (!articles.containsKey(riaArticle.articleLink)) {
                articles.put(riaArticle.articleLink, riaArticle);
            }
        }
    }




    /*
    Нужна по сути pagination для получения всех новостей сплошь до нужного периода.
    Получение всех новостей с даты "С" по дату "По"


    1. Получаем за дату "По"
    2. Находим последнюю новость L
    3. Если дата новости L больше даты "С" то шаг 1.
    4. Если дата новости L меньше даты "С" то имеем список всех новостей.


     */


//    public static void getArticlesFromToTEST() throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {
//        LocalDateTime to = LocalDateTime.of(LocalDate.of(2018, Month.APRIL, 17), LocalTime.of(20, 9));
//        LocalDateTime from = LocalDateTime.of(LocalDate.of(2018, Month.APRIL, 17), LocalTime.of(17, 9));
//        Date from1 = convertToDateViaInstant(from);
//        Date to1 = convertToDateViaInstant(to);
//        Map<String, RiaArticle> articlesFromTo = getArticlesFromTo(from1, to1);
//
//        for (RiaArticle riaArticle : articlesFromTo.values()) {
//            logger.debug(riaArticle);
//        }
//
//        logger.debug(articlesFromTo.size());
//
//    }

    public static Map<String, RiaArticle> getArticlesFromToGetCurrentNewsForLastHours(int hours) throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = LocalDateTime.from(to).minusHours(hours);
        Date from1 = convertToDateViaInstant(from);
        Date to1 = convertToDateViaInstant(to);

        logger.debug(from1);
        logger.debug(to1);

        Map<String, RiaArticle> articlesFromTo = getArticlesFromTo(from1, to1);

//        for (RiaArticle riaArticle : articlesFromTo.values()) {
//            logger.debug(riaArticle.title);
//        }

        boolean needToProcessAgain = true;
        int iterCount = 0;

        while(needToProcessAgain) {
            iterCount++;
            logger.debug("start process filter again!" + "(" + iterCount + ")");
            logger.debug("before SIZE " + articlesFromTo.entrySet().size());
            //FILTER NEWS!!!
            articlesFromTo.entrySet().removeIf(RiaRuPageByPageFromArchiveUrl::filterBySportNews);
            logger.debug("after (" + iterCount + ") SIZE " + articlesFromTo.entrySet().size());

            // if present of unprocessed
            boolean afterIterationNeedToProcessAgain = false;
            for (RiaArticle value : articlesFromTo.values()) {
                if(value.isNeedToRecheck()) {
                    logger.debug("Exists unprocessed - need to recheck" );
                    afterIterationNeedToProcessAgain = true;
                    break;
                }
            }
            needToProcessAgain = afterIterationNeedToProcessAgain;
        }
        return articlesFromTo;


    }


    public static Map<String, RiaArticle> getArticlesFromTo(Date from, Date to) throws IOException, NoSuchFieldException, IllegalAccessException, ParseException {
        Map<String, RiaArticle> result = new LinkedHashMap<>();


        Date lastArticleDate;
        do {
            Map<String, RiaArticle> articlesTo = getArticlesTo(to);

            Map.Entry<String, RiaArticle> lastViaReflection = getLastViaReflection(articlesTo);
            RiaArticle riaArticle = lastViaReflection.getValue();
            lastArticleDate = new SimpleDateFormat("yyyyMMdd HH:mm").parse(riaArticle.getDate());

            mergeArticles(result, articlesTo);

            to = lastArticleDate;

        }
        while (from.before(to));


        return result;


    }

    public static Map<String, RiaArticle> getArticlesTo(Date to) throws IOException {
        String format = new SimpleDateFormat(DATE_FORMAT_HTML_PARAM_VALUE).format(to);
//        logger.debug("FORMAT IS:" + format);
//        Document doc = Jsoup.connect(NEWS_URL + format)
//                .userAgent(USER_AGENT)
//                .timeout(20000)
//                .get();

        Document doc = null;

        try {
            Connection.Response response =
                    Jsoup.connect(NEWS_URL + format)
                            .userAgent(USER_AGENT)
                            .timeout(10000)
                            .execute();
            doc = response.parse();

        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();

            if (statusCode == 503) {

                // try to reconnect
                try {
                    Connection.Response response =
                            Jsoup.connect(NEWS_URL + format)
                                    .userAgent(USER_AGENT)
                                    .timeout(10000)
                                    .execute();


                    doc = response.parse();

                } catch (HttpStatusException e2) {
                    statusCode = e2.getStatusCode();

                    logger.debug("FAIL!!! " + statusCode);

                }
            }


            if (doc == null) {
                throw new RuntimeException("Fail.....");
            }
        }


        Elements masthead = doc.select("div.list-items-loaded");

        //masthead.select()
        //logger.debug(masthead);

        logger.debug("===========================");

        //logger.debug(new SimpleDateFormat(DATE_FORMAT_HTML_PARAM_VALUE).format(new Date()));


        Map<String, RiaArticle> articles = new LinkedHashMap<>();

        for (Element element : masthead.select("div.list-item")) {
            String articleLink = element.select("a").attr("href");


            //logger.debug("articleLink=" + articleLink);

            String imageLink = element.select("span.list__item-img-ind").select("img").attr("src");
            //logger.debug("imageLink=" + imageLink);

            String title = element.select("div.list-item__title").text();
            //logger.debug("title=" + title);

            String dateAndTime = parseDateFromLink(articleLink);
            dateAndTime += " " + element.select("div.list-item__info").select("div.list-item__date").text();

            //logger.debug("dateAndTime=" + dateAndTime);

//            String date = element.select("div.list__item-date").select("span").first().text();
//            //logger.debug("date=" + date);


//            String viewsCount = element.select("div.list__item-statistic").select("div.b-statistic").select("span.b-statistic__number").last().text();
            String viewsCount = element.select("div.list-item__views").select("div.list-item__views-text").text();
//            String viewsCount = "0"; // RIA excluded view count from list page!!!

            // create model
            RiaArticle riaArticle = new RiaArticle();
            String articleUrlPrefix = articleLink.startsWith("https://") ? "" : "https://ria.ru";
            riaArticle.articleLink = articleUrlPrefix + articleLink;
            riaArticle.setDate(dateAndTime);
            riaArticle.setTitle(title);
            riaArticle.imageLink = imageLink;
            riaArticle.setViewsCount(Long.parseLong(viewsCount));

            articles.put(riaArticle.articleLink, riaArticle);


        }

        logger.debug("====");
        logger.debug(articles.size());

        return articles;


    }

    private static String parseDateFromLink(String articleLink) {
        int startIndex = articleLink.indexOf("/");
        int endIndex = articleLink.indexOf("/", startIndex + 1);
        return articleLink.substring(startIndex + 1, endIndex);
    }


    private static <K, V> Map.Entry<K, V> getLastViaReflection(Map<K, V> map) throws NoSuchFieldException, IllegalAccessException {
        Field tail = map.getClass().getDeclaredField("tail");
        tail.setAccessible(true);
        return (Map.Entry<K, V>) tail.get(map);
    }

    private static Date toDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        return date;
    }


    private static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

//    private static boolean filterBySportNews(Map.Entry<String, RiaArticle> entry) {
//        boolean show = true;
//        if (RiaUI.checkRedirects) {
//            try {
//                String url = entry.getValue().articleLink;
//                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//                conn.setInstanceFollowRedirects(false);
//                conn.setRequestProperty("User-Agent", USER_AGENT);
//                conn.connect();
//                switch (conn.getResponseCode()) {
//                    case HttpURLConnection.HTTP_MOVED_PERM:
//                    case HttpURLConnection.HTTP_MOVED_TEMP:
//                        String location = conn.getHeaderField("Location");
//                        location = URLDecoder.decode(location, "UTF-8");
//
//                        URL base;
//                        base = new URL(url);
//                        URL next;  // Deal with relative URLs
//                        next = new URL(base, location);
//                        url = next.toExternalForm();
//                        if (url.contains("rsport.ria.ru")) {
//                            show = false;
//                        }
//                }
//                conn.disconnect();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        if (!show) {
//            logger.debug("Skipped " + entry.getValue().articleLink);
//        } else {
//            logger.debug("NOT skipped " + entry.getValue().articleLink);
//        }
//
//        return !show;
//    }


    private static boolean filterBySportNews(Map.Entry<String, RiaArticle> entry) {
        boolean show = true;
        if (RiaUI.checkRedirects && entry.getValue().isNeedToRecheck()) {
            try {
                URL resourceUrl, base, next;
                HttpURLConnection conn;
                String location;

                String url = entry.getValue().articleLink;
                //logger.debug("start url:" + entry.getValue().articleLink);
                while (true) {
                    resourceUrl = new URL(entry.getValue().articleLink);
                    conn = (HttpURLConnection) resourceUrl.openConnection();

                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.setInstanceFollowRedirects(false);   // Make the logic below easier to detect redirections
                    conn.setRequestProperty("User-Agent", USER_AGENT);


                    logger.debug("conn.getResponseCode()=" + conn.getResponseCode());

                    switch (conn.getResponseCode()) {
                        case HttpURLConnection.HTTP_MOVED_PERM:
                        case HttpURLConnection.HTTP_MOVED_TEMP:
                            location = conn.getHeaderField("Location");
                            location = URLDecoder.decode(location, "UTF-8");
                            base = new URL(url);
                            next = new URL(base, location);  // Deal with relative URLs
                            url = next.toExternalForm();
                            logger.debug("url = " + url);
                            if (url.contains("rsport.ria.ru")) {
                                logger.debug(" SKIPPED ");
                                entry.getValue().setNeedToRecheck(false);
                                show = false;
                                break;
                            }
                            else {
                                entry.getValue().setNeedToRecheck(false);
                                break;
                            }

                        case HttpURLConnection.HTTP_UNAVAILABLE:

                            entry.getValue().setNeedToRecheck(true);
                            break;

                        default:
                            entry.getValue().setNeedToRecheck(false);
                    }

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//        if (!show) {
//            logger.debug("Skipped " + entry.getValue().articleLink + ", entry.getValue().isNeedToRecheck()=" + entry.getValue().isNeedToRecheck());
//        } else {
//            logger.debug("NOT skipped " + entry.getValue().articleLink + ", entry.getValue().isNeedToRecheck()=" + entry.getValue().isNeedToRecheck());
//        }

        return !show;
    }

}
