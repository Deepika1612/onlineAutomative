package com.mab.test.framework.helpers;
/**
 * Created by Mitchells & Butlers plc.
 * Author: Jabez James
 * Date: 02/02/18
 * Time: 12:54 PM
 * This class provides functions to create HTML execution log with extent report libraries.
 */
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.mab.test.framework.helpers.utils.AutoEmail;
import com.mab.test.framework.helpers.utils.RandomGenerator;
import com.mab.test.framework.helpers.utils.Wifi;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class ReportHelper {


	private static ExtentReports extent = null;
	private static ExtentTest logger = null;
	public static ExtentHtmlReporter htmlReporter = null;
	private static String Report = null;
	private static String workingDir = null;
	public static String SSPrefix = null;
	public static String Suffix = null;
	public static String rs = null;
	public static String name = null;
	public static String State = "Passed";
	
    public static void Initialize(String name) {
		//static {
		htmlReporter = null;
		//name = LoadProperties.getProps().getProperty("project.name");
    	Date date = new Date() ;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM_HH-mm-ss") ;
		workingDir = System.getProperty("user.dir");
		System.out.println("Current working directory : " + workingDir);
		//RandomGenerator RG = new RandomGenerator();
		rs = RandomGenerator.randomInteger(2);
		Suffix = dateFormat.format(date);
		//SSPrefix = workingDir+"\\src\\test\\resources\\testreports\\ExecutionLogs\\"+rs+"_"+name+"_"+dateFormat.format(date);
		 SSPrefix = workingDir+"/src/test/resources/testreports/ExecutionLogs/"+Suffix+"_"+rs;

		File directory = new File(SSPrefix);

		if (! directory.exists()) {
			directory.mkdir();
			System.out.println("Directory");
		}

    	Report = SSPrefix+"\\"+name+"_"+rs+".html" ;
		//+dateFormat.format(date)
    	htmlReporter = new ExtentHtmlReporter(Report);
		extent = new ExtentReports ();
		extent.attachReporter(htmlReporter);
		//name=name+rs;
		String title = LoadProperties.getProps().getProperty("program.name");
		htmlReporter.config().setDocumentTitle(title);
		htmlReporter.config().setReportName(name);
		htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setTheme(Theme.STANDARD);
		logger = extent.createTest(name);
    }

	private static ExtentTest getLogger()
	{
		return logger;
	}
    
    public static void Log(String name,String state)
    {
    	if (state == "PASS")
    	getLogger().log(Status.PASS, MarkupHelper.createLabel(name , ExtentColor.GREEN));
    	else if (state == "FAIL") {
			State = "Failed";
			getLogger().log(Status.FAIL, MarkupHelper.createLabel(name, ExtentColor.RED));
		}
    	else if (state == "INFO")
		getLogger().log(Status.INFO, MarkupHelper.createLabel(name , ExtentColor.CYAN));
		else if (state == "KEY")
			getLogger().log(Status.INFO, MarkupHelper.createLabel(name , ExtentColor.BLUE));
		else if (state == "WARN")
			getLogger().log(Status.WARNING, MarkupHelper.createLabel(name , ExtentColor.PINK));
    }
    
    public static void Finalize(String sname) throws Exception
    {
     	extent.flush();
     	String file1 = new String(Report);

     if (LoadProperties.getProps().getProperty("autoemail").equalsIgnoreCase("yes")){
     	// String link ="file://corp.retailhq.net/workspace/IT/Outlet%20Strategy%20Program%20152/E.%20Develop%20&%20Test/Testing/Automation/Reports/"+Suffix+"_"+rs+"/"+name+"_"+rs+".html" ;

		 String rpath = LoadProperties.getProps().getProperty("reportlink").toString();
		 System.out.println(rpath.toString());

		 String link =rpath.substring(1, rpath.length() - 1)+"/"+Suffix+"_"+rs+"/"+name+"_"+rs+".html" ;

		 System.out.println("link equals  ="+link);

		 //reportlink=file://corp.retailhq.net/workspace/IT/Outlet Strategy Program 152/E. Develop & Test/Testing/Automation/Reports
		//String rep = "file://192.168.198.110/c$/AutomationFramework/JavaSeleniumFramework/src/test/resources/testreports/ExecutionLogs/16-04_15-07-33_43/Harvester_43.html";
		 Wifi.switchToWifi("MBRSC01");

		 String source = SSPrefix;
		 File srcDir = new File(source);
		 String destination = "\\\\Corp.retailhq.net\\workspace\\IT\\Outlet Strategy Program 152\\E. Develop & Test\\Testing\\Automation\\Reports\\"+Suffix+"_"+rs;
		 File destDir = new File(destination);
		 try {
			 boolean created =  destDir.mkdir();
			 if(created)
				 System.out.println("Folder was created !");
			 else
				 System.out.println("Unable to create folder");
			 FileUtils.copyDirectory(srcDir, destDir);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 	Wifi.switchToWifi("MABFSGuest");
		 	System.out.println(link);
			AutoEmail.sendEmail(link,sname);
	}
     System.out.println("HTML Report is stored in the path: "+Report);
	 //Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", workingDir+"/src/test/resources/testreports/Exec.bat", file1});
	 Thread.sleep(5000);
    }

	public static void getScreenShot(String ssname) {

		String file = ReportHelper.SSPrefix + "\\" + ssname + ".png";

			try {
				File scrFile = ((TakesScreenshot) WebDriverHelper.getWebDriver()).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, new File(file));
				ReportHelper.Log("<a href=\"file:///" + file + "\"><font color=\"FFFFFFh\"><u> View " + ssname + " Screen Shot  </u></font></a>", "INFO");

			} catch (Exception ioe) {
				ioe.getStackTrace();
			}
	}

}
