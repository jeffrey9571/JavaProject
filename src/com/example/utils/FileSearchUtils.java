package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 파일에서 해당 조건으로 찾기
 * @author Jeffrey
 *
 */
public class FileSearchUtils {

	private static ArrayList<String> FORMIDERR = new ArrayList<>();
	private static ArrayList<String> TITLEERR = new ArrayList<>();
	private static ArrayList<String> CLASSNMERR = new ArrayList<>();
	private static ArrayList<String> SYSOUTERR = new ArrayList<>();
	private static ArrayList<String> MTHDNMWARN = new ArrayList<>();
	private static ArrayList<String> DEFIDWARN = new ArrayList<>();
	private static ArrayList<String> BIZERR = new ArrayList<>();
	private static ArrayList<String> CONTAINTABLE = new ArrayList<>();
	private static ArrayList<String> CONTAINTEXT = new ArrayList<>();
	
	private static int uicnt = 0; //화면모수
	private static int cmpcnt = 0; //컴포넌트모수
	private static int srvcnt = 0; //서비스모수
	private static int pmthdcnt = 0; //퍼블릭메서드모수
	
	private final static String WORKSPACEROOT = "D:\\workspace2\\";
	private final static String PROJECTNM = "COSMETEEM_STEEM";
	
	private final static String[] TEXTLIST = {"(+)"};
	
	private final static String[] TABLELIST = {"IA_AC_BDG_CNTR_BS"
			, "IA_AC_BDG_CNTR_JREN_DC"
	};
	
	private final static String[] BIZDV = {"co\\pu\\pr"
			, "co\\pu\\uw"
			, "co\\pu\\gi"
	};
	
	private final static String[][] UIARRS = new String[][] {
		{"IDDIM001", "표준항목관리"}
		, {"IDDIM002", "원수사파일유형관리"}
		, {"IDDIM003", "원수사-표준항목매핑"}
		, {"IDDIM004", "원수사파일업로드"}
	};
	
	private final static String[] ELENAMES = {"Div"
			, "Button"
			, "PopupDiv"
			, "Combo"
	};
	
	public static void main(String[] args) throws Exception {
		File root = null;
		
		/*************************** 경로 설정 시작 ***************************/
		//화면
//		root = new File("C:\\workspace\\...");
//		getErr(root);
		
		//화면서비스
		root = new File(WORKSPACEROOT + PROJECTNM + "\\src\\main\\java");
		getErr(root);
		
		//화면쿼리
//		root = new File(WORKSPACEROOT + PROJECTNM + "\\src\\main\\java");
//		getErr(root);
		/*************************** 경로 설정 종료 ***************************/
		
		/*************************** 출력 시작 ***************************/
		//업무구분 경로 아님
		if(!BIZERR.isEmpty()) {
//			System.out.println("업무구분경로 오류(" + (int)Math.floor((double)BIZERR.size()/(uicnt+srvcnt)*100) + "%)\n" + BIZERR);
		}
		
		//파일명.formid 다름
		if(!FORMIDERR.isEmpty()){
//			System.out.println("\n\n파일명.formid 오류(" + (int)Math.floor((double)FORMIDERR.size()/uicnt*100) + "%)\n" + FORMIDERR);
		}
		
		//명세타이틀 다름
		if(!TITLEERR.isEmpty()) {
//			System.out.println("\n\nUI명세타이블 오류(" + (int)Math.floor((double)TITLEERR.size()/UIARRS.length*100) + "%\n" + TITLEERR);
		}
		
		//클래스명명규칙
		if(!CLASSNMERR.isEmpty()) {
//			System.out.println("\n\n클래스명명규칙 예외(" + (int)Math.floor((double)CLASSNMERR.size()/srvcnt*100) + "%)\n" + CLASSNMERR);
		}
		
		//system.out 사용
		if(!SYSOUTERR.isEmpty()) {
//			System.out.println("\n\nsystem.out 사용(" + (int)Math.floor((double)SYSOUTERR.size()/srvcnt*100) + "%\n" + SYSOUTERR);
		}
		
		//TABLE 사용
		if(!CONTAINTABLE.isEmpty()) {
//			System.out.println("\n\nTABLE 사용(" + CONTAINTABLE);
		}
		
		//TEXT 사용
		if(!CONTAINTEXT.isEmpty()) {
//			System.out.println("\n\nTEXT 사용(" + CONTAINTEXT);
		}
		
		//id규칙 무시
		if(!DEFIDWARN.isEmpty()) {
//			System.out.println("\n\n컴포넌트 id규칙 예외(" + (int)Math.floor((double)DEFIDWARN.size()/cmpcnt*100) + "%\n" + DEFIDWARN);
		}
		
		//메서드명명규칙
		if(!MTHDNMWARN.isEmpty()) {
//			System.out.println("\n\n메서드명명 규칙 예외(" + (int)Math.floor((double)MTHDNMWARN.size()/pmthdcnt*100) + "%\n" + MTHDNMWARN);
		}
		/*************************** 출력 종료 ***************************/
	}
	
