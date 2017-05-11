package cn.sparta1029.sayi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {  
    private SharedPreferences preferences;  
    private SharedPreferences.Editor editor;  
    private String fileName="setting_info";
    public String keyAddress="server_address"; 
    public String keyCurrentUser="current_user"; 
    public String keyCurrentPassword="current_password"; 
    public String keySavedPassword="saved_password"; 
    public String keyAutoLogin="auto_login_state"; 
    public String booleanAutoLoginTrue="true"; 
    public String booleanAutoLoginFalse="false"; 
    @SuppressLint("CommitPrefEdits")
	public SPUtil(Context context) {  
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);  
        editor = preferences.edit();  
    }  
  
  
    /** 
     * 向SP存入指定key对应的数据 
     * 其中value可以是String、boolean、float、int、long等各种基本类型的值 
     * @param key 
     * @param value 
     */  
    public void putString(String key, String value) {  
    	try {
	        editor.putString(CipherUtil.encrypt(key), CipherUtil.encrypt(value));  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
        editor.commit();  
    }  
}
  
    public void putBoolean(String key, boolean value) {  
        editor.putBoolean(key, value);  
        editor.commit();  
    }  
 
    /** 
     * 清空SP里所以数据 
     */  
    public void clear() {  
        editor.clear();  
        editor.commit();  
    }  
  
    /** 
     * 删除SP里指定key对应的数据项 
     * @param key 
     */  
    public void remove(String key) {  
    	try {
			key=CipherUtil.encrypt(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        editor.remove(key);  
        editor.commit();  
    }  
  
    /** 
     * 获取SP数据里指定key对应的value。如果key不存在，则返回默认值defValue。 
     * @param key 
     * @param defValue 
     * @return 
     */  
    public String getString(String key, String defValue) {  
    	try {
    		key=CipherUtil.encrypt(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String result=preferences.getString(key, defValue); 
    	try {
    		result=CipherUtil.decrypt(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result;
		
    }  
  
    public boolean getBoolean(String key, boolean defValue) {  
        return preferences.getBoolean(key, defValue);  
    }  
  
    /** 
     * 判断SP是否包含特定key的数据 
     * @param key 
     * @return 
     */  
    public boolean contains(String key){ 
    	try {
			key=CipherUtil.encrypt(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return preferences.contains(key);  
    }  
  
}  
