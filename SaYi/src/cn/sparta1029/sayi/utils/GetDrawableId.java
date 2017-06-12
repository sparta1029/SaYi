package cn.sparta1029.sayi.utils;

import java.lang.reflect.Field;

import cn.saprta1029.sayi.R;

public class GetDrawableId {
	public static int getDrawableId(String key) {

	      try {
	            String name = key;
	            Field field = R.drawable.class.getField(name);
	            return field.getInt(null);
	        } catch (SecurityException e) {
	        } catch (NoSuchFieldException e) {
	        } catch (IllegalAccessException e) {
	        }
	        return -1;
	    }
	
}
