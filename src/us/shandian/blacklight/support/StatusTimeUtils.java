package us.shandian.blacklight.support;

import android.content.Context;
import android.content.res.Resources;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import us.shandian.blacklight.R;

/*
  credits to: Sina Weibo SDK / qii / me
*/
public class StatusTimeUtils
{
	private static final long MILLIS_MIN = 1000 * 60;
	private static final long MILLIS_HOUR = MILLIS_MIN * 60;
	private static final long MILLIS_DAY = MILLIS_HOUR * 24;
	
	private static String JUST_NOW, MIN, HOUR, DAY, MONTH, YEAR,
							YESTERDAY, THE_DAY_BEFORE_YESTERDAY, TODAY;
	
	private static final String FORMAT_DATE = "M-d HH:mm";
	private static final String FORMAT_YEAR = "yyyy-M-d HH:mm";
	
	private static SimpleDateFormat day_format = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat date_format = new SimpleDateFormat("M-d HH:mm");
	private static SimpleDateFormat year_format = new SimpleDateFormat("yyyy-M-d HH:mm");
	
	
	private static StatusTimeUtils mInstance;
	
	private StatusTimeUtils(Context context) {
		Resources res = context.getResources();
		JUST_NOW = res.getString(R.string.just_now);
		MIN = res.getString(R.string.min);
		HOUR = res.getString(R.string.hour);
		DAY = res.getString(R.string.day);
		MONTH = res.getString(R.string.month);
		YEAR = res.getString(R.string.year);
		YESTERDAY = res.getString(R.string.yesterday);
		THE_DAY_BEFORE_YESTERDAY = res.getString(R.string.the_day_before_yesterday);
		TODAY = res.getString(R.string.today);
	}
	
	public static StatusTimeUtils instance(Context context) {
		if (mInstance == null) {
			mInstance = new StatusTimeUtils(context);
		}
		
		return mInstance;
	}
	
	private boolean isSameSemiDay(Calendar now, Calendar msg) {
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		int msgHour = msg.get(Calendar.HOUR_OF_DAY);
		
		return (nowHour <= 12 && msgHour <= 12) || (nowHour >= 12) && (msgHour >= 12);
	}
	
	private boolean isSameDay(Calendar now, Calendar msg) {
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int msgDay = msg.get(Calendar.DAY_OF_YEAR);

		return nowDay == msgDay;
	}
	
	private boolean isYesterDay(Calendar now, Calendar msg) {
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int msgDay = msg.get(Calendar.DAY_OF_YEAR);

		return nowDay == (msgDay + 1);
	}
	
	private boolean isTheDayBeforeYesterday(Calendar now, Calendar msg) {
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int msgDay = msg.get(Calendar.DAY_OF_YEAR);

		return nowDay == (msgDay - 2);
	}
	
	private boolean isSameYear(Calendar now, Calendar msg) {
		int nowYear = now.get(Calendar.YEAR);
		int msgYear = msg.get(Calendar.YEAR);

		return nowYear == msgYear;
	}
	
	public String buildTimeString(String created_at) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(created_at));
		long msg = cal.getTimeInMillis();
		long now = System.currentTimeMillis();
		
		Calendar nowCalendar = Calendar.getInstance();
		
		long differ = now - msg;
		long difsec = differ / 1000;
		
		if (difsec < 60) {
			return JUST_NOW;
		}
		
		long difmin = difsec / 60;
		
		if (difmin < 60) {
			return String.valueOf(difmin) + MIN;
		}
		
		long difhour = difmin / 60;
		
		if (difhour < 24 && isSameDay(nowCalendar, cal)) {
			return TODAY + " " + day_format.format(cal.getTime());
		}
		
		long difday = difhour / 24;
		
		if (difday < 31) {
			if (isYesterDay(nowCalendar, cal)) {
				return YESTERDAY + " " + day_format.format(cal.getTime());
			} else if (isTheDayBeforeYesterday(nowCalendar, cal)) {
				return THE_DAY_BEFORE_YESTERDAY + " " + day_format.format(cal.getTime());
			} else {
				return date_format.format(cal.getTime());
			}
		}
		
		long difmonth = difday / 31;
		
		if (difmonth < 12 && isSameYear(nowCalendar, cal)) {
			return date_format.format(cal.getTime());
		}
		
		return year_format.format(cal.getTime());
	}
}
