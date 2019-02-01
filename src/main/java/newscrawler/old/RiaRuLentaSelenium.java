package newscrawler.old;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.function.Function;

public class RiaRuLentaSelenium {
    private WebDriver webDriver;
    WebDriverWait wait;

    public static void main(String[] args) throws URISyntaxException {

        RiaRuLentaSelenium riaRuLentaSelenium = new RiaRuLentaSelenium();
        riaRuLentaSelenium.doWork();

    }

    public void doWork() throws URISyntaxException {
        setProperties();
        webDriver = new ChromeDriver();
        wait = new WebDriverWait(webDriver, 5);

        //webDriver.manage().window().maximize();

        webDriver.get("https://ria.ru/lenta");
        waitForPageLoad(webDriver);


        // Find Last records time!


        WebElement moreButton = webDriver.findElement(By.className("b-pager__button-text"));
        moreButton.click();
        //waitForPageLoad(webDriver);
        System.out.println("ALLL");


        String source = webDriver.getPageSource();
        System.out.println("SOURCE SIZE = " + source.length());



//        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//
//        //WebElement myDynamicElement = webDriver.findElement(By.className("b-pager__button-text"));
//                WebElement myDynamicElement = (new WebDriverWait(webDriver, 10))
//                .until(ExpectedConditions.visibilityOfElementLocated(By.className("b-pager__button-text")));
//        myDynamicElement.click();


        WebElement myDynamicElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("b-pager__button-text")));

        //scrolling
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", myDynamicElement);

        //clickable
        wait.until(ExpectedConditions.elementToBeClickable(By.className("b-pager__button-text")));

        System.out.println("before click!");

        moreButton = webDriver.findElement(By.className("b-pager__button-text"));

        Actions action = new Actions(webDriver);
        action.moveToElement(moreButton).click().perform();
//        moreButton.click();

        System.out.println("after click!");


        source = webDriver.getPageSource();
        System.out.println("SOURCE SIZE = " + source.length());













        myDynamicElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("b-pager__button-text")));

        //scrolling
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", myDynamicElement);

        //clickable
        wait.until(ExpectedConditions.elementToBeClickable(By.className("b-pager__button-text")));

        System.out.println("before click2!");

        moreButton = webDriver.findElement(By.className("b-pager__button-text"));

        action = new Actions(webDriver);
        action.moveToElement(moreButton).click().perform();
//        moreButton.click();

        System.out.println("after click2!");


        source = webDriver.getPageSource();
        System.out.println("SOURCE SIZE = " + source.length());










        myDynamicElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("b-pager__button-text")));

        //scrolling
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", myDynamicElement);

        //clickable
        wait.until(ExpectedConditions.elementToBeClickable(By.className("b-pager__button-text")));

        System.out.println("before click3!");

        moreButton = webDriver.findElement(By.className("b-pager__button-text"));

        action = new Actions(webDriver);
        action.moveToElement(moreButton).click().perform();
//        moreButton.click();

        System.out.println("after click3!");


        source = webDriver.getPageSource();
        System.out.println("SOURCE SIZE = " + source.length());



//        WebElement myDynamicElement = (new WebDriverWait(webDriver, 10))
//                .until(ExpectedConditions.presenceOfElementLocated(By.className("b-pager__button-text")));
//        if(myDynamicElement.isDisplayed() && myDynamicElement.isEnabled()) {
//            myDynamicElement.click();
//            System.out.println("!!!!!! CLICKED");
//        }
//        else {
//            System.out.println("!!!!!! NOT CLICKED");
//        }


//        WebElement myDynamicElement2 = (new WebDriverWait(webDriver, 10))
//                .until(ExpectedConditions.visibilityOfElementLocated(By.className("b-pager__button-text")));
//        while (!myDynamicElement2.isDisplayed() || !myDynamicElement2.isEnabled()) {
//            myDynamicElement2.click();
//            System.out.println("!!!!!! CLICKED2");
//        }


//        WebElement element2 = webDriver.findElement(By.className("b-pager__button-text"));
//        wait.until(ExpectedConditions.visibilityOf(element2));
//        System.out.println("waiting done!!!");

//        WebElement myDynamicElement = (new WebDriverWait(webDriver, 10))
//                .until(ExpectedConditions.visibilityOfElementLocated(By.className("b-pager__button-text")));
//        System.out.println("waiting done!!!");
//
//
//        myDynamicElement.click();
//        myDynamicElement = (new WebDriverWait(webDriver, 10))
//                .until(ExpectedConditions.visibilityOfElementLocated(By.className("b-pager__button-text")));
//        System.out.println("waiting done2!!!");
//
//        myDynamicElement.click();
//        myDynamicElement = (new WebDriverWait(webDriver, 10))
//                .until(ExpectedConditions.visibilityOfElementLocated(By.className("b-pager__button-text")));
//        System.out.println("waiting done3!!!");




    }



    public static void waitForPageLoad(WebDriver webDriver) {

        Wait<WebDriver> wait = new WebDriverWait(webDriver, 30);
        wait.until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                System.out.println("Current Window State       : "
                        + String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState")));
                return String
                        .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
                        .equals("complete");
            }
        });
    }


    private void setProperties() throws URISyntaxException {
        URL chromeURL = getClass().getClassLoader().getResource("driver" + File.separator + "chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", Paths.get(chromeURL.toURI()).toFile().getAbsolutePath());
//
//        URL firefoxURL = getClass().getClassLoader().getResource("driver" + File.separator + "geckodriver.exe");
//        System.setProperty("webdriver.gecko.driver", Paths.get(firefoxURL.toURI()).toFile().getAbsolutePath());
//
//        URL ieURL = getClass().getClassLoader().getResource("driver" + File.separator + "IEDriverServer.exe");
//        System.setProperty("webdriver.ie.driver", Paths.get(ieURL.toURI()).toFile().getAbsolutePath());
    }



}

