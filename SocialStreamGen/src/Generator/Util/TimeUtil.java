package Generator.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public SimpleDateFormat fmt; 

	public TimeUtil(String dataType){
		if(dataType.equals("weibo") ||dataType.equals("email")){
			setDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		if(dataType.equals("patent")){
			setDateFormat("yyyy-MM-dd");
		}
	}
	
	public void setDateFormat(String format){
		 fmt = new SimpleDateFormat(format);//"yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd"
	}
	
	public long changeTimeToSeconds(String time)  {
		Date date=null;
		try {
			date = fmt.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(fmt==null){
				System.err.println("Please set data format first.");
			}
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		long rsult = calendar.getTimeInMillis() / 1000;
		return rsult;
	}

	public String changeTimeToString(Long time) {
		return fmt.format(new Date(time * 1000));
	}
	
	public long getNextRightCursor(long time){
		Date date= new Date(time*1000);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis() / 1000;
	}

	public long getNextLeftCursor(long time){
		Date date= new Date(time*1000);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis() / 1000;
	}
}