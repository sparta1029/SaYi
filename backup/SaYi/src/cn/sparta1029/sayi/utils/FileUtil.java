package cn.sparta1029.sayi.utils;

import java.io.File;

import android.text.TextUtils;

public class FileUtil {
	/**   
     * 获取文件夹大小   
     * @param file File实例   
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
     * 删除指定目录下文件及目录    
     * @param deleteThisPath   
     * @param filepath   
     * @return    
     */     
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {     
        if (!TextUtils.isEmpty(filePath)) {     
            try {  
                File file = new File(filePath);     
                if (file.isDirectory()) {// 处理目录     
                    File files[] = file.listFiles();     
                    for (int i = 0; i < files.length; i++) {     
                        deleteFolderFile(files[i].getAbsolutePath(), true);     
                    }      
                }     
                if (deleteThisPath) {     
                    if (!file.isDirectory()) {// 如果是文件，删除     
                        file.delete();     
                    } else {// 目录     
                   if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除     
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
