package org.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
*   @desc : 时间工具类
*   @auth : TYF
*   @date : 2019-10-14 - 11:26
*/
public class TimeTools {



	// 时间格式数组
	private static String[] formatArray = {
			"yyyy-MM-dd",
			"yyyy-MM-dd HH:mm",
			"yyyy-MM-dd HH:mm:ss",
			"yy-MM-dd HH:mm",
			"yyyyMMdd HH:mm",
			"yyyy-MM-dd HH",
			"yyyy-MM",
			"yyyyMMddHHmmss",
			"yyyyMMdd",
			"yyyy/MM/dd",
			"HH:mm:ss",
			"yyyy-MM-dd-HH-mm-ss",
			"MM/dd HH:mm:ss",
			"yyyy-MM-dd HH:mm:ss SSS",
			"HH:mm:ss SSS"
	};



	/**
	*   @desc : 时间戳转时间字符串
	*   @auth : TYF
	*   @date : 2019-10-14 - 13:25
	*/
	public static String timeStempToTimeStr(Long millisecond){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatArray[2]);
		Date date = new Date(millisecond);
		String  res = simpleDateFormat.format(date);
		return res;
	}

	/**
	 *   @desc : 时间戳转时间字符串
	 *   @auth : TYF
	 *   @date : 2019-10-14 - 13:25
	 */
	public static String timeStempToTimeStr(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatArray[10]);
		Date date = new Date(System.currentTimeMillis());
		String  res = simpleDateFormat.format(date);
		return res;
	}

	public static String timeStempToTimeStr(int index){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatArray[index]);
		Date date = new Date(System.currentTimeMillis());
		String  res = simpleDateFormat.format(date);
		return res;
	}


	/**
	 *   @desc : 时间戳转时间字符串
	 *   @auth : TYF
	 *   @date : 2019-10-14 - 13:25
	 */
	public static String timeStempToTimeStr(Long millisecond,Integer index){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatArray[index]);
		Date date = new Date(millisecond);
		String  res = simpleDateFormat.format(date);
		return res;
	}


	/**
	*   @desc : 获得某天最大时间 2019-10-15 23:59:59
	*   @auth : TYF
	*   @date : 2019-12-03 - 11:23
	*/
	public static Long getMillisecondEndOfDay(Date date) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());;
		LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
		return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();
	}

	/**
	*   @desc : 获得某天最小时间 2019-10-15 00:00:00
	*   @auth : TYF
	*   @date : 2019-12-03 - 11:23
	*/
	public static Long getMillisecondStartOfDay(Date date) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
		LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
		return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();
	}



	/**
	 *   @desc : 获取向前/向后offSet天的最小时间
	 *   @auth : TYF
	 *   @date : 2019-12-03 - 16:08
	 */
	public static Long getMillisecondStartOfDayWithOFFset(Date date, Integer offSet){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,24*offSet);
		return  getMillisecondStartOfDay(calendar.getTime());
	}

	/**
	 *   @desc : 获取向前/向后offSet天的最大时间
	 *   @auth : TYF
	 *   @date : 2019-12-03 - 16:08
	 */
	public static Long getMillisecondEndOfDayWithOFFset(Date date, Integer offSet){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,24*offSet);
		return  getMillisecondEndOfDay(calendar.getTime());
	}



	/**
	*   @desc : 获得某月的最小时间 2019-10-1 00:00:00
	*   @auth : TYF
	*   @date : 2019-12-03 - 15:47
	*/
	public static Long getMillisecondStartOfMonth(Date date){
		LocalDate today = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate firstday = LocalDate.of(today.getYear(),today.getMonth(),1);
		return Date.from(firstday.atStartOfDay(ZoneOffset.ofHours(8)).toInstant()).getTime();
	}

	/**
	 *   @desc : 获得某月的最大时间 2019-10-31 23:59:59
	 *   @auth : TYF
	 *   @date : 2019-12-03 - 15:47
	 */
	public static Long getMillisecondEndOfMonth(Date date){
		LocalDate today = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate lastDay =today.with(TemporalAdjusters.lastDayOfMonth());
		return Date.from(lastDay.atStartOfDay(ZoneOffset.ofHours(8)).toInstant()).getTime()-1+24*60*60*1000;
	}


	/**
	*   @desc : 获取向前/向后offSet个月的最小时间
	*   @auth : TYF
	*   @date : 2019-12-03 - 16:08
	*/
	public static Long getMillisecondStartOfMonthWithOFFset(Date date,Integer offSet){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, offSet);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return  getMillisecondStartOfMonth(calendar.getTime());
	}


	/**
	 *   @desc : 获取向前/向后offSet个月的最大时间
	 *   @auth : TYF
	 *   @date : 2019-12-03 - 16:08
	 */
	public static Long getMillisecondEndOfMonthWithOFFset(Date date,Integer offSet){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, offSet);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return  getMillisecondEndOfMonth(calendar.getTime());
	}

	/**
	 *   @desc : 时间字符串转时间戳
	 *   @auth : TYF
	 *   @date : 2020-01-09 - 9:07
	 */
	public static Long timeStrToTimeStemp(String timeStr,Integer index){
		SimpleDateFormat format =  new SimpleDateFormat(formatArray[index]);
		Date date = null;
		try {
			date = format.parse(timeStr);
		}catch (Exception e){
			return null;
		}
		//日期转时间戳（毫秒）
		long timeStemp=date.getTime();
		return timeStemp;
	}

	/**
	 *   @desc : 时间字符串转date
	 *   @auth : TYF
	 *   @date : 2020-01-09 - 9:07
	 */
	public static Date timeStrToDate(String timeStr, Integer index){
		SimpleDateFormat format =  new SimpleDateFormat(formatArray[index]);
		Date date = null;
		try {
			date = format.parse(timeStr);
		}catch (Exception e){
			return null;
		}
		return date;
	}


}
