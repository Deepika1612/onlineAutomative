package com.mab.test.hybrid;
/**
 * Created by Mitchells & Butlers plc.
 * Author: Jabez James
 * Date: 06/02/18
 * This library provides methods to configure and start webdrivers for standard browsers and devices
 * Design patterns : Singelton and Factory are used in designing the reusable method(openBrowser(String browser))
 */
import com.mab.test.framework.helpers.LoadProperties;
import com.mab.test.framework.helpers.utils.Wifi;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileBrowserType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
public class WebDriverGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(WebDriverGenerator.class);
    private static final Dimension BROWSER_WINDOW_SIZE = new Dimension(1280, 1024);
    private static AppiumDriver<WebElement> MOBILE_DRIVER = null;
    private static AndroidDriver ANDROID_DRIVER = null;

    //Deepika
    private static IOSDriver IOS_DRIVER = null;


    private static final Thread CLOSE_THREAD = new Thread(() -> MOBILE_DRIVER.quit());
    private static String BROWSER;
    private static String PLATFORM;
    private static String MOBILE;
    private static String DRIVER_PATH;
    private static String DRIVER_ROOT_DIR;
    private static String FILE_SEPARATOR;
    private static String SELENIUM_HOST;
    private static String SELENIUM_PORT;
    private static String SELENIUM_REMOTE_URL;
    private static WebDriver REAL_DRIVER = null;
    public static final String USERNAME = "jabez3";
    public static final String AUTOMATE_KEY = "yLcxiHxAMwmh3669TCkw";
    //public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
    public static final String URL = "http://192.168.198.166:4444/wd/hub";
    static {
        LoadProperties.loadRunConfigProps("/environment.properties");
        SELENIUM_HOST = System.getProperty("driverhost");
        SELENIUM_PORT = System.getProperty("driverport");
        FILE_SEPARATOR = System.getProperty("file.separator");
        PLATFORM = LoadProperties.getProps().getProperty("platform");
        BROWSER = LoadProperties.getProps().getProperty("browser");
        MOBILE = LoadProperties.getProps().getProperty("mobile");
        DRIVER_ROOT_DIR = LoadProperties.getProps().getProperty(
                "driver.root.dir");

        if (!DRIVER_ROOT_DIR.equals("DEFAULT_PATH")) {
            System.setProperty("webdriver.chrome.driver", getDriverPath());


        }
   }

    public static void openBrowser(String browser) throws Exception{
            if (REAL_DRIVER == null) {
                try {
                    if (browser.equalsIgnoreCase("AndroidChrome")) {
                        MOBILE_DRIVER = startAndroidChromeDriver();
                        REAL_DRIVER = (RemoteWebDriver) MOBILE_DRIVER;
                        REAL_DRIVER.manage().deleteAllCookies();
                        String parent_window = REAL_DRIVER.getWindowHandle();
                        REAL_DRIVER.switchTo().window(parent_window);
                    } else if (browser.equalsIgnoreCase("DesktopChrome")) {
                        REAL_DRIVER = (RemoteWebDriver) startChromeDriver();
                        REAL_DRIVER.manage().deleteAllCookies();
                        String parent_window = REAL_DRIVER.getWindowHandle();
                        REAL_DRIVER.switchTo().window(parent_window);
                        REAL_DRIVER.manage().window().maximize();
                    } else if (browser.equalsIgnoreCase("DesktopChromeWithCookies")) {
                        REAL_DRIVER = (RemoteWebDriver) startChromeDriver();
                        String parent_window = REAL_DRIVER.getWindowHandle();
                        REAL_DRIVER.switchTo().window(parent_window);
                        REAL_DRIVER.manage().window().maximize();
                    } else if (browser.equalsIgnoreCase("DesktopFirefox")) {
                        BROWSER = "firefox";
                        //System.setProperty("webdriver.gecko.driver", "C:\\Drivers\\geckodriver\\win64\\geckodriver.exe");
                        // System.setProperty("webdriver.gecko.driver", getDriverPath());
                        System.setProperty("webdriver.gecko.driver", "C:\\Drivers\\geckodriver\\win64\\geckodriver.exe");
                        REAL_DRIVER = (RemoteWebDriver) startFireFoxDriver();
                        REAL_DRIVER.manage().deleteAllCookies();
                        String parent_window = REAL_DRIVER.getWindowHandle();
                        REAL_DRIVER.switchTo().window(parent_window);
                        REAL_DRIVER.manage().window().maximize();
                    } else if (browser.equalsIgnoreCase("DesktopIE")) {
                        BROWSER = "iexplore";
                        System.setProperty("webdriver.ie.driver", getDriverPath());
                        REAL_DRIVER = (RemoteWebDriver) startIEDriver();
                        ;
                        REAL_DRIVER.manage().deleteAllCookies();
                        String parent_window = REAL_DRIVER.getWindowHandle();
                        REAL_DRIVER.switchTo().window(parent_window);
                        REAL_DRIVER.manage().window().maximize();
                    } else if (browser.equalsIgnoreCase("RemoteChrome")) {
                        REAL_DRIVER = (RemoteWebDriver) startRemoteChromeDriver();
                    } else if (browser.equalsIgnoreCase("RemoteFirefox")) {
                        System.setProperty("webdriver.gecko.driver", "C:\\Drivers\\geckodriver\\win64\\geckodriver.exe");
                        REAL_DRIVER = (RemoteWebDriver) startRemoteFireFoxDriver();
                    } else if (browser.equalsIgnoreCase("RemoteIE")) {
                        BROWSER = "iexplore";
                        // System.setProperty("webdriver.ie.driver", getDriverPath());
                        System.setProperty("webdriver.ie.driver", "C:\\AutomationFramework\\JavaSeleniumFramework\\tools\\iedriver\\win64\\IEDriverServer.exe");
                        REAL_DRIVER = (RemoteWebDriver) startRemoteIEDriver();
                    } else if (browser.equalsIgnoreCase("RemoteAndroid")) {
                        MOBILE_DRIVER = startRemoteAndroidDriver();
                        //MOBILE_DRIVER = startAndroidChromeDriver();
                        REAL_DRIVER = (RemoteWebDriver) MOBILE_DRIVER;
                        REAL_DRIVER.manage().deleteAllCookies();
                        String parent_window = REAL_DRIVER.getWindowHandle();
                        REAL_DRIVER.switchTo().window(parent_window);
                    } else if (browser.equalsIgnoreCase("RemoteIOS")) {
                        REAL_DRIVER = (RemoteWebDriver) startRemoteIOSDriver();

                    } else if (browser.equalsIgnoreCase("DesktopSafari")) {
                        REAL_DRIVER = (RemoteWebDriver) startSafariDriver();
                        REAL_DRIVER.manage().window().maximize();
                    }else if (browser.equalsIgnoreCase("MobileIosSafari")){
                        MOBILE_DRIVER = startMobileSafariBrowser();
                        REAL_DRIVER = (RemoteWebDriver) MOBILE_DRIVER;

                        //REAL_DRIVER.manage().deleteAllCookies();
                        //String parent_window = REAL_DRIVER.getWindowHandle();
                        //System.out.println( "window is" +parent_window);
                        //REAL_DRIVER.switchTo().window(parent_window);
                    }
                } catch (IllegalStateException e) {
                    LOG.error("FIX path for driver.root.dir in pom.xml " + DRIVER_ROOT_DIR
                            + " Browser parameter " + browser + " Platform parameter " + PLATFORM
                            + " type not supported");
                }

            }
    }


    public static void openMobileApp(String appPackage,String appActivity){

        MOBILE_DRIVER = startMobileAppdriver(appPackage,appActivity);
        REAL_DRIVER = (RemoteWebDriver) MOBILE_DRIVER;

    }

    private static AppiumDriver<WebElement> startAndroidChromeDriver() {
        try {
            URL url = new URL("http://127.0.0.1:4723/wd/hub");
            // Create object of  AndroidDriver class and pass the url and capability that we created
            MOBILE_DRIVER = new AndroidDriver<WebElement>(url, getChromeAndroidDesiredCapabilities());
            // AppiumDriver<WebElement>
        }catch(Exception ex)
        {
            System.out.println("MalformedURLException occurs");
        }
        return MOBILE_DRIVER;
    }

    private static AppiumDriver<WebElement> startMobileSafariBrowser (){
        try {
            URL url = new URL("http://127.0.0.1:4723/wd/hub");
            // Create object of IOSDriver class and pass the url and capability that we created
            MOBILE_DRIVER = new IOSDriver<WebElement>(url, getMobileIosSafariDesiredCapabilities());
            // AppiumDriver<WebElement>
            //MOBILE_DRIVER.get("http://www.google.com");
            //System.out.println("context is" +MOBILE_DRIVER.getContext());

        }catch(Exception ex)
        {
            System.out.println("MalformedURLException occurs");
        }
       return MOBILE_DRIVER;

    }

    private static AppiumDriver<WebElement> startMobileAppdriver(String appPackage,String appActivity) {
        try {
            URL url = new URL("http://127.0.0.1:4723/wd/hub");
            // Create object of  AndroidDriver class and pass the url and capability that we created
            ANDROID_DRIVER = new AndroidDriver(url, getMobileAppDesiredCapabilities(appPackage,appActivity));
            // AppiumDriver<WebElement>
        }catch(Exception ex)
        {
            System.out.println("MalformedURLException occurs");
            System.out.println(ex.getMessage());
        }
        return ANDROID_DRIVER;
    }

    private static WebDriver startChromeDriver() {
        DesiredCapabilities capabilities = getChromeDesiredCapabilities();
//        capabilities.setCapability("chrome.switches", Collections.singletonList("--no-default-browser-check"));
//        capabilities.setCapability("chrome.switches", Collections.singletonList("--disable-logging"));
//        HashMap<String,String> chromePreferences = new HashMap<>();
//        chromePreferences.put("profile.password_maanger_enabled","false");
        if (SELENIUM_HOST == null)
            REAL_DRIVER = new ChromeDriver(
                    ChromeDriverService.createDefaultService(),capabilities);
        else {
            try {
                REAL_DRIVER = getRemoteWebDriver(capabilities);
            } catch (MalformedURLException e) {
                LOG.error(SELENIUM_REMOTE_URL + " Error " + e.getMessage());
            }
        }
        REAL_DRIVER.manage().window().setSize(BROWSER_WINDOW_SIZE);
        return REAL_DRIVER;
    }

    private static WebDriver startIEDriver() {
        DesiredCapabilities capabilities = getInternetExploreDesiredCapabilities();
        if (SELENIUM_HOST == null)
            REAL_DRIVER = new InternetExplorerDriver(capabilities);
        else {
            try {
                REAL_DRIVER = getRemoteWebDriver(capabilities);
            } catch (MalformedURLException e) {
                LOG.error(SELENIUM_REMOTE_URL + " Error " + e.getMessage());
            }
        }
        return REAL_DRIVER;
    }

    private static WebDriver startSafariDriver() {
        REAL_DRIVER = new SafariDriver();
        return REAL_DRIVER;
    }
    private static WebDriver startRemoteChromeDriver() throws Exception{
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-b2c-security");
        chromeOptions.addArguments("--test-type");

        //To Disable any browser notifications
        chromeOptions.addArguments("--disable-notifications");
        //To disable yellow strip info bar which prompts info messages
        chromeOptions.addArguments("disable-infobars");
        caps.setCapability("chrome.verbose", false);
        caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
         /* caps.setBrowserName("chrome");
        caps.setPlatform(Platform.WIN10);
        ChromeOptions options = new ChromeOptions();
        options.setBinary(new File(System.getProperty("user.dir")+"//tools//chromedriver//win32//chromedriver.exe"));
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        caps.setCapability("browser", "chrome");
        caps.setCapability("browser_version", "68.0");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        //caps.setCapability("resolution", "1024x768");
        caps.setCapability("browserstack.debug","true");
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.localIdentifier", "abcd123");
        */

        REAL_DRIVER = new RemoteWebDriver(new URL(URL), caps);
        REAL_DRIVER.manage().window().maximize();
        return REAL_DRIVER;

    }
    private static WebDriver startRemoteFireFoxDriver() throws Exception{

      /*   DesiredCapabilities caps = DesiredCapabilities.firefox();

      caps.setCapability("browser", "Firefox");
        caps.setCapability("browser_version", "62.0 beta");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("resolution", "1024x768");
        caps.setCapability("browserstack.debug","true");
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.localIdentifier", "abcd123");

        caps = getFireFoxDesiredCapabilities();   */

        DesiredCapabilities caps = getFireFoxDesiredCapabilities();
        FirefoxProfile testProfile = new FirefoxProfile();
        testProfile.setAcceptUntrustedCertificates(true);
        testProfile.setAssumeUntrustedCertificateIssuer(true);
        testProfile.setPreference("browser.cache.memory.enable", false);
        testProfile.setPreference("network.proxy.type", 0);
        caps.setCapability("acceptInsecureCerts",true);
        caps.setCapability(FirefoxDriver.PROFILE, testProfile);
        caps.setJavascriptEnabled(true);
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("network.proxy.type", 0);
       // driver = new RemoteWebDriver(options.toCapabilities());

        /*caps.setPlatform(Platform.ANY);
        caps.setBrowserName("firefox");
        caps.setCapability("acceptInsecureCerts",true);
        caps.setCapability(FirefoxDriver.PROFILE, testProfile);
        caps.setJavascriptEnabled(true); */

        //REAL_DRIVER = new RemoteWebDriver(new URL(URL), options.toCapabilities());
        REAL_DRIVER.manage().window().maximize();
        return REAL_DRIVER;

    }

    private static WebDriver startRemoteIEDriver() throws Exception{
       /*  DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
        caps.setCapability("browser", "IE");
        caps.setCapability("browser_version", "11.0");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("resolution", "1024x768");
        caps.setCapability("browserstack.debug","true");
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.localIdentifier", "abcd123");
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.DRIVER, Level.OFF);

        caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        caps.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);*/
        DesiredCapabilities caps = getInternetExploreDesiredCapabilities();

        InternetExplorerOptions options = new InternetExplorerOptions();

        caps.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "");
        REAL_DRIVER = new RemoteWebDriver(new URL(URL), caps);
        return REAL_DRIVER;

    }

    private static AppiumDriver<WebElement> startRemoteAndroidDriver() throws Exception{
        DesiredCapabilities caps = new DesiredCapabilities();
       /* caps.setCapability("browserstack.debug","true");
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.localIdentifier", "abcd123");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("device", "Samsung Galaxy S8");
        caps.setCapability("realMobile", "true");
        caps.setCapability("os_version", "7.0");

        caps.setCapability("browserName", "chrome");
        caps.setCapability("deviceName", "SamsungS9");
        caps.setCapability("platform", "Android");*/
        caps = getChromeAndroidDesiredCapabilities();

        DesiredCapabilities capabilities=DesiredCapabilities.android();
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
//        capabilities.setCapability(MobileCapabilityType.UDID, ID);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"Galaxy S8");

        capabilities.setCapability("disable-popup-blocking", true);
        capabilities.setCapability("autoAcceptAlerts", true);
        capabilities.setCapability("autoDismissAlerts", true);
        capabilities.setCapability("chromedriverExecutable", System.getProperty("user.dir")+"//tools//chromedriver//win32//chromedriver.exe");
        capabilities.setCapability(MobileCapabilityType.VERSION,"8.0.0");

       // REAL_DRIVER = new RemoteWebDriver(new URL(URL), capabilities);

        MOBILE_DRIVER = new AndroidDriver<WebElement>(new URL(URL), capabilities);

        return MOBILE_DRIVER ;
    }

    private static WebDriver startRemoteIOSDriver() throws Exception{
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserstack.debug","true");
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.localIdentifier", "abcd123");
        caps.setCapability("browserstack.console", "errors");
       /* caps.setCapability("browserName", "Safari");
        caps.setCapability("device", "iPhone 8");
        caps.setCapability("realMobile", "true"); */
        caps.setCapability("browserName", "iPhone");
        caps.setCapability("device", "iPhone X");
        caps.setCapability("realMobile", "true");
        caps.setCapability("os_version", "11.0");
        caps.setCapability("nativeWebTap", "true");
        REAL_DRIVER = new RemoteWebDriver(new URL(URL), caps);
        return REAL_DRIVER;

}
    private static WebDriver startFireFoxDriver() {
        DesiredCapabilities capabilities = getFireFoxDesiredCapabilities();
        FirefoxProfile testProfile = new FirefoxProfile();
        testProfile.setAcceptUntrustedCertificates(true);
        testProfile.setAssumeUntrustedCertificateIssuer(true);
        testProfile.setPreference("browser.cache.memory.enable", false);
        testProfile.setPreference("network.proxy.type", 0);
        if (SELENIUM_HOST == null) {
            capabilities.setCapability(FirefoxDriver.PROFILE, testProfile);
            FirefoxBinary ffBinary = new FirefoxBinary(new File("C:\\Program Files\\Mozilla Firefox\\firefox.exe"));
            //REAL_DRIVER = new FirefoxDriver(capabilities);
            REAL_DRIVER= new FirefoxDriver();
//            REAL_DRIVER = new FirefoxDriver(testProfile);
        } else {
            try {
                LOG.info("Running on Selenium Server " + SELENIUM_HOST + " PORT " + SELENIUM_PORT);
                capabilities.setPlatform(Platform.ANY);
                capabilities.setBrowserName("firefox");
                capabilities.setCapability("acceptInsecureCerts",true);
                capabilities.setCapability(FirefoxDriver.PROFILE, testProfile);
                capabilities.setJavascriptEnabled(true);
                REAL_DRIVER = getRemoteWebDriver(capabilities);
            } catch (MalformedURLException e) {
                LOG.error(SELENIUM_REMOTE_URL + " Error " + e.getMessage());
            }
        }
        return REAL_DRIVER;
    }


    private static DesiredCapabilities getChromeAndroidDesiredCapabilities() {

        String ID = LoadProperties.getProps().getProperty("udid");
        DesiredCapabilities capabilities=DesiredCapabilities.android();
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
        //capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "BROWSER");
        // capabilities.setBrowserName(MobileBrowserType.CHROMIUM);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");

//	capabilities.setCapability(MobileCapabilityType.UDID, "ce0617165176d4120d");
        capabilities.setCapability(MobileCapabilityType.UDID, ID);

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"Galaxy S8");
        //capabilities.setCapability(MobileCapabilityType.UDID, "CB512DQRVN");
        //capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"Xperia X");
        capabilities.setCapability("disable-popup-blocking", true);
        capabilities.setCapability("autoAcceptAlerts", true);
        capabilities.setCapability("autoDismissAlerts", true);
        capabilities.setCapability("chromedriverExecutable", System.getProperty("user.dir")+"//tools//chromedriver//win32//chromedriver.exe");
        //capabilities.setCapability("appPackage", "com.android.chrome");
        //capabilities.setCapability("app-activity", "org.chromium.chrome.browser.ChromeTabbedActivity");
        capabilities.setCapability(MobileCapabilityType.VERSION,"7.0");

        return capabilities;
    }

    private static DesiredCapabilities getMobileIosSafariDesiredCapabilities (){

        String iosPhoneUdid = LoadProperties.getProps().getProperty("udid");

        DesiredCapabilities cap =DesiredCapabilities.iphone();
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME,"XCUITest");
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION,"12.3.1");
        cap.setCapability(MobileCapabilityType.DEVICE_NAME,"iPhone X");
        cap.setCapability(MobileCapabilityType.UDID,iosPhoneUdid);
        cap.setCapability(MobileCapabilityType.BROWSER_NAME,"Safari");
        //cap.setCapability("webkitDebugProxyPort", "9221");
        cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT,"600");
        cap.setCapability(MobileCapabilityType.AUTO_WEBVIEW,"true");
        //cap.setCapability(MobileCapabilityType.CLEAR_SYSTEM_FILES,"true");
        cap.setCapability("startIDWP",true);
        cap.setCapability(IOSMobileCapabilityType.START_IWDP, true);

        return cap;

    }

    private static DesiredCapabilities getMobileAppDesiredCapabilities(String appPackage,String appActivity) {

        String ID = LoadProperties.getProps().getProperty("udid");
        DesiredCapabilities capabilities=DesiredCapabilities.android();

        capabilities.setCapability("BROWSER_NAME", "Android");
        capabilities.setCapability("VERSION", "8.0.0");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");

        //	capabilities.setCapability(MobileCapabilityType.UDID, "ce0617165176d4120d");
        capabilities.setCapability(MobileCapabilityType.UDID, ID);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"Galaxy S8");
        //capabilities.setCapability(MobileCapabilityType.UDID, "CB512DQRVN");
        //capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"Xperia X");
        //capabilities.setCapability("autoWebview", "true");
        capabilities.setCapability("locationServicesAuthorized", true);
        capabilities.setCapability("autoGrantPermissions", true);
        capabilities.setCapability("unicodeKeyboard", true);
        capabilities.setCapability("resetKeyboard", true);
        capabilities.setCapability("chromedriverExecutable", System.getProperty("user.dir")+"//tools//chromedriver//win32//chromedriver.exe");
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);
        capabilities.setCapability(MobileCapabilityType.VERSION,"7.0");
        System.out.println("Started App");
        return capabilities;
    }

   /* public static AppiumDriver<WebElement> getMobileWebDriver() {
        return MOBILE_DRIVER;
    } */

    private static DesiredCapabilities getChromeDesiredCapabilities() {
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.DRIVER, Level.OFF);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-b2c-security");
        chromeOptions.addArguments("--test-type");

        //To Disable any browser notifications
        chromeOptions.addArguments("--disable-notifications");
        //To disable yellow strip info bar which prompts info messages
        chromeOptions.addArguments("disable-infobars");
        capabilities.setCapability("chrome.verbose", false);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return capabilities;
    }

    private static DesiredCapabilities getFireFoxDesiredCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("marionette", false);
