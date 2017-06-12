package cn.sparta1029.sayi.utils;

import java.util.ArrayList;

public class IsInBlacklist {
	public static boolean isInBlacklist(ArrayList<String> blacklistAccountList,String name) {
boolean isInBlacklist=false;
for(int i=0;i<blacklistAccountList.size();i++)
{
    if(	blacklistAccountList.get(i).equals(name))
    	isInBlacklist=true;
    i=blacklistAccountList.size();
}
		return isInBlacklist;
}
}
