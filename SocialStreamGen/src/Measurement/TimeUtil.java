package Measurement;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	String time;
	Calendar calendar;
	Long seconds;
	public TimeUtil(String itime) throws ParseException{
		SimpleDateFormat fmt =null;
		time = itime;
		if(time.contains(" ")){
			 fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else{
			fmt = new SimpleDateFormat("yyyy-MM-dd");
		}
		Date date = fmt.parse(time);
		this.calendar = Calendar.getInstance();
		this.calendar.setTime(date);
		this.seconds = calendar.getTimeInMillis() / 1000;
	}
	
	public TimeUtil(Long itime){
		SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(itime*1000);
		this.calendar = Calendar.getInstance();
		this.calendar.setTime(date);
		this.time = fmt.format(date);
	}
	
	public Calendar getCalendar() {
		
		return calendar;
	}

	public long getSeconds() {
		return seconds;
	}
	public String getTime() {
		return time;
	}
	
}