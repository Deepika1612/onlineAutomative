package com.mab.test.framework.helpers.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;


public class DateUtil {

	public static int MonthsFromToday(String bdate) throws Exception {
		
	    Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String today = format.format(date);
		DateTime startDate = new DateTime(format.parse(today));
		DateTime endDate = new DateTime(format.parse(bdate));
		
		int month1 = startDate.getMonthOfYear();
		int month2 = endDate.getMonthOfYear();
		
		return (month2-month1);
		
	}

	public static int DaysBeforeToday(String bdate) throws Exception {

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String today = format.format(date);
		DateTime startDate = new DateTime(format.parse(today));
		DateTime endDate = new DateTime(format.parse(bdate));

		int month1 = startDate.getMonthOfYear();
		int month2 = endDate.getMonthOfYear();
		if (month2==month1) {
			int day1 = startDate.getDayOfMonth();
			int day2 = endDate.getDayOfMonth();

			if (day1 > day2)
				return (day1 - day2);
			else
				return 0;
		}
		else
				return 0;
	}
	
	public static int DayOfDate(String bdate) throws Exception {

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		DateTime endDate = new DateTime(format.parse(bdate));
		return endDate.getDayOfMonth();

	}

	public static String CurrentYear() throws Exception {

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		return format.format(date);

	}

	public static String CurrentDay() throws Exception {

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd");
		String today = format.format(date);
		return format.format(date) ;
	}

	public static String CurrentMonth() throws Exception {

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("MMMM");
		String today = format.format(date);
		return today.toUpperCase();

	}

		

}
