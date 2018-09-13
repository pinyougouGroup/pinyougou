package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Andronicus
 *
 */
public class DateUtils {
	private static final int CURRENT = 0;
	private static final int AGO = -1;
	private static final int FEATRUE = 1;
	private DateUtils(){}
	
	public static String getAgeByBirthday(Date birthday) {
		Date now = new Date();
		long day=(now.getTime()-birthday.getTime())/(24*60*60*1000) + 1;
		String year=new java.text.DecimalFormat("#.00").format(day/365f);
		return year;
	}
	
	public static int compareWithTwoDate(Date specifiedTime, Date time) {
		if (time == null) {
			throw new IllegalArgumentException("Parameter 'time' could not be null!");
		}
		long[] points = null;
		if (specifiedTime != null) {
			points = getPointOfDate(specifiedTime);
		} else {
			points = getPointOfDate();
		}
		if (time.getTime() >= points[0] && time.getTime() <= points[1]) {
			return CURRENT;
		} else if (time.getTime() < points[0]) {
			return AGO;
		} else {
			return FEATRUE;
		}
	}
	
	public static boolean isToday(Date time) {
		if (compareWithTwoDate(null, time) == CURRENT) {
			return true;
		}
		return false;
	}
	
	public static boolean isBefore(Date time) {
		if (compareWithTwoDate(null, time) == AGO) {
			return true;
		}
		return false;
	}
	
	public static boolean isFeatrue(Date time) {
		if (compareWithTwoDate(null, time) == FEATRUE) {
			return true;
		}
		return false;
	}
	
	private static long[] getPointOfDate() {
		return getPointOfDate(null);
	}
	
	private static long[] getPointOfDate(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) {
			c.setTime(date);
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long beginPoint = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		long endPoint = c.getTimeInMillis();
		return new long[]{beginPoint, endPoint};
	}
	
	/**
	 * 获取当周第一天和最后一天
	 * 
	 * @author rxg
	 * @param day
	 * @return
	 */
	public static Date[] getWeekStartAndEndDate(Date day) {
		int mondayPlus = 0;
		Calendar cd = Calendar.getInstance();
		cd.setTime(day);
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 1) {
			mondayPlus = 0;
		} else if(dayOfWeek == 0){
			mondayPlus = -6;
		}else {
			mondayPlus = 1 - dayOfWeek;
		}
		cd.add(GregorianCalendar.DATE, mondayPlus);
		Date firstDay = cd.getTime();// 本周第一天
		cd.add(Calendar.DAY_OF_WEEK, 6);
		Date lastDay = cd.getTime();// 本周最后一天
		Date[] dates = new Date[2];
		dates[0]=firstDay;
		dates[1]=lastDay;
		return dates;
	}
	/**
	 * 获取当月第一天和最后一天
	 * 
	 * @author rxg
	 * @param day
	 * @return
	 */
	public static Date[] getMonthStartAndEndDate(Date day) {
		 //获取当前月第一天：
        Calendar c = Calendar.getInstance(); 
        c.setTime(day);
        c.set(Calendar.DAY_OF_MONTH, c     
                .getActualMinimum(Calendar.DAY_OF_MONTH));  
        Date firstDay = c.getTime();
        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();  
        ca.setTime(day);
        ca.set(Calendar.DAY_OF_MONTH, ca     
                .getActualMaximum(Calendar.DAY_OF_MONTH));     
        Date lastDay = ca.getTime();
		Date[] dates = new Date[2];
		dates[0]=firstDay;
		dates[1]=lastDay;
		return dates;
	}
	
	public static String[] getDayStartAndEndTimePointStr(Date day){
		String[] dayStrs = new String[2];
		String dayStr = formatDateToStr(day);
		String startTimePoint = new StringBuffer().append(dayStr).append(" ").append("00:00:00").toString();
		String endTimePoint = new StringBuffer().append(dayStr).append(" ").append("23:59:59").toString();
		dayStrs[0] = startTimePoint;
		dayStrs[1] = endTimePoint;
		return dayStrs;
	}
	
	public static String formatDateToStr(Date day){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(day);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date dateAddOrSubtract(Date day,int size){
		 Calendar  calendar  =  new  GregorianCalendar(); 
	     calendar.setTime(day); 
	     calendar.add(Calendar.DATE,size);
	     return calendar.getTime();
	}
	
	public static List<Date> getPeriodOfTime(Date startDay,Date endDay){
		List<Date> dayList = new ArrayList<Date>();
		dayList.add(startDay);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间  
        calBegin.setTime(startDay);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间  
        calEnd.setTime(endDay);
        // 测试此日期是否在指定日期之后  
        while (endDay.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量  
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            dayList.add(calBegin.getTime());
        }
        return dayList;
	}
	
}