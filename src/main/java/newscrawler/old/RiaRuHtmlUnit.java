package newscrawler.old;


import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import java.io.IOException;

public class RiaRuHtmlUnit {

    public static void main(String[] args) throws IOException {
        System.out.println("Start Html Unit");

//        WebClient webClient = new WebClient();
//
//        Page page = webClient.getPage("http://ria.ru/lenta/");
//        System.out.println(page.toString());
//
////        try (final WebClient webClient = new WebClient()) {
////            final HtmlPage page = webClient.getPage("http://myserver/test.html");
////        }


//        try {
//            final WebClient webClient = new WebClient(BrowserVersion.CHROME);
//            webClient.getOptions().setJavaScriptEnabled(true);
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getOptions().setUseInsecureSSL(true);
//            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//            webClient.getCookieManager().setCookiesEnabled(true);
//            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//            webClient.getOptions().setThrowExceptionOnScriptError(false);
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getCookieManager().setCookiesEnabled(true);
//
//
//            WebRequest request = new WebRequest(new URL("http://ria.ru/lenta/"));
//            HtmlPage page = webClient.getPage(request);
//
//            System.out.println(page.toString());
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getCookieManager().setCookiesEnabled(true);


            final HtmlPage page = webClient.getPage("http://ria.ru/lenta/");
//            Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

//            final String pageAsXml = page.asXml();
//            System.out.println(pageAsXml);


            //final String pageAsText = page.asText();



//            System.out.println(pageAsText);


//            DomNode domNode = page.querySelector(".b-pager__button-text");
//            Iterable<HtmlElement> htmlElementDescendants = domNode.getHtmlElementDescendants();
//            for (HtmlElement htmlElementDescendant : htmlElementDescendants) {
//                System.out.println(htmlElementDescendant.toString());
//            }


            HtmlSpan firstByXPath = (HtmlSpan)page.getFirstByXPath("//span[@class='b-pager__button-text']");

            System.out.println("firstByXPath before click");
            Page afterClick = firstByXPath.click();
            System.out.println("firstByXPath after click");


            System.out.println(afterClick.toString());


            System.out.println("that's all");
        }


    }
}
