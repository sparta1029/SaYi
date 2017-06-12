package cn.sparta1029.sayi.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GetDateTime {

public static String  getDateTimeStringFromDate(Date date)
{
	SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	String dateString=formatter.format(date);
		return dateString;
	}
}


