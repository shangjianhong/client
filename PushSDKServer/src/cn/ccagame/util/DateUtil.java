package cn.ccagame.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DateUtil
{
    private static Log log = LogFactory.getLog(DateUtil.class);
    private static String timePattern = "HH:mm";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    public static final Date convertStringToDate(String aMask, String strDate)
    {
	if (null == strDate)
	{
	    return null;
	}

	Date date = null;
	SimpleDateFormat df = new SimpleDateFormat(aMask);
	try
	{
	    date = df.parse(strDate);
	}
	catch (ParseException pe)
	{
	    log.error("ParseException: " + pe);
	}

	return (date);
    }

    /**
     * This method returns the current date time in the format: yyyy-MM-dd HH:MM
     * 
     * @param theTime
     *            the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime)
    {
	return getDateTime(timePattern, theTime);
    }


    /**
     * This method generates a string representation of a date's date/time in the format you specify on input
     * 
     * @param aMask
     *            the date pattern the string is in
     * @param aDate
     *            a date object
     * @return a formatted string representation of the date
     * @see java.text.SimpleDateFormat
     */
    public static final String getDateTime(String aMask, Date aDate)
    {
	SimpleDateFormat df = null;
	String returnValue = "";

	if (aDate != null)
	{
	    df = new SimpleDateFormat(aMask);
	    returnValue = df.format(aDate);
	}

	return (returnValue);
    }



    /**
     * ?��?�????ateFormat?��?�?????�?��
     * 
     * @param aDate
     *            A date to convert
     * @return a string representation of the date
     */
    public static final String convertDateToString(String pattern, Date aDate)
    {
	return getDateTime(pattern, aDate);
    }


    public static Date getDayDate(int day)
    {
	Calendar c = Calendar.getInstance();
	c.add(Calendar.DATE, day);
	return c.getTime();
    }
    
    public static Date getDayByDate(Date date,int day){
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, day);
	return c.getTime();
    }


    public static Date getMonthDate(int month)
    {
	Calendar c = Calendar.getInstance();
	c.add(Calendar.MONTH, month);
	return c.getTime();
    }

    public static Date getYearDate(int Year)
    {
	Calendar c = Calendar.getInstance();
	c.add(Calendar.YEAR, Year);
	return c.getTime();
    }


    public static Date getDayDate(Date date, int day)
    {
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, day);
	return c.getTime();
    }


    public static Date getMonthDate(Date date, int month)
    {
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.MONTH, month);
	return c.getTime();
    }


    public static Date getYearDate(Date date, int Year)
    {
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.YEAR, Year);
	return c.getTime();
    }



    public static Date getMinuteDate(int minute)
    {
	Calendar c = Calendar.getInstance();
	c.add(Calendar.MINUTE, minute);
	return c.getTime();
    }

    public static Date getHourDate(int hour){
	Calendar c = Calendar.getInstance();
	c.add(Calendar.HOUR, hour);
	return c.getTime();
    }
    
    public static boolean isToday(long time){
    	Calendar morning = Calendar.getInstance(); 
    	morning.set(Calendar.HOUR_OF_DAY, 0); 
    	morning.set(Calendar.SECOND, 0); 
    	morning.set(Calendar.MINUTE, 0); 
    	morning.set(Calendar.MILLISECOND, 0);
    	
    	Calendar night = Calendar.getInstance(); 
    	night.set(Calendar.HOUR_OF_DAY, 24); 
    	night.set(Calendar.SECOND, 0); 
    	night.set(Calendar.MINUTE, 0); 
    	night.set(Calendar.MILLISECOND, 0); 
    	
    	return (morning.getTimeInMillis()<=time&&time<=night.getTimeInMillis())?true:false;
    }
    
    //�??两个?��????�?��??
    public static long getDiscrepDate(Date nowDate,Date oldDate){
	long difference= (nowDate.getTime()- oldDate.getTime()) /(24*60*60*1000); 
	return difference;
	
    }

    public static int getAge(Date birthDay)
    {
	Calendar cal = Calendar.getInstance();
	if (cal.before(birthDay))
	{
	    throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
	}
	int yearNow = cal.get(Calendar.YEAR);
	int monthNow = cal.get(Calendar.MONTH);
	int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
	cal.setTime(birthDay);
	int yearBirth = cal.get(Calendar.YEAR);
	int monthBirth = cal.get(Calendar.MONTH);
	int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
	int age = yearNow - yearBirth;
	if (monthNow <= monthBirth)
	{
	    if (monthNow == monthBirth)
	    {
		if (dayOfMonthNow < dayOfMonthBirth)
		{
		    age--;
		}
	    }
	    else
	    {

		age--;
	    }
	}
	return age;
    }
    
    public static void main(String[] args) {
	String date1 = "2012-05-28";
	String date2 = "2012-03-10";
	try {
	    
	  long day =   DateUtil.getDiscrepDate(DATE_FORMAT.parse(date1),DATE_FORMAT.parse(date2));
	  System.out.println(day);
	} catch (ParseException e) {
	 
	    e.printStackTrace();
	}
    }


}
