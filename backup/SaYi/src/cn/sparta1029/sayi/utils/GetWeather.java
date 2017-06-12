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
		     * 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容 1.创建一个URL对象 
		     * 2.通过URL对象,创建一个HttpURLConnection对象 3.得到InputStream 4.从InputStream当中读取数据 
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