//        capabilities.setCapability("firefox_binary", "C://Program Files//Mozilla Firefox//firefox.exe");
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return capabilities;
    }

    private static DesiredCapabilities getInternetExploreDesiredCapabilities() {
        //System.setProperty("webdriver.ie.driver", getDriverPath());
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.DRIVER, Level.OFF);
        DesiredCapabilities capabilities = DesiredCapabilities
                .internetExplorer();
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
       // capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, "accept");

        capabilities.setVersion("11");
        return capabilities;
    }

    public static WebDriver getWebDriver() {

        // return MOBILE_DRIVER;
        return REAL_DRIVER;
    }

    private static RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) throws MalformedURLException {
        SELENIUM_REMOTE_URL = "http://" + SELENIUM_HOST + ":" + SELENIUM_PORT + "/wd/hub";
        LOG.info(SELENIUM_REMOTE_URL + " Checking Selenium Remote URL");
        return new RemoteWebDriver(new URL(SELENIUM_REMOTE_URL), (capabilities));
    }

    public static String getDriverPath() {
        if (BROWSER.equals("chrome") && PLATFORM.contains("win")) {
            DRIVER_PATH = DRIVER_ROOT_DIR + FILE_SEPARATOR + "chromedriver"
                    + FILE_SEPARATOR + PLATFORM + FILE_SEPARATOR
                    + "chromedriver.exe";
        } else if (BROWSER.equals("chrome") && PLATFORM.contains("linux")) {
            DRIVER_PATH = DRIVER_ROOT_DIR + FILE_SEPARATOR + "chromedriver"
                    + FILE_SEPARATOR + PLATFORM + FILE_SEPARATOR
                    + "chromedriver";
        } else if (BROWSER.equals("iexplore") && PLATFORM.contains("win")) {
            DRIVER_PATH = DRIVER_ROOT_DIR + FILE_SEPARATOR + "iedriver"
                    + FILE_SEPARATOR + PLATFORM + FILE_SEPARATOR
                    + "IEDriverServer.exe";
        } else if(BROWSER.equals("firefox")){
            DRIVER_PATH = DRIVER_ROOT_DIR + FILE_SEPARATOR + "geckodriver"
                    + FILE_SEPARATOR + PLATFORM + FILE_SEPARATOR
                    + "geckodriver.exe";
            System.out.println(DRIVER_PATH);
        } else
            LOG.warn("No web driver path is set.");
        return DRIVER_PATH;
    }


  //  @Override
    public void close() {
        if (Thread.currentThread() != CLOSE_THREAD) {
            throw new UnsupportedOperationException(
                    "You shouldn't close this WebDriver. It's shared and will close when the JVM exits.");
        }
        //super.close();
    }
    public static void closeBrowsers() {

        REAL_DRIVER.quit();
    }
}
