package rxjava2.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	static SimpleDateFormat sdf = new SimpleDateFormat("YYYY:MM:dd HH:ss");
	public static void log(String msg) {
		System.out.println(String.format("%s %s %s ", sdf.format(new Date()), Thread.currentThread(), msg));
	}
	
	public static void log(String format, Object... objects) {
		String s = String.format(format, objects);
		log(s);
	}
}
