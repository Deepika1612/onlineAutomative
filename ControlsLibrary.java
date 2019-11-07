package com.mab.test.hybrid;
/**
 * Created by Mitchell's &amp; Butlers plc.
 *
 * @Author: Jabez James
 * Date: 05/06/18
 * This library provides generic methods to interact with UI controls
 */

import com.mab.test.framework.helpers.ReportHelper;
import com.mab.test.framework.helpers.UrlBuilder;
import com.mab.test.framework.helpers.utils.DateUtil;
import com.mab.test.pageobjects.mab.harvester.HomePage;
import com.testautomationguru.ocular.Ocular;
import com.testautomationguru.ocular.comparator.OcularResult;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteTouchScreen;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

import org.apache.commons.io.FileUtils;

import com.mab.test.framework.helpers.LoadProperties;
import com.mab.test.framework.helpers.WebDriverHelper;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static io.restassured.RestAssured.given;

public abstract class ControlsLibrary {
    private static final long DRIVER_WAIT_TIME = 7;
    private static final Logger LOG = LoggerFactory.getLogger(ControlsLibrary.class);
    private static ResourceBundle onlineProps;
    private static Properties props;
    @Getter
    protected static WebDriverWait wait;
    @Getter
    protected static WebDriver webDriver = null;
    public WebDriverHelper wdh;

    public ControlsLibrary() {

        this.wait = new WebDriverWait(webDriver, DRIVER_WAIT_TIME);
        this.onlineProps = ResourceBundle.getBundle("properties/online/messages");
        this.props = LoadProperties.getProps();
    }

    /**
     * Initializes the WebDriver,WebDriverWait,Properties and ResourceBundle objects .
     */
    public static void InitializeLibrary() {
        webDriver = WebDriverGenerator.getWebDriver();
        //webDriver.manage().window().maximize();
        wait = new WebDriverWait(webDriver, DRIVER_WAIT_TIME);
        onlineProps = ResourceBundle.getBundle("properties/online/messages");
        props = LoadProperties.getProps();
    }

