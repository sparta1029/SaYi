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
     * ��SP����ָ��key��Ӧ������ 
     * ����value������String��boolean��float��int��long�ȸ��ֻ������͵�ֵ 
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
     * ���SP���������� 
     */  
    public void clear() {  
        editor.clear();  
        editor.commit();  
    }  
  
    /** 
     * ɾ��SP��ָ��key��Ӧ�������� 
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
     * ��ȡSP������ָ��key��Ӧ��value�����key�����ڣ��򷵻�Ĭ��ֵdefValue�� 
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
     * �ж�SP�Ƿ�����ض�key������ 
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
