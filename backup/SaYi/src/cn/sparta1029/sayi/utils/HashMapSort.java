package cn.sparta1029.sayi.utils;

import java.util.Comparator;
import java.util.HashMap;

public class HashMapSort implements Comparator<HashMap<String, String>> {
		private boolean isAsc; // 是否为升序，true:升序，false:降序
		private boolean isNum; // 排序value是否为数值型
		private String key; // 根据哪个key排序
		public HashMapSort(boolean isAsc, boolean isNum, String key) {
			this.isAsc = isAsc;
			this.isNum = isNum;
			this.key = key;
		}
		@Override
		public int compare(HashMap<String, String> hashMap1,
				HashMap<String, String> hashMap2) {
			String v1 = hashMap1.get(this.key);
			String v2 = hashMap2.get(this.key);
			if (!isNum) {
				return isAsc ? (v1.compareTo(v2)) : (v2.compareTo(v1));
			} else {
				if (Double.parseDouble(v1) > Double.parseDouble(v2)) {
					return isAsc ? 1 : -1;
				}else if(Double.parseDouble(v1) < Double.parseDouble(v2)) {
					return isAsc ? -1 : 1;
				}else {
					return 0;
				}
			}
	}
}