    /**
     * Returns the current Url from active page
     */
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    /**
     * Checks the current URL contents and returns true or false
     *
     * @param url Expected url contents
     * @return true or false
     */
    public static Boolean checkCurrentUrlContains(String url) {
        sleep(3000);
        try {
            String currenturl = webDriver.getCurrentUrl().toString();
            log(currenturl);
            ReportHelper.Log("Current Browser URL : " + currenturl, "INFO");
            ReportHelper.Log("Expected Browser URL : " + url, "INFO");
            if (currenturl.contains(url)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ReportHelper.Log(ex.getMessage(), "INFO");
            return false;
        }
        // return new WebDriverWait(webDriver, DRIVER_WAIT_TIME).until(ExpectedConditions.urlContains(title));
        // return getCurrentUrl().contains(title);
    }


    public static String retrieveCurrentUrl() {
        sleep(3000);
        try {
            String currenturl = webDriver.getCurrentUrl().toString();
            log(currenturl);
            ReportHelper.Log("Current Browser URL : " + currenturl, "INFO");
            return currenturl;
        } catch (Exception ex) {
            ReportHelper.Log(ex.getMessage(), "INFO");
            return null;
        }
        // return new WebDriverWait(webDriver, DRIVER_WAIT_TIME).until(ExpectedConditions.urlContains(title));
        // return getCurrentUrl().contains(title);
    }

    public String getCurrentPageTitle() {
        timeUnitWait(3);
        return getWebDriver().getTitle();
    }

    public String getPageContent() {
        Document doc = Jsoup.parse(getWebDriver().getPageSource());
        return doc.text();
    }


    /**
     * getWebDriver()
     * An expectation for checking that the title contains a case-sensitive
     * substring
     *
     * @param title the fragment of title expected
     * @return true when the title matches, false otherwise
     */
    public boolean checkPageTitleContains(String title) {

        return new WebDriverWait(webDriver, DRIVER_WAIT_TIME).until(ExpectedConditions.titleContains(title));
    }

    public WebElement waitForExpectedElement(final By by, long waitTimeInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, waitTimeInSeconds);
            return wait.until(visibilityOfElementLocated(by));
        } catch (NoSuchElementException e) {
            LOG.info(e.getMessage());
            return null;
        } catch (TimeoutException e) {
            LOG.info(e.getMessage());
            return null;
        }
    }

    protected static ExpectedCondition<WebElement> visibilityOfElementLocated(final By by) throws NoSuchElementException {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage());
                }
                WebElement element = webDriver.findElement(by);
                sleep(2000);
                //highlightElement(element);
                scrollToElement(element);

                return element.isDisplayed() ? element : null;
            }
        };
    }

    public static void highlightElement(WebElement element) {

        //scrollToElement(element);
        for (int i = 0; i < 2; i++) {
            JavascriptExecutor js = ((JavascriptExecutor) webDriver);
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, " border: 2px solid yellow;");
            sleep(1000);
            // js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
        }
    }

    public static WebElement waitForExpectedElement(final By by) {

        WebElement we = wait.until(visibilityOfElementLocated(by));
        //highlightElement(we);
        log(by.toString() + " located");
        if (we.equals(null)) {
            ReportHelper.Log("Element of technical name " + by + " is not visible", "FAIL");
        }
        return we;
    }

    /**
     * Allows selenium to pause for a set amount of time
     *
     * @param seconds time to wait in seconds
     */
    public static void timeUnitWait(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Fluent Wait Waits for an specific amount of time or until an element is
     * displayed
     */
    public WebElement waitForPresenceOfElement(final By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForPresenceOfElement(String cssSelector) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
    }

    protected List<WebElement> visibilityOfAllElementsLocatedBy(final By by) {
        return (new WebDriverWait(webDriver, DRIVER_WAIT_TIME)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    protected List<WebElement> visibilityOfAllElementsLocatedBy(final By by, long waitTimeInSeconds) {
        return (new WebDriverWait(webDriver, waitTimeInSeconds)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    public static void jClick(WebElement element) {
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus();", element);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click()", element);
    }

    public static double jClick(By element) {
        double loadTime;
        WebElement we = webDriver.findElement(element);
        // scrollToElement(we);
        timeUnitWait(1);

        ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus();", we);

        //  ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", we);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click()", we);
        loadTime = (Double) ((JavascriptExecutor) webDriver).executeScript(
                "return (window.performance.timing.loadEventEnd - window.performance.timing.navigationStart) / 1000");
        return loadTime;
    }

    public static Boolean jsClick(By by) throws Exception {
        double loadTime;

        if (verifyElementPresence(by)) {
            WebElement we = webDriver.findElement(by);
            //scrollToElement(we);
            timeUnitWait(1);
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus();", we);
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].click()", we);
            return true;
        } else
            return false;
    }

    public static Boolean jsClick(WebElement we) throws Exception {
        double loadTime;

        if (we != null) {
            //scrollToElement(we);
            timeUnitWait(1);
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus();", we);
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].click()", we);
            return true;
        } else
            return false;
    }


    public void setFocus() {
        ((JavascriptExecutor) webDriver).executeScript("window.focus();");
        getWebDriver().switchTo().defaultContent();
        ((JavascriptExecutor) webDriver).executeScript("window.focus();");
    }

    public static void setoFocusonElement(By element) {

        WebElement we = webDriver.findElement(element);
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].click()", we);
    }

    public void hoverAndClick(WebElement element) {
        Actions builder = new Actions(webDriver);
        builder.moveToElement(element).build().perform();
    }

    /**
     * Waits for a single element to be clickable and returns it
     *
     * @param locator for the element
     * @return web element found
     */
    public static WebElement waitForElementClickable(final By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for a single element to be Disappear
     *
     * @param locator for the element
     */
    public Boolean waitForElementToDisappear(String locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(locator)));
    }

    //With wait
    protected static boolean isElementPresent(By by) {
        try {
            new WebDriverWait(getWebDriver(), DRIVER_WAIT_TIME).until(visibilityOfElementLocated(by));
        } catch (TimeoutException exception) {
            LOG.info(exception.getMessage());
            return false;
        }
        return true;
    }

    public static boolean isEnabled(By by) {
        try {
            WebElement Field = getWebDriver().findElement(by);

            if (Field.isEnabled()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return Boolean.FALSE;
        }
    }

    public static boolean isDisabled(By by) {

        WebElement Field = getWebDriver().findElement(by);
        if (Field.isEnabled()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }


    //Without wait
    protected static boolean isPresent(By by) {
        try {
            getWebDriver().findElement(by);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }


    protected WebElement waitForElementAvailableAndVisible(By by) {
        return (new WebDriverWait(getWebDriver(), DRIVER_WAIT_TIME)).until(visibilityOfElementLocated(by));
    }

    public WebElement waitForElementDisplayedAndClickable(By by) {
        return (new WebDriverWait(getWebDriver(), DRIVER_WAIT_TIME)).until(visibilityOfElementLocated(by));
    }

    protected boolean waitForElementToDisappear(By by) {
        return (new WebDriverWait(getWebDriver(), DRIVER_WAIT_TIME)).until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    public WebDriver getBrowserByPageTitle(String pageTitle) {
        for (String windowHandle : webDriver.getWindowHandles()) {
            webDriver = webDriver.switchTo().window(windowHandle);
            if (pageTitle.equalsIgnoreCase(webDriver.getTitle())) {
                return webDriver;
            }
        }
        return null;
    }

    public static void minimizeToMobileBrowser() {
        webDriver.manage().window().maximize();
        webDriver.manage().window().setSize(new Dimension(300, 1200));
        webDriver.manage().window().setPosition(new Point(0, -50));
        sleep(3000);
        scrollToBottom();
        scrollToTop();

    }

    public static void navigateToPreviousPageUsingBrowserBackButton() {
        webDriver.navigate().back();
    }

    public static void navigateToURL(String url) {
        //webDriver.get(url);
        getWebDriver().navigate().to(url);
        getWebDriver().manage().timeouts().implicitlyWait(35, TimeUnit.SECONDS);
        //getWebDriver().switchTo().defaultContent();
        //getWebDriver().manage().deleteAllCookies();
        //getWebDriver().manage().window().maximize();
    }

    public static void getToURL(String url) {
        //webDriver.get(url);
        getWebDriver().get(url);
        getWebDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        getWebDriver().switchTo().defaultContent();
        getWebDriver().manage().deleteAllCookies();
        //getWebDriver().manage().window().maximize();
    }

    public static void navigateToNextPageUsingBrowserForwardButton() {
        webDriver.navigate().forward();
    }

    public static void clickWithinElementWithXYCoordinates(WebElement webElement, int x, int y) {
        Actions builder = new Actions(webDriver);
        builder.moveToElement(webElement, x, y);
        builder.click();
        builder.perform();
    }

    public String getElementByTagNameWithJSExecutor(String tagName) {
        return ((JavascriptExecutor) webDriver).executeScript("return window.getComputedStyle(document.getElementsByTagName('" + tagName + "')").toString();
    }

    public String getElementByQueryJSExecutor(String cssSelector) {
        return ((JavascriptExecutor) webDriver).executeScript("return window.getComputedStyle(document.querySelector('" + cssSelector + "')").toString();
    }

    /**
     * Gets the key from online/messages.properties for CSR
     *
     * @param key
     **/
    public String getOnlineProp(String key) {


        if ((key == null) || key.isEmpty()) {
            return "";
        } else {
            return onlineProps.getString(key);
        }
    }


    /**
     * Gets the key from Config.properties related to chosen profile
     *
     * @param key
     **/

    public String getProp(String key) {
        if ((key == null) || key.isEmpty()) {
            return "";
        } else {
            return props.getProperty(key);

        }
    }

/*    public void clickWithinElementWithXYCoordinates(WebElement webElement, int x, int y) {
        Actions builder = new Actions(webDriver);
        builder.moveToElement(webElement, x, y);
        builder.click();
        builder.perform();
    }*/

    public static Boolean scrollAndClick(WebElement webElement) {

        // if (webElement != null) {
        //Point hoverItem = webElement.getLocation();
        // clickWithinElementWithXYCoordinates(webElement, hoverItem.getX(), hoverItem.getY());
        webElement.click();
        return true;
       /* }else{
            return false;
        } */

    }

    public static void scrollToElement(WebElement webElement) {
        // Point hoverItem = webElement.getLocation();
        // clickWithinElementWithXYCoordinates(webElement, hoverItem.getX(), hoverItem.getY());
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        sleep(2000);
    }

    public static void scrollToBottom() {
        ((JavascriptExecutor) getWebDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public static void scrollToCentre() {
        ((JavascriptExecutor) getWebDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight/2)");
    }

    public static void scrollStepDown() {
        ((JavascriptExecutor) getWebDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight/5)");
    }

    public static void scrollSteps(int st) {
        int value;
        ((JavascriptExecutor) getWebDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight/30)");
    }

    public static void scrollToTop() {
        Actions actions = new Actions(webDriver);
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).perform();
        actions.sendKeys(Keys.HOME).perform();
            actions.sendKeys(Keys.PAGE_UP).perform();
    }

    public static void pageDown(By by) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webDriver.findElement(by)).sendKeys(Keys.PAGE_DOWN).perform();
    }

    public static void pageUp(By by) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webDriver.findElement(by)).sendKeys(Keys.PAGE_UP).perform();
    }

    public void scrollSlowMotion() {
        for (int second = 0; ; second++) {
            if (second >= 10) { //You can alter the value '20'
                break;
            }
            ((JavascriptExecutor) getWebDriver()).executeScript("window.scrollBy(0,200)", ""); //y value '800' can be altered
            sleep(1000);
        }
        scrollToTop();
    }

    public void scrollElementInCenter(WebElement webElement) {
        String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";
        ((JavascriptExecutor) getWebDriver()).executeScript(scrollElementIntoMiddle, webElement);

    }

    public void scrollBy(int offset) {
        ((JavascriptExecutor) getWebDriver()).executeScript("window.scrollBy(0, " + String.valueOf(offset) + ")", "");
    }

    public static Boolean scrollAndClick(By by, String name) throws Exception {
        if (verifyElementPresence(by)) {
            // Point hoverItem = webDriver.findElement(by).getLocation();
            // scrollToElement(webDriver.findElement(by));
            // clickWithinElementWithXYCoordinates(webDriver.findElement(by), hoverItem.getX(), hoverItem.getY());
            timeUnitWait(2);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(by));
                webDriver.findElement(by).click();
                Double loadTime = (Double) ((JavascriptExecutor) webDriver).executeScript(
                        "return (window.performance.timing.loadEventEnd - window.performance.timing.navigationStart) / 1000");
                ReportHelper.Log(name + " : Measured time taken : " + loadTime + " seconds", "INFO");
            } catch (Exception e) {
                timeUnitWait(5);
                wait.until(ExpectedConditions.elementToBeClickable(by));
                webDriver.findElement(by).click();
                Double loadTime = (Double) ((JavascriptExecutor) webDriver).executeScript(
                        "return (window.performance.timing.loadEventEnd - window.performance.timing.navigationStart) / 1000");
                ReportHelper.Log("Measured time taken : " + loadTime + " seconds", "INFO");
            }
            //waitForExpectedElement(by).click();
            return true;

        } else
            return false;
    }


    public static void webClick(By by) throws Exception {
        //webDriver.findElement(by).submit();
        Actions builder = new Actions(webDriver);
        builder.moveToElement(webDriver.findElement(by)).click(webDriver.findElement(by));
        builder.perform();
        //webDriver.findElement(by).click();
    }

    public static Boolean scrollAndDoubleClick(By by) throws Exception {
        if (verifyElementPresence(by)) {
            Actions action = new Actions(webDriver);
            WebElement element = webDriver.findElement(by);
            action.moveToElement(element).doubleClick().perform();
            return true;
        } else
            return false;

    }

    public static Boolean clickElement(By by) throws Exception {
        sleep(2000);
        if (verifyElementPresence(by)) {
            getWebDriver().findElement(by).click();
            return true;
        } else
            return false;
    }

    public static void closeTempJSPopup() {
        navigateToPreviousPageUsingBrowserBackButton();
        sleep(3000);
        navigateToNextPageUsingBrowserForwardButton();
        sleep(3000);
        //scrollToCentre();
    }

    public static void iosAcceptAlert() {
        /*navigateToPreviousPageUsingBrowserBackButton();
        sleep(3000);
        navigateToNextPageUsingBrowserForwardButton();
        sleep(3000);*/

        webDriver.switchTo().alert().accept();

    }

    public static void closeRemoteLocationPopups() {
        try {
            log("selected restaurants page");
            String currentContext = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(currentContext);
            ((AppiumDriver<WebElement>) getWebDriver()).context("NATIVE_APP");
            log("Inside native app context");
            sleep(5000);
            ((AndroidDriver) getWebDriver()).findElement(By.id("android:id/button1")).click();
            ;
            sleep(3000);
            List elementList = getWebDriver().findElements(MobileBy.xpath("//*[@class='android.widget.Button'][2]"));
            if (!elementList.isEmpty()) {
                if (getWebDriver().findElement(MobileBy.xpath("//*[@class='android.widget.Button'][2]")).isDisplayed()) {
                    getWebDriver().findElement(MobileBy.xpath("//*[@class='android.widget.Button'][2]")).click();
                }
            }
            ((AppiumDriver<WebElement>) getWebDriver()).context(currentContext);
            log(currentContext);
        } catch (Exception ne) {
            log("Native app popups does not appears");
            log(ne.getMessage());
        }
    }

    public static void closeAndroidLocationPopups() {
        try {
            log("selected restaurants page");
            String currentContext = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(currentContext);
            ((AppiumDriver<WebElement>) getWebDriver()).context("NATIVE_APP");
            log("Inside native app context");
            sleep(5000);
            ((AndroidDriver) getWebDriver()).findElement(By.id("android:id/button1")).click();
            ;
            sleep(3000);
            List elementList = getWebDriver().findElements(MobileBy.xpath("//*[@class='android.widget.Button'][2]"));
            if (!elementList.isEmpty()) {
                if (getWebDriver().findElement(MobileBy.xpath("//*[@class='android.widget.Button'][2]")).isDisplayed()) {
                    getWebDriver().findElement(MobileBy.xpath("//*[@class='android.widget.Button'][2]")).click();
                }
            }
            ((AppiumDriver<WebElement>) getWebDriver()).context(currentContext);
            log(currentContext);
        } catch (Exception ne) {
            log("Native app popups does not appears");
            log(ne.getMessage());
        }
    }

    public static void closeMobilePopups() {
        try {
            List elementList = getWebDriver().findElements(MobileBy.xpath("//*[@class='android.widget.Button'][2]"));
            if (!elementList.isEmpty()) {
                if (getWebDriver().findElement(MobileBy.xpath("//*[@class='android.widget.Button'][2]")).isDisplayed()) {
                    getWebDriver().findElement(MobileBy.xpath("//*[@class='android.widget.Button'][2]")).click();
                    sleep(1500);
                    log("popup clicked");
                    ((AndroidDriver) getWebDriver()).pressKeyCode(AndroidKeyCode.HOME);
                    sleep(1500);
                    ((AndroidDriver) getWebDriver()).pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
                    sleep(1500);
                    ((AndroidDriver) getWebDriver()).pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
                    sleep(1500);
                }
            }
        } catch (Exception ne) {
            log("Native Element does not appears");
            log(ne.getMessage());

        }
    }

    public static Boolean clickMobileElement(By by) {
        try {
            //clickMobileByIndex(MobileBy.xpath("//*[@class='android.webkit.WebView']"),0);
            sleep(1500);
            getWebDriver().findElement(by).click();
            sleep(2000);
            return true;
        } catch (Exception ne) {
            log("Native Mobile Element does not appear");
            log(ne.getMessage());
            return false;
        }
    }

    public static void clickMobileByIndex(By by, Integer index) {

        //clickMobileByIndex(MobileBy.xpath("//*[@class='android.webkit.WebView']"),0);
        List<WebElement> linkList = webDriver.findElements(by);
        //now traverse over the list and check
        log("" + linkList.size());
        log("" + linkList.get(index).getText());
        linkList.get(index).click();

        //log(linkList.get(i).getText());

        //log(linkList.get(i).getAttribute("alt"));
        // JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        //executor.executeScript("arguments[0].click();", linkList.get(index));

    }

    public static void cleanNativeAppDialogue() {

        ((AndroidDriver) getWebDriver()).pressKeyCode(AndroidKeyCode.HOME);
        sleep(1500);
        ((AndroidDriver) getWebDriver()).pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
        sleep(1500);
        ((AndroidDriver) getWebDriver()).pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
        sleep(2000);
    }

   /* public static Boolean tapMobileElement(By by) {
        try {
            sleep(1500);
            Dimension size = ((AndroidDriver) getWebDriver()).manage().window().getSize();
            TouchAction touchAction = new TouchAction((AndroidDriver) getWebDriver());
            touchAction.tap(((AndroidDriver) getWebDriver()).findElement(by)).perform();
            return true;
        } catch (Exception ne) {
            log("Native Mobile Element does not appear");
            log(ne.getMessage());
            return false;
        }
    }

    public static Boolean longPressMobileElement(By by) {
        try {
            sleep(1500);
            TouchAction touchAction = new TouchAction((AndroidDriver) getWebDriver());
            touchAction.longPress(((AndroidDriver) getWebDriver()).findElement(by)).perform().release();
            return true;
        } catch (Exception ne) {
            log("Native Mobile Element does not appear");
            log(ne.getMessage());
            return false;
        }
    }

    public static Boolean pressMobileElement(By by) {
        try {
            sleep(1500);
            TouchAction touchAction = new TouchAction((AndroidDriver) getWebDriver());
            touchAction.press(((AndroidDriver) getWebDriver()).findElement(by)).perform().release();
            return true;
        } catch (Exception ne) {
            log("Native Mobile Element does not appear");
            log(ne.getMessage());
            return false;
        }
    }*/

    public static Boolean setMobileValue(By by, String name) throws Exception {

        getWebDriver().findElement(by).click();
        ((AndroidDriver) getWebDriver()).getKeyboard().sendKeys(name);
        sleep(2000);
        ((AndroidDriver) getWebDriver()).hideKeyboard();
        return true;
    }

    public static Boolean setAndSelectMobileValue(By by, String name) throws Exception {

        getWebDriver().findElement(by).click();
        ((AndroidDriver) getWebDriver()).getKeyboard().sendKeys(name);
        sleep(2000);
        getWebDriver().findElement(MobileBy.xpath("//android.view.View[contains(@text,'" + name + "')]")).click();
        sleep(2000);
        ((AndroidDriver) getWebDriver()).hideKeyboard();
        return true;
    }

    public static void scrollToMobiletext(String text) throws Exception {

        String currentContext = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
        log(currentContext);
        if (!currentContext.matches("NATIVE_APP")) {
            ((AppiumDriver<WebElement>) getWebDriver()).context("NATIVE_APP");
            log("Inside native app context");
        }
        String uiSelector = "new UiSelector().textMatches(\"" + text
                + "\")";
        String command = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView("
                + uiSelector + ");";
        ((AndroidDriver) getWebDriver()).findElementByAndroidUIAutomator(command);

        if (!currentContext.matches("NATIVE_APP")) {
            ((AppiumDriver<WebElement>) getWebDriver()).context(currentContext);
            log(currentContext);
        }
    }

    public static void scrollDownRemoteMobile() throws Exception {
        ExecuteMethod method = new RemoteExecuteMethod(((AppiumDriver<WebElement>) webDriver));
        RemoteTouchScreen screen = new RemoteTouchScreen(method);
        screen.down(10, 25);
    }

    /*public static void scrollDownMobile() throws Exception {

        String currentContext = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
        log(currentContext);
        if (!currentContext.matches("NATIVE_APP")) {
            ((AppiumDriver<WebElement>) getWebDriver()).context("NATIVE_APP");
            log("Inside native app context");
        }

        Dimension size = ((AndroidDriver) getWebDriver()).manage().window().getSize();
        int width = (int)(size.width/2);
        int sp = (int)(size.getHeight()* 0.95);
        int ep = (int)(size.getHeight()* 0.05);
        new TouchAction((AndroidDriver) getWebDriver()).tap(width,size.getHeight()/2);
        new TouchAction((AndroidDriver) getWebDriver()).press(width,sp).waitAction(Duration.ofMillis(2000)).moveTo(width,ep).release().perform();

        if (!currentContext.matches("NATIVE_APP")) {
            ((AppiumDriver<WebElement>) getWebDriver()).context(currentContext);
            log(currentContext);
        }

    }

    public static void scrollCentreMobile() throws Exception {

        String currentContext = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
        log(currentContext);
        if (!currentContext.matches("NATIVE_APP")) {
            ((AppiumDriver<WebElement>) getWebDriver()).context("NATIVE_APP");
            log("Inside native app context");
            String cc = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(cc);
        }

        Dimension size = ((AndroidDriver) getWebDriver()).manage().window().getSize();
        int width = (int)(size.width/2);
        int sp = (int)(size.getHeight()* 0.9);
        int ep = (int)(size.getHeight()* 0.2);
        new TouchAction((AndroidDriver) getWebDriver()).tap(width,size.getHeight()/2);
        new TouchAction((AndroidDriver) getWebDriver()).press(width,sp).waitAction(Duration.ofMillis(2000)).moveTo(width,ep).release().perform();

        if (!currentContext.matches("NATIVE_APP")) {
            String cc = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(cc);
            ((AppiumDriver<WebElement>) getWebDriver()).context(currentContext);
            log(currentContext);
            cc = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(cc);
        }
    }

    public static void scrollStepDownMobile() throws Exception {

        String currentContext = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
        log(currentContext);
        if (!currentContext.matches("NATIVE_APP")) {
            ((AppiumDriver<WebElement>) getWebDriver()).context("NATIVE_APP");
            log("Inside native app context");
            String cc = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(cc);
        }

        Dimension size = ((AndroidDriver) getWebDriver()).manage().window().getSize();
        ((AndroidDriver) getWebDriver()).hideKeyboard();
        int width = (int)(size.width/2);
        int sp = (int)(size.getHeight()* 0.9);
        int ep = (int)(size.getHeight()* 0.6);
        new TouchAction((AndroidDriver) getWebDriver()).tap(width,size.getHeight()/2);
        new TouchAction((AndroidDriver) getWebDriver()).press(width,sp).waitAction(Duration.ofMillis(2000)).moveTo(width,ep).release().perform();

        if (!currentContext.matches("NATIVE_APP")) {
            String cc = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(cc);
            ((AppiumDriver<WebElement>) getWebDriver()).context(currentContext);
            log(currentContext);
            cc = ((AppiumDriver<WebElement>) getWebDriver()).getContext();
            log(cc);
        }
    }*/

    // ----------------------------------------------------------------------

    public static void switchToWebview() {

        sleep(2000);
        getWebDriver().findElement(MobileBy.xpath("//*[@class='android.webkit.WebView']")).click();
        sleep(5000);

        Set<String> contextNames = ((AndroidDriver) getWebDriver()).getContextHandles();
        System.out.println(contextNames.size());
        for (String contextName : contextNames) {
            System.out.println(contextNames);
            log(contextName);
            if (contextName.contains("WEBVIEW")) {
                log(contextName);
                ((AndroidDriver) getWebDriver()).context(contextName);
            }
        }
    }

    public static void switchFrame(By by) {
        try {
            //webDriver.switchTo().defaultContent();
            webDriver.switchTo().defaultContent();
            webDriver.switchTo().parentFrame();
            WebElement ifm = webDriver.findElement(by);
            webDriver.switchTo().frame(ifm);
            ReportHelper.Log("Sucessfully switched to new frame : ", "INFO");
        } catch (Exception e) {
            WebElement ifm = webDriver.findElement(by);
            webDriver.switchTo().frame(ifm);
        }
    }


    public static void switchFrameByPos(Integer num) {
        try {
            webDriver.switchTo().defaultContent();
            webDriver.switchTo().parentFrame();
            webDriver.switchTo().frame(num);
            ReportHelper.Log("Sucessfully switched to new frame : ", "INFO");
        } catch (Exception e) {
            webDriver.switchTo().frame(num);
        }
    }

    public static void iOSSwitchFrame(By by) {
        //getWebDriver().switchTo().defaultContent();
        //getWebDriver().switchTo().frame(webDriver.findElement(by));

        try {
            WebElement ifm = webDriver.findElement(by);
            String ifmAttribute = ifm.getAttribute("src");
            System.out.println("source is : " + ifmAttribute);
            //System.out.println("URL of the frame is:- " + ifm.getAttribute("src"));
            getWebDriver().navigate().to(ifmAttribute);
            ReportHelper.Log("URL of the frame is:- " + ifm.getAttribute("src"), "INFO");

        } catch (Exception e) {

            e.printStackTrace();


        }

    }

    public static void switchToNewTab() {
        ArrayList<String> tabs = new ArrayList<String>(webDriver.getWindowHandles());
        log(tabs.size() + "");
        if (tabs.size() > 1) {
            webDriver.switchTo().window(tabs.get(1));
        }
    }

    public static void switchToNewWindow() {

        String parentWindow = webDriver.getWindowHandle();
        Set<String> handles = webDriver.getWindowHandles();
        for (String windowHandle : handles) {
            if (!windowHandle.equals(parentWindow)) {
                webDriver.switchTo().window(windowHandle);
                // driver.switchTo().window(parentWindow); //cntrl to parent window
            }
        }
    }

    public static void switchToAWindow(String windowHandle) {
        webDriver.switchTo().window(windowHandle);
    }

    public static void openNewBrowser(String url) {

        String parentWindow = webDriver.getWindowHandle();
        ((JavascriptExecutor) webDriver).executeScript("window.open(arguments[0])", url);
        switchToNewTab();
        webDriver.manage().window().maximize();
        Set<String> handles = webDriver.getWindowHandles();
       /* for(String windowHandle  : handles)
        {
            if(!windowHandle.equals(parentWindow))
            {
                webDriver.switchTo().window(windowHandle);
                webDriver.manage().window().maximize();
                // driver.switchTo().window(parentWindow); //cntrl to parent window
            }
        } */
    }

    public static void maximizeBrowser() {

        webDriver.manage().window().maximize();
    }

    public static String storeWindowHandle() {
        return webDriver.getWindowHandle();
    }

    public static void switchToMainTab() {

        ArrayList<String> tabs = new ArrayList<String>(webDriver.getWindowHandles());
        log(tabs.size() + "");
        webDriver.switchTo().window(tabs.get(0));
    }

    public static void closeNewTab() {
        sleep(3000);
        ((JavascriptExecutor) webDriver).executeScript("window.close()");
    }

    public static Boolean selectByVisibleText(By by, String text) throws Exception {

        //if (webDriver.findElements(by).size() != 0) {
        if (verifyElementPresence(by)) {

            // Point hoverItem = webDriver.findElement(by).getLocation();
            //scrollToElement(webDriver.findElement(by));
            // clickWithinElementWithXYCoordinates(webDriver.findElement(by), hoverItem.getX(), hoverItem.getY());
            //  timeUnitWait(1);
            Select select = new Select(webDriver.findElement(by));
            select.selectByVisibleText(text);

            return true;
        } else
            return false;
    }

    public static void validateDropDownValue(By by, String text) {

        Select select = new Select(webDriver.findElement(by));
        List<WebElement> allOptions = select.getOptions();
        for (int i = 0; i < allOptions.size(); i++) {
            if (allOptions.get(i).equals("text")) {
                ReportHelper.Log("Desired option is present in the dropdown", "INFO");

            } else {
                ReportHelper.Log("Desired option is not present in the dropdown", "INFO");
                break;
            }
        }
    }


    public static Boolean webSelectByVisibleText(By by, String text) throws Exception {

        Select select = new Select(webDriver.findElement(by));
        select.selectByVisibleText(text);
        return true;
    }

    public static Boolean selectByValue(By by, String text) throws Exception {

        if (verifyElementPresence(by)) {
            //scrollToElement(webDriver.findElement(by));
            Select select = new Select(webDriver.findElement(by));
            select.selectByValue(text);
            return true;
        } else
            return false;
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log("Following execption occurs " + e.getMessage());
        }
    }

    public static void log(String someString) {

        System.out.println(someString);
    }

    public static void clickLinkbyURL(String url) {

        // driver.findElement(By.linkText("Restaurants")).click();
        List<WebElement> linkList = webDriver.findElements(By.tagName("a"));
        //now traverse over the list and check
        for (int i = 0; i < linkList.size(); i++) {
            if (linkList.get(i).getAttribute("href").matches(url)) {
                log(linkList.get(i).getText());
                log(linkList.get(i).getAttribute("href"));
                JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                executor.executeScript("arguments[0].click();", linkList.get(i));
                // linkList.get(i).click();
                break;
            }
        }
    }

    public static void enterJText(By by, String text) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].setAttribute('text', '" + text + "')", webDriver.findElement(by));
    }

    public static Boolean enterJNumber(By by, Integer val) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].setAttribute('value', " + val + ")", webDriver.findElement(by));
        return true;
    }

    public static void webEnter(By by, String text) {
        webDriver.findElement(by).clear();
        webDriver.findElement(by).sendKeys(text);
    }

    public static Boolean selectSinglePageDate(String bdate) throws Exception {

        //By bdate_field = By.xpath("//input[@id='bookingdate-singlepg_tablebookin']");
        By bdate_field = By.xpath("//input[@name='bookingdate']");
        By cancel = By.xpath("(//i[@class='fa fa-times'])[1]");
        By next = By.xpath("//a[@title='Next']");
        log(bdate);
        int mon = DateUtil.MonthsFromToday(bdate);
        int day1 = DateUtil.DayOfDate(bdate);
        int days = DateUtil.DaysBeforeToday(bdate);

        log(Integer.toString(mon));
        log(Integer.toString(day1));

        scrollAndClick(bdate_field, "CalenderField");
        sleep(3000);

        for (int i = 0; i < mon; i++) {

            sleep(1000);

            webDriver.findElement(next).click();
            //getWebDriver().findElement(calenderNext).click();
            log(Integer.toString(i + 1));
        }

        sleep(2000);

        if (days > 0) {
            try {
                ReportHelper.Log("Booking is not possible for the past date", "INFO");
                return Boolean.FALSE;
            } catch (Exception exp) {
                log("Booking is not possible for the chosen date");
                return Boolean.FALSE;
            }
        } else {
            try {
                WebElement dayField = webDriver.findElement(By.xpath("//a[@class = 'ui-state-default' and contains(.,'" + Integer.toString(day1) + "')]"));
                if (dayField.isEnabled()) {
                    sleep(2000);
                    dayField.click();
                    webDriver.findElement(By.xpath("//span[@class='main-btn confirm-btn accept-date']")).click();
                    return Boolean.TRUE;
                } else {
                    scrollToBottom();
                    pageDown(By.xpath("//a[contains(.,'30')]"));
                    if (dayField.isEnabled()) {
                        sleep(2000);
                        dayField.click();
                        return Boolean.TRUE;
                    } else {
                        log("Booking is not possible for the given date");
                        return Boolean.FALSE;
                    }
                }
            } catch (Exception exp) {
                log("Booking is not possible for the chosen date");
                return false;
            }
        }
    }

    public static Boolean selectDate(By locator, String bdate) throws Exception {

        log(bdate);
        int mon = DateUtil.MonthsFromToday(bdate);
        int day1 = DateUtil.DayOfDate(bdate);
        int days = DateUtil.DaysBeforeToday(bdate);

        log(Integer.toString(mon));
        log(Integer.toString(day1));

        WebDriverWait waitForDatePicker = new WebDriverWait(getWebDriver(), 5);
        waitForDatePicker.until(ExpectedConditions.elementToBeClickable(locator));
        //WebElement calNextMonth = driver.findElement(By.xpath("//a[contains(@class,'ui-datepicker-next ui-corner-all')]"));

        for (int i = 0; i < mon; i++) {

            sleep(1000);
            waitForDatePicker.until(ExpectedConditions.elementToBeClickable(locator));
            webDriver.findElement(locator).click();
            //getWebDriver().findElement(calenderNext).click();
            log(Integer.toString(i + 1));
        }

        sleep(2000);

        if (days > 0) {
            try {
                scrollToCentre();
                WebElement dayField = getWebDriver().findElement(By.xpath("//span[@class='ui-state-default' and contains(.,'" + Integer.toString(day1) + "')]"));
                WebElement parentDayField = getWebDriver().findElement(By.xpath("//span[@class='ui-state-default' and contains(.,'" + Integer.toString(day1) + "')]//parent::td"));
                log("Is day field enabled and selectable ? : " + dayField.isSelected());
                log("Is day field enabled and selectable ? : " + dayField.getAttribute("class"));
                log("Booking is not possible for the given past date");
                ReportHelper.Log("Calender Date field is of class: " + parentDayField.getAttribute("class"), "INFO");
                ReportHelper.Log("Booking is not possible for the past date", "INFO");
                return Boolean.FALSE;
            } catch (Exception exp) {
                log("Booking is not possible for the chosen date");
                return Boolean.FALSE;
            }
        } else {
            try {
                WebElement dayField = getWebDriver().findElement(By.linkText(Integer.toString(day1)));
                scrollToCentre();
                scrollToElement(dayField);
                if (dayField.isEnabled()) {
                    dayField.click();
                    return Boolean.TRUE;
                } else {
                    scrollToBottom();
                    if (dayField.isEnabled()) {
                        dayField.click();
                        return Boolean.TRUE;
                    } else {
                        log("Booking is not possible for the given date");
                        return Boolean.FALSE;
                    }
                }
            } catch (Exception exp) {
                log("Booking is not possible for the chosen date");
                return false;
            }
        }
    }

    /*public static void selectMobileDate(String bdate) throws Exception {

        log(bdate);
        int mon = DateUtil.MonthsFromToday(bdate);
        int day1 = DateUtil.DayOfDate(bdate);

        log(Integer.toString(mon));
        log(Integer.toString(day1));

        for (int i = 0; i < mon; i++) {
            sleep(1000);
            //getWebDriver().findElement(calenderNext).click();
           getWebDriver().findElement(MobileBy.xpath("//android.view.View[@bounds='[936,579][984,645]']")).click();
            log(Integer.toString(i + 1));
        }

        sleep(2000);

        try {

           // clickMobileByIndex(MobileBy.xpath("//android.view.View[@text='"+Integer.toString(day1)+"']"),0);

            scrollCentreMobile();
            WebElement dayField = getWebDriver().findElement(MobileBy.xpath("//android.view.View[@text='"+Integer.toString(day1)+"']"));
            dayField.click();

        } catch (NoSuchElementException exp) {
            log("Booking is not possible for the chosen date");
            System.exit(1);
        }
    }*/

    public void gotoSubPage(String name) {

        sleep(3000);
        String url = getCurrentUrl();
        if (url.endsWith("#")) {
            url = url.substring(0, url.length() - 1);
        }
        url = url + name;
        log(url);
        clickLinkbyURL(url);
        sleep(3000);
        if (checkCurrentUrlContains(name)) {
            getScreenShot(name);
            ReportHelper.Log("User has navigated to the " + name + " page", "PASS");
            ReportHelper.Log("URL : " + getCurrentUrl(), "INFO");
            ReportHelper.Log("Page Title : " + getCurrentPageTitle(), "INFO");
        } else {
            ReportHelper.Log("User could not navigate to the " + name + " page", "FAIL");
            getScreenShot("Error during " + name + " page Navigation");
            System.exit(1);
        }

    }

    public void validateAnImage(String text, String name) {

        // String text = "Book a table at Harvester";
        webDriver.findElement(By.xpath("//img[@alt='" + text + "']")).click();

        sleep(3000);
        if (checkCurrentUrlContains(name)) {

            getScreenShot(name);
            ReportHelper.Log("User has navigated to the " + name + " page", "PASS");
            ReportHelper.Log("URL : " + getCurrentUrl(), "INFO");
            ReportHelper.Log("Page Title : " + getCurrentPageTitle(), "INFO");
        } else {
            ReportHelper.Log("User could not navigate to the " + name + " page", "FAIL");
            getScreenShot("Error during " + name + " page Navigation");
            System.exit(1);
        }
    }

    public static boolean compareAnImage(String image) {

        String path = "C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\snapshot\\" + image + ".png";

        Ocular.config()
                .resultPath(Paths.get("C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\snapshot\\results"))
                .snapshotPath(Paths.get("C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\snapshot"))
                .globalSimilarity(95)
                .saveSnapshot(true);
        OcularResult result = Ocular.snapshot().from(Paths.get(path))
                .sample().using(webDriver)
                .compare();
        return result.isEqualsImages();

    }

    public static boolean compareAnElementImage(By by, String image) {

        String path = "C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\snapshot\\" + image + ".png";

        Ocular.config()
                .resultPath(Paths.get("C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\snapshot\\results"))
                .snapshotPath(Paths.get("C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\snapshot"))
                .globalSimilarity(80)
                .saveSnapshot(true);
        OcularResult result = Ocular.snapshot().from(Paths.get(path))
                .sample().using(webDriver)
                .element(webDriver.findElement(by))
                .compare();
        return result.isEqualsImages();

    }


    public static Boolean setValue(By by, String name) throws Exception {

        if (verifyElementPresence(by)) {
            if (webDriver.findElement(by) != null) {
                // scrollToElement(webDriver.findElement(by));
                // setoFocusonElement(by);
                try {
                    timeUnitWait(2);
                    wait.until(ExpectedConditions.elementToBeClickable(by));
                    webDriver.findElement(by).click();
                    webDriver.findElement(by).sendKeys(name);
                    webDriver.findElement(by).sendKeys(Keys.TAB);
                } catch (Exception ex) {
                    timeUnitWait(5);
                    wait.until(ExpectedConditions.elementToBeClickable(by));
                    webDriver.findElement(by).click();
                    webDriver.findElement(by).sendKeys(name);
                    webDriver.findElement(by).sendKeys(Keys.TAB);
                }
            }
            return true;
        } else
            return false;
    }

    public static Boolean keyPress(String name) throws Exception {

        switch (name) {
            case "F5":
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_F5);
                robot.keyRelease(KeyEvent.VK_F5);
                timeUnitWait(3);
                webDriver.findElement(By.xpath("/html/body")).sendKeys(Keys.F12);
                break;
        }
        return true;
    }

    public static Boolean keyValue(By by, String name) throws Exception {

        if (verifyElementPresence(by)) {
            if (webDriver.findElement(by) != null) {
                scrollToElement(webDriver.findElement(by));
                // setoFocusonElement(by);
                try {
                    timeUnitWait(2);
                    wait.until(ExpectedConditions.elementToBeClickable(by));
                    webDriver.findElement(by).sendKeys(name);
                } catch (Exception ex) {
                    timeUnitWait(5);
                    wait.until(ExpectedConditions.elementToBeClickable(by));
                    webDriver.findElement(by).sendKeys(name);
                }
            }
            return true;
        } else
            return false;
    }

    public static Boolean clearValue(By by) throws Exception {

        if (verifyElementPresence(by)) {
            if (webDriver.findElement(by) != null) {
                // scrollToElement(webDriver.findElement(by));
                // setoFocusonElement(by);
                wait.until(ExpectedConditions.elementToBeClickable(by));
                webDriver.findElement(by).click();
                webDriver.findElement(by).clear();
                webDriver.findElement(by).sendKeys(Keys.TAB);
            }

            return true;
        } else
            return false;
    }

    public static Boolean setCheckboxValue(By by, String state) throws Exception {
        // Email OptIn
        if (verifyElementPresence(by)) {
            WebElement cb = getWebDriver().findElement(by);
            switch (state) {
                case "ON":
                    if (!(cb.isSelected())) {
                        sleep(2000);
                        jClick(cb);
                    }
                    break;

                case "OFF":
                    if (cb.isSelected()) {
                        sleep(2000);
                        jClick(cb);
                    }
                    break;
            }
            return true;
        } else
            return false;
    }

    public static Boolean SetAndSelectValue(By by, String name) throws Exception {

        if (verifyElementPresence(by)) {
            if (webDriver.findElement(by) != null) {
                // setoFocusonElement(by);
                try {
                    webDriver.findElement(by).sendKeys(name);
                    jClick(By.xpath("//strong[@class='tt-highlight']"));
                    jClick(By.xpath("//div[@class='tt-suggestion tt-selectable']"));
                    sleep(3000);
                } catch (NoSuchElementException Ex) {
                    ReportHelper.Log("Suggest elements does not appear", "INFO");
                }
            }
            return true;
        } else
            return false;
    }

    public static Boolean SelectSuggestValue(By by, String name) throws Exception {

        if (verifyElementPresence(by)) {
            if (webDriver.findElement(by) != null) {
                jClick(by);
                webDriver.findElement(by).sendKeys(name);
                //jClick(by);
                //jClick(By.xpath("//strong[@class='tt-highlight']"));
                jClick(by);
                jClick(By.xpath("//div[@class='tt-suggestion tt-selectable']"));
                sleep(2000);
            }
            return true;
        } else
            return false;
    }

    public void validateInputFields(By by, String name) {

        if (isElementPresent(by)) {
            if (webDriver.findElement(by).isEnabled()) {
                ReportHelper.Log(name + " Field is Enabled ", "PASS");
                getScreenShot(name);
                setoFocusonElement(by);
                webDriver.findElement(by).sendKeys(Keys.TAB);
                WebElement we = webDriver.switchTo().activeElement();
                ReportHelper.Log("Next control is a " + we.getTagName() + " with label " + we.getText(), "INFO");
            } else {
                ReportHelper.Log(name + " Field is disabled ", "PASS");
                getScreenShot("Disabled" + name);
            }
        } else {
            ReportHelper.Log(name + " Field is missing", "FAIL");
            getScreenShot("Rendering error");
        }

    }

    public static Boolean validateElementText(By by, String expected, String name) {

        sleep(3000);
        if (webDriver.findElement(by).getText().trim().contains(expected)) {
            ReportHelper.Log(name + ": Actual and expected values matches", "PASS");
            return Boolean.TRUE;
        } else {
            ReportHelper.Log(name + ": Mismatch between actual and expected values", "FAIL");
            ReportHelper.Log(name + ": Expected value : " + expected, "INFO");
            ReportHelper.Log(name + ": Actual value : " + webDriver.findElement(by).getText().trim(), "INFO");
            return Boolean.FALSE;
        }
    }

    public static Boolean validateElementText(WebElement we, String expected, String name) {

        sleep(3000);
        log(we.toString());
        webDriver.switchTo().defaultContent();
        log(we.getText().trim());
        if (we.getText().trim().matches(expected)) {
            ReportHelper.Log(name + ": Actual and expected values matches", "PASS");
            return Boolean.TRUE;
        } else {
            ReportHelper.Log(name + ": Mismatch between actual and expected values", "FAIL");
            ReportHelper.Log(name + ": Expected value : " + expected, "INFO");
            ReportHelper.Log(name + ": Actual value : " + we.getText().trim(), "INFO");
            return Boolean.FALSE;
        }
    }

    public static Boolean validateElementPresence(By by, String name) throws Exception {

        try {
            timeUnitWait(8);
            webDriver.switchTo().defaultContent();
            if (webDriver.findElements(by).size() != 0) {
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                if (webDriver.findElement(by) != null) {
                    ReportHelper.Log(name + ": Element exists and displayed", "PASS");
                    return Boolean.TRUE;
                } else {
                    ReportHelper.Log(name + ": Element is not displayed", "INFO");
                    return Boolean.FALSE;
                }
            } else {
                ReportHelper.Log(name + ": Element does not exist", "INFO");
                return Boolean.FALSE;
            }
        } catch (Exception ex) {
            timeUnitWait(5);
            if (webDriver.findElement(by) != null) {
                ReportHelper.Log(name + ": Element exists and displayed", "PASS");
                return Boolean.TRUE;
            } else {
                ReportHelper.Log(name + ": Element is not displayed", "FAIL");
                return Boolean.FALSE;
            }
        }

    }

    public static Boolean validateRendering(By by, String name) throws Exception {

        try {
            timeUnitWait(1);
            webDriver.switchTo().defaultContent();
            if (webDriver.findElements(by).size() != 0) {
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                if (webDriver.findElement(by) != null) {
                    ReportHelper.Log(name + ": Element exists and rendered", "PASS");
                    return Boolean.TRUE;
                } else {
                    ReportHelper.Log(name + ": Element is not rendered", "FAIL");
                    return Boolean.FALSE;
                }
            } else {
                ReportHelper.Log(name + ": Element does not exist", "FAIL");
                return Boolean.FALSE;
            }
        } catch (Exception ex) {
            timeUnitWait(2);
            if (webDriver.findElement(by) != null) {
                ReportHelper.Log(name + ": Element exists and displayed", "PASS");
                return Boolean.TRUE;
            } else {
                ReportHelper.Log(name + ": Element is not displayed", "FAIL");
                return Boolean.FALSE;
            }
        }

    }

    public static boolean verifyElementPresence(By by) throws Exception {
        try {
            if (webDriver.findElements(by).size() != 0) {
                if (webDriver.findElement(by).isDisplayed())
                    return true;
                else {
                    return false;
                }
            } else
                return false;
        } catch (NoSuchElementException e) {
            ReportHelper.Log(": Element is not displayed", "INFO");
            return false;
        }
    }

    public static String storeText(By by, String expected, String name) throws Exception {

        sleep(3000);
        if (verifyElementPresence(by)) {
            return webDriver.findElement(by).getText().trim();
        } else
            ReportHelper.Log(": Element is not displayed", "INFO");
        return "NotShown";
    }


    public static void validateElementTextContains(By by, String expected, String name) {

        sleep(3000);
        if (webDriver.findElement(by).getText().trim().contains(expected)) {
            ReportHelper.Log(name + ": Actual and expected values matches", "PASS");
        } else {
            ReportHelper.Log(name + ": Mismatch between actual and expected values", "FAIL");
            ReportHelper.Log(name + ": Expected value : " + expected, "INFO");
            ReportHelper.Log(name + ": Actual value : " + webDriver.findElement(by).getText().trim(), "INFO");
        }
    }

    public static void validateSelectOptionCount(By by, Integer expected, String name) {

        sleep(3000);
        Select listBox = new Select(webDriver.findElement(by));
        int size1 = listBox.getOptions().size();
        if (size1 == expected) {
            ReportHelper.Log(name + ": Actual and expected count matches", "PASS");
        } else {
            ReportHelper.Log(name + ": Mismatch between actual and expected count " + size1, "FAIL");
        }
    }

    public static Integer countElements(By by) {
        List<WebElement> linkList = webDriver.findElements(by);
        //now traverse over the list and check
        return linkList.size();
    }

    public static void validateElementCount(By by, Integer expected, String name) {

        List<WebElement> linkList = webDriver.findElements(by);
        int size1 = linkList.size();
        if (size1 == expected) {
            ReportHelper.Log(name + ": Actual and expected element count matches", "PASS");
        } else {
            ReportHelper.Log(name + ": Mismatch between actual : " + size1 + " and expected no.of elements : " + expected, "FAIL");
        }
    }

    public static void getScreenShot(String ssname) {

        String file = ReportHelper.SSPrefix + "\\" + ssname + ".png";
        //String file1 = "./" + ssname + ".png";

        try {
            File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(file));
            ReportHelper.Log("<a href=\"" + file + "\"><font color=\"FFFFFFh\"><u> View " + ssname + " Screen Shot  </u></font></a>", "INFO");

        } catch (Exception ioe) {
            ioe.getStackTrace();
        }
    }

    public static void clickArticlebyText(String text) throws Exception {

        List<WebElement> linkList = webDriver.findElements(By.tagName("article"));
        //now traverse over the list and check
        for (int i = 0; i < linkList.size(); i++) {

            if (linkList.get(i).getAttribute("data-menu-title").contains(text)) {
                //log(linkList.get(i).getAttribute("data-menu-title"));
                sleep(3000);
                JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                executor.executeScript("arguments[0].focus();", linkList.get(i));
                executor.executeScript("arguments[0].click();", linkList.get(i));

                linkList.get(i).click();

               /* Actions act = new Actions(webDriver);
                act.moveToElement(linkList.get(i)).click(linkList.get(i)).build().perform(); */
                sleep(3000);
                break;
            }
        }
    }

    public static void addItemToBasket(By by, String Item) throws Exception {

        clickArticlebyText(Item);
        sleep(5000);
        webDriver.findElement(by).click();
        sleep(3000);

    }

    public static void clickImagebyAltText(String text) {

        List<WebElement> linkList = webDriver.findElements(By.tagName("img"));
        //now traverse over the list and check
        for (int i = 0; i < linkList.size(); i++) {
            if (linkList.get(i).getAttribute("alt").matches(text)) {
                //log(linkList.get(i).getText());
                //log(linkList.get(i).getAttribute("alt"));
                JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                executor.executeScript("arguments[0].click();", linkList.get(i));
                // linkList.get(i).click();
                break;
            }
        }
    }

    public static void clickByKeyValue(By by, String key, String value) {

        List<WebElement> linkList = webDriver.findElements(by);
        //now traverse over the list and check
        for (int i = 0; i < linkList.size(); i++) {
            if (linkList.get(i).getAttribute(key).matches(value)) {
                //log(linkList.get(i).getText());
                //log(linkList.get(i).getAttribute("alt"));
                // scrollToElement(linkList.get(i));
                JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                executor.executeScript("arguments[0].scrollIntoView(true);", linkList.get(i));
                executor.executeScript("arguments[0].click();", linkList.get(i));
                // linkList.get(i).click();
                break;
            }
        }
    }

    public static void clickByIndex(By by, Integer index) {

        List<WebElement> linkList = webDriver.findElements(by);
        //now traverse over the list and check

        //log(linkList.get(i).getText());
        //log(linkList.get(i).getAttribute("alt"));
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].click();", linkList.get(index));
        sleep(3000);
        // linkList.get(index).click();

    }

    public static Boolean selectContextMenuItem(By by) throws Exception {
        if (verifyElementPresence(by)) {
            Actions action = new Actions(webDriver);
            WebElement element = webDriver.findElement(by);
            action.contextClick(element).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.RETURN).build().perform();
            return true;
        } else
            return false;

    }

    public static Boolean clickNext(By by) throws Exception {
        if (verifyElementPresence(by)) {
            Actions action = new Actions(webDriver);
            WebElement element = webDriver.findElement(by);
            action.contextClick(element).sendKeys(Keys.TAB).sendKeys(Keys.RETURN).build().perform();
            return true;
        } else
            return false;

    }

    public static WebElement getWebElement(String type, String value) {

        String[] values;
        String[] data;
        List<WebElement> linkList;
        switch (type) {
            case "xpathByIndex":
                values = value.split(";");
                linkList = webDriver.findElements(By.xpath(values[0]));
                return linkList.get(Integer.parseInt(values[1]));
            case "xpathByKeyValue":
                values = value.split(";");
                data = values[1].split("#");
                linkList = webDriver.findElements(By.xpath(values[0]));
                for (int i = 0; i < linkList.size(); i++) {
                    if (linkList.get(i).getAttribute(data[0]).matches(data[1])) {
                        return linkList.get(i);
                    }
                }
                return null;
            default:
                return null;
        }
    }

    public static String generateVoucherCode(String Campaign, String HashCode) {

        // First, retrieve the circuit ID for the first circuit of the 2017 season

        String code = given().header("X-EES-AUTH-HASH", HashCode).header("X-EES-AUTH-CLIENT-ID", "kzdsstd38fe4wnz1d5ay").
                contentType("application/json").
                body("{\"resourceType\":\"CAMPAIGN\",\"resourceId\":" + Campaign + "}").
                when().
                post("https://consumer.sandbox.uk.eagleeye.com/2.0/token/create").
                then().
                extract().
                path("token");
        System.out.println(code);
        return code;


    }

    public static void deleteBrowserCookies() throws Exception {

        //getWebDriver().manage().deleteAllCookies();
        /*webDriver.get("chrome://settings/clearBrowserData");
        timeUnitWait(5);
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        timeUnitWait(10);
        System.out.println("Cookies have been deleted");
        ReportHelper.Log("Cookies have been deleted ", "PASS");*/

        Set<Cookie> coo = webDriver.manage().getCookies();
        System.out.println("Number of Cookies" + coo.size());

        Iterator<Cookie> it = webDriver.manage().getCookies().iterator();

        while (it.hasNext()) {
            Cookie C = it.next();
            System.out.println(C.getName() + "\n" + C.getPath() + "\n" + C.getDomain() + "\n" + C.getValue() + "\n" + C.getExpiry());
        }
        coo.clear();
        System.out.println("Number of cookies after deletion" + coo.size());
        ReportHelper.Log("Cookies have been deleted ", "PASS");

    }


}
