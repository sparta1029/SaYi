package cn.sparta1029.sayi.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetNetWorkTime {
	private static String  webUrl = "http://www.ntsc.ac.cn";//�й���ѧԺ������ʱ����
	public static String getWebsiteDatetime(){
        try {
            URL url = new URL(webUrl);// ȡ����Դ����
            URLConnection uc = url.openConnection();// �������Ӷ���
            uc.connect();// ��������
            long ld = uc.getDate();// ��ȡ��վ����ʱ��
            Date date = new Date(ld);// ת��Ϊ��׼ʱ�����
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// �������ʱ��
            return sdf.format(date);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
}
}