	private static void getErr(File src) throws Exception {
		if(src.isDirectory()) {
			for(File file : src.listFiles()) {
				getErr(file);
			}
		}else {
			if(src.getName().endsWith("xfdl")) {
				printUiErr(src);
			}else if(src.getName().endsWith("java")) {
				printSrvErr(src);
			}else if(src.getName().endsWith("xml")) {
				printXmlErr(src);
			}
		}
	}
	
	private static void printUiErr(File src) throws Exception {
		isContainBiz(src);
		
		uicnt++;
		DocumentBuilder dbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dbuilder.parse(src);
		Element elem = doc.getDocumentElement();
		elem.normalize();
		
		String fileid = src.getName().replaceAll(".xfdl", "");
		NodeList ndList = elem.getElementsByTagName("Form");
		for(int i=0; i<ndList.getLength(); i++) {
			Node ele = ndList.item(i);
			NamedNodeMap attrs = ele.getAttributes();
			if(!fileid.equals(attrs.getNamedItem("id").getNodeValue())) {
				FORMIDERR.add(src.getPath() + "//" + fileid + " : form.id=" + attrs.getNamedItem("id").getNodeValue() + "\n");
			}
			for(String[] uititle : UIARRS) {
				if(fileid.equals(uititle[0])) {
					String title = "[" + uititle[0] + "]" + uititle[1].replaceAll("[(팝업)]", "").replaceAll("\\s+", "");
					String titletext = attrs.getNamedItem("titletext").getNodeValue().replaceAll("\\s+", "");
					if(!title.equals(titletext)) {
						TITLEERR.add(src.getPath() + "//" + fileid + " : 타이틀=" + title + " - " + titletext + "\n");
					}
					break;
				}
			}
		}
		//default id warning
		for(String elename : ELENAMES) {
			ndList = elem.getElementsByTagName(elename);
			ArrayList<String> arrlist = new ArrayList<>();
			for(int i=0; i<ndList.getLength(); i++) {
				cmpcnt++;
				Node ele = ndList.item(i);
				NamedNodeMap attrs = ele.getAttributes();
				if(attrs.getNamedItem("id").getNodeValue().startsWith(elename)) {
					arrlist.add(attrs.getNamedItem("id").toString());
				}
			}
			if(!arrlist.isEmpty()) {
				DEFIDWARN.add(src.getPath() + "//" + fileid + " : " + arrlist + "\n");
			}
		}
	}
	
