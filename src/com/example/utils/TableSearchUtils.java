package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class TableSearchUtils {

	final static String WORKSPACE = "C:\\cosmeteem\\workspace";
	static String BATPATH = "IFRS17Batch\\src\\main\\resources\\mapper\\sqlmap\\koreanre\\fr\\ie\\dc\\";
	static String WEBPATH = "IFRS17Web\\src\\main\\resources\\sqlmap\\ie\\dc\\";
	
	static boolean ISSQLID = false; // true주면 sql_ID별로 나옴
	static boolean ISIU = false; // insert, update >> 배치설계 할 때 true하면 편함
	static boolean ISFUNC = false; // db함수 포함?
	
	public static void main(String[] args) throws Exception {
		
		// sql_ID별
//		ISSQLID = true;
		// insert나 update는 IU♥ 표시 해주기
		ISIU = true;
		
		ArrayList<String[]> tabs = getTabs(new File(WORKSPACE + BATPATH));
		tabs.addAll(getTabs(new File(WORKSPACE + WEBPATH)));
		
//		tabs.stream().forEach(arr -> System.out.println(Arrays.toString(arr)));
		HashMap<String, HashSet<String>> pgmap = new HashMap<>();
		HashMap<String, HashSet<String>> tabmap = new HashMap<>();
		
		for(String[] tab : tabs) {
			HashSet<String> pgSet = pgmap.get(tab[1]);
			if(pgSet == null) {
				pgSet = new HashSet<String>();
				pgmap.put(tab[1], pgSet);
			}
			
			pgSet.add(tab[0]);
			
			HashSet<String> tabSet = tabmap.get(tab[0]);
			if(tabSet == null) {
				tabSet = new HashSet<String>();
				tabmap.put(tab[0].replaceAll("IU♥", ""), tabSet);
			}
			
			tabSet.add(tab[1]);
		}
		
		for(Entry<String, HashSet<String>> es : pgmap.entrySet()) {
			System.out.println(es.toString());
		}
		
		System.out.println("-------------------------------------------------------");
		
		for(Entry<String, HashSet<String>> es : tabmap.entrySet()) {
			System.out.println(es.toString());
		}
		
	}
	
	private static ArrayList<String[]> getTabs(File src) throws Exception {
		
		ArrayList<String[]> tabList = new ArrayList<>();
		
		if(src.isDirectory()) {
			for(File file : src.listFiles()) {
				tabList.addAll(getTabs(file));
			}
		}else {
			tabList.addAll(getTab(src));
		}
		
		return tabList;
		
	}
	
	private static ArrayList<String[]> getTab(File sql) throws Exception {
		
		ArrayList<String[]> tabList = new ArrayList<>();
		
		FileInputStream fis = new FileInputStream(sql);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		
		String read = null;
		
		while((read = br.readLine()) != null) {
			
			String[] split = read.split("\\.|\\s|\\(");
			split = Arrays.stream(split).filter(s->(s!=null&&s.length()>0)).toArray(String[]::new); // 빈배열 제거..
			
			for(int i=1; i<split.length; i++) {
				if(split[i-1].equals("fr_own") || split[i-1].equals("fr_tmp")) {
					
					String tabName = split[i];
					
					if(!ISFUNC && tabName.startsWith("fn_")) {
						continue;
					}
					
					if(ISIU && (i>=2) && ("into".equals(split[i-2]) || "update".equals(split[i-2]))) {
						tabName = "IU♥" + tabName;
					}
					
					tabList.add(new String[] {tabName, sql.getName().substring(0, (sql.getPath().contains(BATPATH) && ISSQLID)?10:8)});
					
				}
			}
			
		}
		
		fis.close();
		isr.close();
		br.close();
		
		return tabList;
		
	}
	
}
