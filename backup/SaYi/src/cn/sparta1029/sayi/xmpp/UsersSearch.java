package cn.sparta1029.sayi.xmpp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.util.Log;

public class UsersSearch {
	static XMPPConnection connection;
	final static int ACCOUNTLENGTHMAX = 15;
	
	
	public static List<UserSearchEntity> searchUsers(XMPPConnection connection,String serverAddress,String userName) throws XMPPException  
	    {  
		//	configure(ProviderManager.getInstance());
	        List<UserSearchEntity> results = new ArrayList<UserSearchEntity>();  
	        UserSearchManager usm = new UserSearchManager(connection);  
	        
	        Form searchForm = usm.getSearchForm("search." +connection.getServiceName());  
	        Form answerForm = searchForm.createAnswerForm();  
	        answerForm.setAnswer("Username", true);  
	        answerForm.setAnswer("search", userName);  
	        ReportedData data = usm.getSearchResults(answerForm, "search." +connection.getServiceName());  
	        Iterator<Row> it = data.getRows();  
	         Row row = null;  
	         UserSearchEntity userSearch = null;  
	         while(it.hasNext())  
	         {   
	        	 row = it.next();  
	        	 userSearch = new UserSearchEntity(row.getValues("Username").next().toString(),row.getValues("Name").next().toString(),row.getValues("Email").next().toString());  
	        	 if(userSearch.getUserName().length()<=ACCOUNTLENGTHMAX)
	        	 results.add(userSearch);  
	             //若存在，则有返回
	         }  
	         return results;  
	    }

	public static List<UserSearchEntity> searchUsersForgetPWD(XMPPConnection connection,String serverAddress,String userName) throws XMPPException  
	    {  
		//	configure(ProviderManager.getInstance());
	        List<UserSearchEntity> results = new ArrayList<UserSearchEntity>();  
	        UserSearchManager usm = new UserSearchManager(connection);  
	        Log.i("searchtest","searchtest:"+connection.getServiceName());
	        Form searchForm = usm.getSearchForm("search." +connection.getServiceName());  
	        Form answerForm = searchForm.createAnswerForm();  
	        answerForm.setAnswer("Username", true);  
	        answerForm.setAnswer("search", userName);  
	        ReportedData data = usm.getSearchResults(answerForm, "search." +connection.getServiceName());  
	        Iterator<Row> it = data.getRows();  
	         Row row = null;  
	         UserSearchEntity userSearch = null;  
	         while(it.hasNext())  
	         {   
	        	 row = it.next();  
	        	 userSearch = new UserSearchEntity(row.getValues("Username").next().toString(),row.getValues("Name").next().toString(),row.getValues("Email").next().toString());  
	        	 if(userSearch.getUserName().length()>ACCOUNTLENGTHMAX)
		        	  results.add(userSearch);  
	             //若存在，则有返回
	         }  
	         return results;  
	    }
	
}