	private static void printSrvErr(File src) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(src));
		isContainBiz(src);
		
		srvcnt++;
		
		String fileid = src.getName().replaceAll(".java",  "");
		//클래스명명규칙
		if(!fileid.matches(".*(Controller|Svc|SvcImpl|Mapper|BSvc|Job|Mapper)$")) {
			CLASSNMERR.add(src.getPath() + "//" + fileid + "\n");
		}
		
		String line = null;
		int lineno = 0;
		ArrayList<Integer> arrlist = new ArrayList<>();
		Map<String, Object> tablemap = new HashMap<>();
		Map<String, Object> textmap = new HashMap<>();
		while((line = br.readLine()) != null) {
			line = line.trim();
			
			if(line.contains("System.out.println") || line.contains("e.printStackTrace")) {
				arrlist.add(lineno);
			}
			
			isContainTable(line, tablemap);
			isContainText(line, textmap);
			
			lineno++;
		}
		if(!arrlist.isEmpty()) {
			SYSOUTERR.add(src.getPath() + "//" + fileid + " : lineno." + arrlist + "\n");
		}
		if(!tablemap.keySet().isEmpty()) {
			//logic
			CONTAINTABLE.add(printEachLine(src.getPath(), mapSort(tablemap, "1")));
		}
		if(!textmap.keySet().isEmpty()) {
			//logic
			CONTAINTEXT.add(src.getPath() + "//" + fileid + " : textlist." + textmap.keySet() + "\n");
		}
		br.close();
		
		String classpath = src.getPath().replace(WORKSPACEROOT, "")
				.replace(PROJECTNM, "")
				.replace("\\src\\main\\java\\", "")
				.replaceAll("[\\\\]", ".")
				.replace(".java", "");
		File classroot = new File(WORKSPACEROOT + src.getPath().split("\\\\")[3] + "\\target\\classes");
		ArrayList<String> errlist = new ArrayList<>();
		URLClassLoader ucl = null;
		
		try {
			ucl = new URLClassLoader(new URL[] {classroot.toURI().toURL()});
			Class<?> loadClass = ucl.loadClass(classpath);
			for(Method mthd : loadClass.getDeclaredMethods()) {
				if(mthd.getModifiers() != Modifier.PUBLIC) {
					continue;
				}
				pmthdcnt++;
				if(!mthd.getName().matches("^(create|update|delete|read|main).*$")) {
					errlist.add(mthd.getName());
				}
			}
		}catch(NoClassDefFoundError e){
			System.out.println("NoClassDefFoundError=" + classpath);
		}finally {
			if(ucl != null) {
				ucl.close();
			}
		}
		if(!errlist.isEmpty()) {
			MTHDNMWARN.add(src.getPath() + "//" + fileid + " : method" + errlist + "\n");
		}
	}
	
	private static void printXmlErr(File src) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(src));
		
		String fileid = src.getName().replaceAll(".xml", "");
		
		String line = null;
		Map<String, Object> tablemap = new HashMap<>();
		Map<String, Object> textmap = new HashMap<>();
		while((line = br.readLine()) != null) {
			line = line.trim();
			isContainTable(line, tablemap);
			isContainText(line, textmap);
		}
		if(!tablemap.keySet().isEmpty()) {
			//logic
			CONTAINTABLE.add(printEachLine(src.getPath(), mapSort(tablemap, "1")));
		}
		if(!textmap.keySet().isEmpty()) {
			//logic
			CONTAINTEXT.add(src.getPath() + "//" + fileid + " : textlist." + textmap.keySet() + "\n");
		}
		br.close();
	}
	
	private static void isContainBiz(File src) {
		for(String bizpath : BIZDV) {
			if(src.getPath().contains(bizpath)) {
				return;
			}
		}
		BIZERR.add(src.getPath() + "\n");
	}
	
	private static void isContainTable(String str, Map<String, Object> map) {
		str = str.toUpperCase();
		for(String tablename : TABLELIST) {
			if(str.contains(tablename)) {
				if(isContainTableTarget(str, tablename)) {
					map.put(tablename, "ok");
				}
			}
		}
	}
	
	private static boolean isContainTableTarget(String str, String tablename) {
		if(str.indexOf("//") > -1 && (str.indexOf("//") < str.indexOf(tablename))) {
			return false;
		}else {
			return true;
		}
	}
	
	private static void isContainText(String str, Map<String, Object> map) {
		str = str.toUpperCase();
		for(String textname : TEXTLIST) {
			if(str.contains(textname)) {
				map.put(textname, "ok");
			}
		}
	}
	
	private static Map<String, Object> mapSort(Map<String, Object> map, String gubun) {
		TreeMap<String, Object> result = null;
		
		//gubun == 1 : 오름차순
		//gubun == 2 : 내림차순
		if("1".equals(gubun)) {
			result = new TreeMap<>(map);
		}else if("2".equals(gubun)) {
			result = new TreeMap<>(Collections.reverseOrder());
		}
		
		return result;
	}
	
	private static String printEachLine(String path, Map<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> itr = map.keySet().iterator();
		
		while(itr.hasNext()) {
			sb.append(path + "\t" + itr.next() + "\n");
		}
		
		return sb.toString();
	}
	
}
