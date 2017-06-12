package cn.sparta1029.sayi.utils;

import java.io.File;

import android.text.TextUtils;

public class FileUtil {
	/**   
     * ��ȡ�ļ��д�С   
     * @param file Fileʵ��   
     * @return long      
     */     
    public static long getFolderSize(java.io.File file){    
   
        long size = 0;    
        try {  
            java.io.File[] fileList = file.listFiles();     
            for (int i = 0; i < fileList.length; i++)     
            {     
                if (fileList[i].isDirectory())     
                {     
                    size = size + getFolderSize(fileList[i]);    
   
                }else{     
                    size = size + fileList[i].length();    
   
                }     
            }  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }     
       //return size/1048576;    
        return size;    
    }
    
    
    
    
    /**   
     * ɾ��ָ��Ŀ¼���ļ���Ŀ¼    
     * @param deleteThisPath   
     * @param filepath   
     * @return    
     */     
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {     
        if (!TextUtils.isEmpty(filePath)) {     
            try {  
                File file = new File(filePath);     
                if (file.isDirectory()) {// ����Ŀ¼     
                    File files[] = file.listFiles();     
                    for (int i = 0; i < files.length; i++) {     
                        deleteFolderFile(files[i].getAbsolutePath(), true);     
                    }      
                }     
                if (deleteThisPath) {     
                    if (!file.isDirectory()) {// ������ļ���ɾ��     
                        file.delete();     
                    } else {// Ŀ¼     
                   if (file.listFiles().length == 0) {// Ŀ¼��û���ļ�����Ŀ¼��ɾ��     
                            file.delete();     
                        }     
                    }     
                }  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }     
        }     
    }
}
