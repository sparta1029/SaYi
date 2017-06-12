package cn.sparta1029.sayi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class GetWeather {
		  
		  
		    private URL url = null;  
		  
		    /** 
		     * ����URL�����ļ�,ǰ��������ļ����е��������ı�,�����ķ���ֵ�����ı����е����� 1.����һ��URL���� 
		     * 2.ͨ��URL����,����һ��HttpURLConnection���� 3.�õ�InputStream 4.��InputStream���ж�ȡ���� 
		     *  
		     * @param urlStr 
		     * @return 
		     */  
		    
		    public String getWeatherText(String cityName) {  
		    	String urlStr="https://api.seniverse.com/v3/weather/now.json?key="+GetNetworkPARM.getSeniverseKey()+"&location="+java.net.URLEncoder.encode(""+cityName+"")+"&language=zh-Hans&unit=c";
		    	  
		        StringBuffer sb = new StringBuffer();  
		        String line = null;  
		        BufferedReader buffer = null;  
		        try {  
		            url = new URL(urlStr);  
		            HttpURLConnection urlConn = (HttpURLConnection) url  
		                    .openConnection();  
		            urlConn.setRequestMethod("GET");  
		            urlConn.setConnectTimeout(1000);  
		            urlConn.setReadTimeout(1000);  
		            buffer = new BufferedReader(new InputStreamReader(  
		                    urlConn.getInputStream()));  
		            Log.i("test", "buffer:"+buffer);
		            while ((line = buffer.readLine()) != null) {  
		                sb.append(line);  
		            }  
		  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        } finally {  
		            try {  
		                buffer.close();  
		            } catch (IOException e) {  
		                e.printStackTrace();  
		            }  
		        }  
		        return sb.toString();  
		    }  
	
	
	
	
	
	
	
	
}
