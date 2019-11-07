package com.mab.test.hybrid;

import com.mab.test.framework.helpers.ReportHelper;
import com.mab.test.framework.helpers.utils.ExcelUtils;
import com.mab.test.framework.helpers.utils.RandomGenerator;
import io.appium.java_client.MobileBy;
import org.junit.Test;
import org.openqa.selenium.*;

import java.util.HashMap;

/**
 * Created by Mitchell's &amp; Butlers plc.
 * Author: Jabez James
 * Date: 05/06/18
 * This library provides Junit test method DesignDrivenExecution() that reads data from test design
 * spreadsheet and repeatedly triggers executeStep() method to execute respective actions.
 */
public class ScriptGenerator   {

    public static String baseurl ;
    public static ExcelUtils EU;
    public static String datacolumn ;
    public static String scenario;
    public static int data_col_num ;
    public static int step;
    public static Long time;
    public static Boolean lastRecord;
    public static Boolean failure;

    @Test
/**
 * Junit test method DesignDrivenExecution() that reads data from test design
 * spreadsheet and triggers executestep method in loop
*/
    public void DesignDrivenExecution() throws Exception {

        WebDriver driver;
        Boolean status = Boolean.TRUE;
        lastRecord = Boolean.FALSE;
        failure = Boolean.FALSE;

        HashMap<String, String> record = new HashMap<String, String>();
        //String scenario = "PCP_TableBooking";
        //String scenario = "Harvester_TakeAway";
        scenario = System.getProperty("scenario");
        System.out.println("Scenario is passed as : "+scenario);
        time = Long.valueOf(10000);
        // Creation of an object of class ExcelUtils
         EU = null;
         EU = new ExcelUtils("./src/test/resources/testdesign/"+scenario+".xlsx");
            /*if (scenario.contains("//")){
            scenario = scenario.replace("\\","_");
            System.out.println("Scenario is modified as : "+scenario);
            }*/
        for (int j = 1; j < EU.colCount()-3; j++) {
            datacolumn = "DATA_" + String.valueOf(j);
            data_col_num = j + 3;
            if (j == (EU.colCount()-4)) {
                lastRecord = Boolean.TRUE;
            }
            try {
            for (int i = 1; i < EU.rowCount(); i++) {
                //  Returns a row of record selected by its column name & its value ( key and value) combination.
                step = i;
                record = EU.readRow(i);
                status = executeStep(record);
                if (!status) {
                    status = Boolean.FALSE;
                    failure = Boolean.TRUE;
                    ReportHelper.Finalize(scenario);
                    if (lastRecord) {
                        if (failure) {
                            System.exit(1);
                        }
                    }
                    break;
                }
                   }
            } catch (Exception e) {
               ReportHelper.Log("Execution of the dataset : " + datacolumn + "interupted due to exception ", "FAIL");
                ReportHelper.Log(e.getMessage(), "FAIL");
                failure = Boolean.TRUE;
                ReportHelper.Finalize(scenario);
                if (lastRecord) {
                    if (failure) {
                        System.exit(1);
                    }
                }
                continue;
            }
            ReportHelper.Log("Execution completed for the dataset : " + datacolumn, "PASS");
           // if (status) {
                ReportHelper.Finalize(scenario);
            //}
            if (lastRecord) {
                if (failure) {
                    System.exit(1);
                }
            }
        }
    }
    /**
    * @param record Contains a row dataset as HashMap collection.
    * @return Status - Success or Failure
     */
    public static Boolean executeStep(HashMap<String, String> record)throws Exception{
            String name;
            String action;
            String data ;
            String appPackage,appActivity,locvalue;
            Boolean state = Boolean.TRUE;
            By locator;
            WebElement webelement = null;
            String Rgen = RandomGenerator.randomString(2);
            name = record.get("CONTROL");
            action = record.get("ACTION");
            data = record.get(datacolumn);
            if (data.contains("_Random_")){
                String[] items = data.split("_");
                    data = items[0]+Rgen+items[2]; }
            if (data.contains("_Random")){
            String[] items = data.split("_");
            data = items[0]+Rgen; }
            if (data.contains("Wait")){
               if (data.contains(" ")) {
                    String[] el = data.split(" ");
                    time = Long.valueOf(el[1])*1000;
                    ControlsLibrary.sleep(time);
                }else {
                    ControlsLibrary.sleep(time);
                }
                }
                if (data.contains("Skip")){
                    return state;
                }

            locvalue = record.get("LOCATOR_VALUE");
            locator = getLocator(record.get("LOCATOR_TYPE"),record.get("LOCATOR_VALUE"));
            if(locator==null)
            {
                webelement = ControlsLibrary.getWebElement(record.get("LOCATOR_TYPE"),record.get("LOCATOR_VALUE"));
            }
        try {
        switch (action) {
            case "StartBrowser":
                WebDriverGenerator.openBrowser(data);
                ControlsLibrary.InitializeLibrary();
                ReportHelper.Initialize(scenario);
                ReportHelper.Log("Execution started for the dataset : "+ datacolumn ,"PASS");
                state = Boolean.TRUE;
                break;
            case "StartApp":
            case "StartMobileApp":
                appPackage = record.get("LOCATOR_TYPE");
                appActivity = record.get("LOCATOR_VALUE");
                WebDriverGenerator.openMobileApp(appPackage,appActivity);
                ControlsLibrary.InitializeLibrary();
                ReportHelper.Initialize(scenario);
                ReportHelper.Log("Execution started for the dataset : "+ datacolumn ,"PASS");
                ReportHelper.Log(data + " : Mobile app has been started " ,"PASS");
                state = Boolean.TRUE;
                break;
            case "CloseBrowser":
                WebDriverGenerator.closeBrowsers();
                ReportHelper.Log("Application browser windows are closed : "+ data ,"PASS");
                state = Boolean.TRUE;
                break;
            case "MaximizeBrowser":
                ControlsLibrary.maximizeBrowser();
                state = Boolean.TRUE;
                break;
            case "CloseTab":
                ControlsLibrary.closeNewTab();
                ReportHelper.Log("Sucessfully closed the current tab","PASS");
                break;
            case "NavigateBack":
                ControlsLibrary.navigateToPreviousPageUsingBrowserBackButton();
                break;
            case "NavigateForward":
                ControlsLibrary.navigateToNextPageUsingBrowserForwardButton();
                break;
            case "MinimizeToMobile":
                ControlsLibrary.minimizeToMobileBrowser();
                state = Boolean.TRUE;
                break;
                //WebDriver driver = ControlsLibrary.getWebDriver();
            case "ScrollDown":
                ControlsLibrary.scrollToBottom();
                state = Boolean.TRUE;
                break;
            case "PageDown":
                ControlsLibrary.pageDown(locator);
                state = Boolean.TRUE;
                break;
            case "PageUp":
                ControlsLibrary.pageUp(locator);
                state = Boolean.TRUE;
                break;
            case "ScrollUp":
                ControlsLibrary.scrollToTop();
                ReportHelper.Log("Sucessfully scrolled to top of browser","PASS");
                state = Boolean.TRUE;
                break;
            case "ScrollStepDown":
                ControlsLibrary.scrollStepDown();
                state = Boolean.TRUE;
                break;
            case "ScrollSteps":
                ControlsLibrary.scrollSteps(Integer.valueOf(data));
                state = Boolean.TRUE;
                break;
            case "ScrollCenter":
            case "ScrollToCenter":
                ControlsLibrary.scrollToCentre();
                state = Boolean.TRUE;
                break;
            case "ScrollToElement":
                ControlsLibrary.scrollToElement(ControlsLibrary.webDriver.findElement(locator));
                state = Boolean.TRUE;
                break;
            case "NavigateToUrl":
                baseurl = data;
                ControlsLibrary.navigateToURL(data);
                ReportHelper.Log("Sucessfully navigated to the URL: "+data,"PASS");
                state = Boolean.TRUE;
                break;
            case "OpenNewBrowser":
                baseurl = data;
                ControlsLibrary.openNewBrowser(data);
                ReportHelper.Log("Sucessfully navigated to the URL within a new browser: "+data,"PASS");
                state = Boolean.TRUE;
                break;
            case "GetUrl":
                baseurl = data;
                ControlsLibrary.navigateToURL(data);
                ReportHelper.Log("Sucessfully navigated to the URL: "+data,"PASS");
                state = Boolean.TRUE;
                break;
            case "SwitchToNewTab":
            case "SwitchToNextTab":
                baseurl = data;
                ControlsLibrary.switchToNewTab();
                ReportHelper.Log("Sucessfully switched to the next tab","PASS");
                state = Boolean.TRUE;
                break;
            case "SwitchToWindow":
                ControlsLibrary.switchToAWindow(data);
                ReportHelper.Log("Sucessfully switched to the window","PASS");
                state = Boolean.TRUE;
                break;
            case "Click":
            case "ClickLink":
            case "ClickButton":
            case "ClickImage":

                if(locator==null)
                {
                    state = ControlsLibrary.scrollAndClick(webelement);
                }else {
                    state = ControlsLibrary.scrollAndClick(locator,name);
                }
                if (state) {
                    ReportHelper.Log("Identified and clicked the element: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " Element is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;
            case "ValidateURL":
                state = ControlsLibrary.checkCurrentUrlContains(data);
                if (state) {
                    ReportHelper.Log(name + ": Navigated to the correct URL" , "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + ": Navigated to the incorrect URL" , "FAIL");
                    state = Boolean.TRUE;
                }
                break;

            case "RetrieveURL":
                record.put(datacolumn,ControlsLibrary.retrieveCurrentUrl()) ;
                data = record.get(datacolumn);
                ControlsLibrary.getScreenShot(name);
                EU.UpdateCellValue(step, data_col_num,data);
                ReportHelper.Log( name +" : "+data,"KEY");
                state = Boolean.TRUE;
                break;

            case "ClickNext":
                state = ControlsLibrary.clickNext(locator);
                if (state) {
                    ReportHelper.Log("Identified and clicked the element: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " Element is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;
            case "ClickMobileElement":
                state = ControlsLibrary.clickMobileElement(locator);
                if (state) {
                    ReportHelper.Log("Identified and clicked the element: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " Element is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;
            case "Stop":
            case "StopExecution":
                state = Boolean.FALSE;
                ReportHelper.Log("Execution of the dataset has been stopped by design", "INFO");
                break;
            case "SwitchToWebview":
                ControlsLibrary.switchToWebview();
                break;
            case "DoubleClick":
                state = ControlsLibrary.scrollAndDoubleClick(locator);
                if (state) {
                    ReportHelper.Log("Identified and double clicked the element: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " : Element is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;

            case "SelectContextMenuItem":
                state = ControlsLibrary.selectContextMenuItem(locator);
                if (state) {
                    ReportHelper.Log("Identified and selected the context menu item: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " : Element is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;

            case "ClickImageText":
                ControlsLibrary.clickImagebyAltText(data);
                ReportHelper.Log("Identified and clicked the element: "+name,"PASS");
                state = Boolean.TRUE;
                break;
            case "ClickByIndex":
                ControlsLibrary.clickByIndex(locator,Integer.valueOf(data));
                ReportHelper.Log("Identified and clicked the element: "+name,"PASS");
                state = Boolean.TRUE;
                break;
            case "ClickByKeyValue":
                String[] vals = data.split("#");
                ControlsLibrary.clickByKeyValue(locator,vals[0],vals[1]);
                ReportHelper.Log("Identified and clicked the element: "+name,"PASS");
                state = Boolean.TRUE;
                break;

            case "JSClick":
            case "ExecuteClick":
                state = ControlsLibrary.jsClick(locator);
                if (state) {
                    ReportHelper.Log("Identified and clicked the element: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " : Element is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;
            case "WebClick":
            case "QuickClick":
                ControlsLibrary.webClick(locator);
                break;
            case "WebEnter":
            case "QuickEnter":
                ControlsLibrary.webEnter(locator,data);
                break;
            case "WebSelect":
            case "QuickSelect":
                ControlsLibrary.webSelectByVisibleText(locator,data);
                break;
            case "JSSetValue":
                ControlsLibrary.enterJText(locator,data);
                break;
            case "CookieClick":
                state = ControlsLibrary.clickElement(locator);
                if (state) {
                    ReportHelper.Log("Identified and clicked the cookie button: " + name, "PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " : Cookie button is not displayed ", "INFO");
                    if (data.matches("Mandatory"))
                        state = Boolean.FALSE;
                    else
                        state = Boolean.TRUE;
                }
                break;
            case "CloseAddPopup":
                ControlsLibrary.closeTempJSPopup();
                state = Boolean.TRUE;
                break;
            case "CloseLocationPopups":
                ControlsLibrary.closeAndroidLocationPopups();
                state = Boolean.TRUE;
                break;
            case "SetValue":
                if (data.matches("Null"))
                    data = "";
                state = ControlsLibrary.setValue(locator,data);
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                    state = Boolean.TRUE;
                break;
            case "KeyValue":
                if (data.matches("Null"))
                    data = "";
                state = ControlsLibrary.keyValue(locator,data);
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                state = Boolean.TRUE;
                break;

            case "KeyPress":
            case "KeyStroke":
                state = ControlsLibrary.keyPress(data);
                break;

            case "EnterNumber":
                state = ControlsLibrary.enterJNumber(locator,Integer.parseInt(data));
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                state = Boolean.TRUE;
                break;
            case "ClearValue":
            case "RemoveText":
                state = ControlsLibrary.clearValue(locator);
                if (state)
                    ReportHelper.Log(name +" : Cleared the existing value","PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                state = Boolean.TRUE;
                break;

            case "MobileSetValue":
                state = ControlsLibrary.setMobileValue(locator,data);
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;
            case "MobileSetAndSelectValue":
                state = ControlsLibrary.setAndSelectMobileValue(locator,data);
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;

            /*case "MobileTap":
                state = ControlsLibrary.tapMobileElement(locator);
                if (state)
                    ReportHelper.Log(name +" : Tap action executed sucessfully " ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;*/
            case "MobileCleanNative":
            case "MobileCleanDialogue":
                    ControlsLibrary.cleanNativeAppDialogue();
             break;

            /*case "MobileLongPress":
                state = ControlsLibrary.longPressMobileElement(locator);
                if (state)
                    ReportHelper.Log(name +" : Long press action executed sucessfully " ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;*/
            /*case "MobileElementPress":
                state = ControlsLibrary.pressMobileElement(locator);
                if (state)
                    ReportHelper.Log(name +" : Press action executed sucessfully on mobile " ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;*/

            case "MobileScrollToText":
                ControlsLibrary.scrollToMobiletext(data);
                break;
            case "MobileScrollDown":
                //ControlsLibrary.scrollDownMobile();
                ControlsLibrary.scrollDownRemoteMobile();
                break;
            /*case "MobileScrollStepDown":
                //ControlsLibrary.scrollDownMobile();
                ControlsLibrary. scrollStepDownMobile();
                break;*/
            /*case "MobileScrollCenter":
                ControlsLibrary.scrollCentreMobile();
                break;*/
            case "MobileClickByIndex":
                ControlsLibrary.clickMobileByIndex(locator,Integer.valueOf(data));
                ReportHelper.Log("Identified and clicked the element: "+name,"PASS");
                state = Boolean.TRUE;
                break;
            /*case "MobileSelectDate":
                ControlsLibrary.selectMobileDate(data);
                ReportHelper.Log("Calender is set to : "+data,"PASS");
                state = Boolean.TRUE;
                break;*/
            case "SetAndSelect":
            case "TypeAndSelect":
            case "EnterAndChoose":
                state = ControlsLibrary.SetAndSelectValue(locator,data);
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;

            case "SelectSuggest":
                state = ControlsLibrary.SelectSuggestValue(locator,data);
                if (state)
                    ReportHelper.Log(name +" : Entered the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Element is not displayed ","INFO");
                break;

            case "Select":
            case "SelectValue":
                //ControlsLibrary.scrollAndClick(locator);
                state = ControlsLibrary.selectByValue(locator,data);

                if (state)
                    ReportHelper.Log(name +" : Selected the value - "+data ,"PASS");
                else
                    ReportHelper.Log(name +" : Combobox element is not displayed ","INFO");
                    state = Boolean.TRUE;
                break;
            case "SelectText":
            case "SelectVisibleText":
                //ControlsLibrary.scrollAndClick(locator);
                state = ControlsLibrary.selectByVisibleText(locator,data);
                if (state)
                ReportHelper.Log(name +" : Selected the value - "+data ,"PASS");
                else
                ReportHelper.Log(name +" : Combobox element is not displayed ","INFO");
                state = Boolean.TRUE;
               break;
            case "ValidateDropDownValue":
                ControlsLibrary.validateDropDownValue(locator,data);
                state = Boolean.TRUE;
                break;
            case "SetCheckbox":
            case "TurnCheckbox":
                state = ControlsLibrary.setCheckboxValue(locator,data);
                if (state)
                    ReportHelper.Log("Checkbox "+ name +" is switched to : "+data,"PASS");
                else
                    ReportHelper.Log(name +" : Combobox element is not displayed ","INFO");
                    state = Boolean.TRUE;
                break;
            case "SwitchFrame":
                ControlsLibrary.switchFrame(locator);
                ReportHelper.Log("Sucessfully switched the focus to inner frame","PASS");
                state = Boolean.TRUE;
                break;
            case "SwitchFrameByPos":
                ControlsLibrary.switchFrameByPos(Integer.valueOf(data));
                ReportHelper.Log("Sucessfully switched the focus to inner frame","PASS");
                state = Boolean.TRUE;
                break;
            case "IOSSwitchFrame":
                ControlsLibrary.iOSSwitchFrame(locator);
                ReportHelper.Log("Sucessfully switched the focus to inner frame","PASS");
                state = Boolean.TRUE;
                break;

            case "SwitchTab":
                ControlsLibrary.switchToNewTab();;
                state = Boolean.TRUE;
                break;
            case "SwitchToNewWindow":
                ControlsLibrary.switchToNewWindow();
                ReportHelper.Log("Sucessfully switched to the new browser window","PASS");
                state = Boolean.TRUE;
                break;

            case "SwitchToMainTab":
                ControlsLibrary.switchToMainTab();
                ReportHelper.Log("Sucessfully switched to the main browser tab","PASS");
                state = Boolean.TRUE;
                break;
            case "ClickToSubpage":
            case "NavigateToSubpage":
                data = baseurl+data;
                ControlsLibrary.log(data);
                ControlsLibrary.sleep(2000);
                ControlsLibrary.clickLinkbyURL(data);
                state = Boolean.TRUE;
                break;
            case "ClickLinkURL":
            case "NavigateToLink":
                ControlsLibrary.log(data);
                ControlsLibrary.sleep(2000);
                ControlsLibrary.clickLinkbyURL(data);
                state = Boolean.TRUE;
                break;
            case "SelectDate":
                state = ControlsLibrary.selectDate(locator,data);
                if (state) {
                    ReportHelper.Log("Calender is set to : "+data,"PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + "Booking is not possible for the given date", "INFO");
                        state = Boolean.FALSE;
                  }
                break;
            case "SelectSinglePageDate":
                state = ControlsLibrary.selectSinglePageDate(data);
                if (state) {
                    ReportHelper.Log("Calender is set to : "+data,"PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + "Booking is not possible for the given date", "INFO");
                    state = Boolean.FALSE;
                }
                break;
            case "AddToBasket":
                ControlsLibrary.addItemToBasket(locator,data);
                ReportHelper.Log("Item : "+data+" is added to the basket","PASS");
                break;
            case "AssertText":
            case "ValidateText":
                ControlsLibrary.validateElementText(locator,data,name);
                ControlsLibrary.getScreenShot("Assert:"+name);
                state = Boolean.TRUE;
                break;
            case "ValidateElementCount":
                ControlsLibrary.validateElementCount(locator,Integer.valueOf(data),name);
                break;
            case "CompareImage":
            case "ValidatePageImage":
                state = ControlsLibrary.compareAnImage(data);
                if (state) {
                    ReportHelper.Log(name + " : Image is identical with the baseline","PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " : Image is not identical with the baseline", "WARN");
                }
                state = Boolean.TRUE;
                break;
            case "CompareElementImage":
            case "ValidateElementImage":
                state = ControlsLibrary.compareAnElementImage(locator,data);
                if (state) {
                    ReportHelper.Log(name + " : Image is identical with the baseline","PASS");
                    state = Boolean.TRUE;
                }
                else {
                    ReportHelper.Log(name + " : Image is not identical with the baseline", "WARN");
                }
                state = Boolean.TRUE;
                break;

            case "MandateText":
                if(locator==null)
                {
                    state = ControlsLibrary.validateElementText(webelement,data,name);
                }else {
                    state = ControlsLibrary.validateElementText(locator,data,name);
                }

                ControlsLibrary.getScreenShot("Assert:" + name);
                if (state)
                    state = Boolean.TRUE;
                else
                    state = Boolean.FALSE;
               break;
            case "AssertTextContains":
            case "ValidateSubstring":
                ControlsLibrary.validateElementTextContains(locator,data,name);
                ControlsLibrary.getScreenShot("Assert:"+name);
                state = Boolean.TRUE;
                break;

            case "CheckEnabled":
                state = ControlsLibrary.isEnabled(locator);
                //ReportHelper.Log(name+": Validation has returned "+ state.toString(), "Info");
                ControlsLibrary.getScreenShot(name);
                break;
            case "CheckDisabled":
                state = ControlsLibrary.isDisabled(locator);
                //ReportHelper.Log(name+": Validation has returned "+ state.toString(), "Info");
                ControlsLibrary.getScreenShot(name);
                break;

            case "CheckNotExists":
                if(locator==null) {
                    if(webelement!=null)
                        state = Boolean.TRUE;
                    else
                        state = Boolean.FALSE;
                }
                else {
                    state = ControlsLibrary.validateElementPresence(locator, name);
                    //ReportHelper.Log(name+": Validation has returned "+ state.toString(), "Info");
                }
                ControlsLibrary.getScreenShot(name);

                if (!state) {
                        ReportHelper.Log(name+": Expected element does not exist as expected", "PASS");
                        state = Boolean.TRUE;
                    }
                    else{
                        ReportHelper.Log(name+": Unexpected element exists", "FAIL");
                        state = Boolean.FALSE;
                    }
          break;

            case "CheckExists":
            case "VerifyElementPresent":
            case "CheckRendering":
                if(locator==null) {
                    if(webelement!=null)
                        state = Boolean.TRUE;
                    else
                        state = Boolean.FALSE;
                    }
                else {
                    if (action.matches("CheckRendering"))
                        state = ControlsLibrary.validateRendering(locator, name);
                    else
                        state = ControlsLibrary.validateElementPresence(locator, name);
                    //ReportHelper.Log(name+": Validation has returned "+ state.toString(), "Info");
                }
                ControlsLibrary.getScreenShot(name);

                if (!state) {
                    if (data.matches("Mandatory")){
                         ReportHelper.Log(name+": Failed as mandatory element is missing", "FAIL");
                        state = Boolean.FALSE;
                        }
                    else{
                        ReportHelper.Log(name+": Expected element does not exist", "Info");
                        state = Boolean.TRUE;
                         }
                    }
                else if (state) {
                    if (data.matches("Failure")){
                        state = Boolean.FALSE;
                        ReportHelper.Log(name+": Failed as error message or error condition occurs", "FAIL");
                        }
                    else{
                        state = Boolean.TRUE;
                        ReportHelper.Log(name+": Sucessful as expected element is rendered", "PASS");
                        }
                }
                break;
            case "StoreText":
            case "RetrieveContent":
                record.put(datacolumn,ControlsLibrary.storeText(locator,data,name)) ;
                data = record.get(datacolumn);
                ControlsLibrary.getScreenShot(name);
                EU.UpdateCellValue(step, data_col_num,data);
                ReportHelper.Log( name +" : "+data,"KEY");
                state = Boolean.TRUE;
                break;
            case "GenerateVoucherCode":
                record.put(datacolumn,ControlsLibrary.generateVoucherCode("601851","d81646838b575ab17040a304d4216ebb")) ;
                data = record.get(datacolumn);
                EU.UpdateCellValue(step, data_col_num,data);
                ReportHelper.Log( name +" : "+data,"KEY");
                state = Boolean.TRUE;
                break;

            case "GenerateFreeMainVoucherCode":
                record.put(datacolumn,ControlsLibrary.generateVoucherCode("601853","65b861b62c62b1b24464525356c9b574")) ;
                data = record.get(datacolumn);
                EU.UpdateCellValue(step, data_col_num,data);
                ReportHelper.Log( name +" : "+data,"KEY");
                state = Boolean.TRUE;
                break;

            case "StoreWindowHandle":
                record.put(datacolumn,ControlsLibrary.storeWindowHandle()) ;
                data = record.get(datacolumn);
                ControlsLibrary.getScreenShot(name);
                EU.UpdateCellValue(step, data_col_num,data);
                ReportHelper.Log( name +" : "+data,"KEY");
                state = Boolean.TRUE;
                break;

            case "Wait":
                time = Long.valueOf(data)*1000;
                ControlsLibrary.sleep(time);
                ReportHelper.Log("Browser wait time set to "+data+" seconds","PASS");
                break;

            case "ScreenShot":
                ControlsLibrary.getScreenShot("Screenshot:"+name);
                ControlsLibrary.getScreenShot(name);
                break;
            case "DeleteCookies":
                ControlsLibrary.deleteBrowserCookies();
                break;

            case "IosAlertAccept" :
                ControlsLibrary.iosAcceptAlert();
                break;
        }
        } catch (Exception e) {
            ReportHelper.Log("Execution of the dataset : " + datacolumn + ": interupted due to an exception in the step : " + name, "FAIL");
            ControlsLibrary.getScreenShot("Error");
            ReportHelper.Log(e.getMessage(), "FAIL");
            failure = Boolean.TRUE;
            state = Boolean.FALSE;
            ReportHelper.Finalize(scenario);
            if (lastRecord) {
                if (failure) {
                    System.exit(1);
                }
            }

        }
        return state;
    }

    /**
     * @param type Selenium locator type.
     * @param value - locator value as string
     * @return locator - Selenium By object
     */
    public static By getLocator(String type , String value){

        switch (type)
        {
            case "xpath":
                return By.xpath(value);
            case "id":
                return By.id(value);
            case "name":
                return By.name(value);
            case "cssSelector":
                return By.cssSelector(value);
            case "linkText":
                return By.linkText(value);
            case "partialLinkText":
                return By.partialLinkText(value);
            case "tagName":
                return By.tagName(value);
            case "className":
                return By.className(value);
            case "mobileXpath":
                return MobileBy.xpath(value);
            case "mobileId":
                return MobileBy.id(value);
            case "mobileClass":
                return MobileBy.className(value);
            default:
                return null;
        }

    }



}
