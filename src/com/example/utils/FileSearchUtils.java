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
 * ���Ͽ��� �ش� �������� ã��
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
	
	private static int uicnt = 0; //ȭ����
	private static int cmpcnt = 0; //������Ʈ���
	private static int srvcnt = 0; //���񽺸��
	private static int pmthdcnt = 0; //�ۺ��޼�����
	
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
		{"IDDIM001", "ǥ���׸����"}
		, {"IDDIM002", "������������������"}
		, {"IDDIM003", "������-ǥ���׸����"}
		, {"IDDIM004", "���������Ͼ��ε�"}
	};
	
	private final static String[] ELENAMES = {"Div"
			, "Button"
			, "PopupDiv"
			, "Combo"
	};
	
	public static void main(String[] args) throws Exception {
		File root = null;
		
		/*************************** ��� ���� ���� ***************************/
		//ȭ��
//		root = new File("C:\\workspace\\...");
//		getErr(root);
		
		//ȭ�鼭��
		root = new File(WORKSPACEROOT + PROJECTNM + "\\src\\main\\java");
		getErr(root);
		
		//ȭ������
//		root = new File(WORKSPACEROOT + PROJECTNM + "\\src\\main\\java");
//		getErr(root);
		/*************************** ��� ���� ���� ***************************/
		
		/*************************** ��� ���� ***************************/
		//�������� ��� �ƴ�
		if(!BIZERR.isEmpty()) {
//			System.out.println("�������а�� ����(" + (int)Math.floor((double)BIZERR.size()/(uicnt+srvcnt)*100) + "%)\n" + BIZERR);
		}
		
		//���ϸ�.formid �ٸ�
		if(!FORMIDERR.isEmpty()){
//			System.out.println("\n\n���ϸ�.formid ����(" + (int)Math.floor((double)FORMIDERR.size()/uicnt*100) + "%)\n" + FORMIDERR);
		}
		
		//��Ÿ��Ʋ �ٸ�
		if(!TITLEERR.isEmpty()) {
//			System.out.println("\n\nUI��Ÿ�̺� ����(" + (int)Math.floor((double)TITLEERR.size()/UIARRS.length*100) + "%\n" + TITLEERR);
		}
		
		//Ŭ��������Ģ
		if(!CLASSNMERR.isEmpty()) {
//			System.out.println("\n\nŬ��������Ģ ����(" + (int)Math.floor((double)CLASSNMERR.size()/srvcnt*100) + "%)\n" + CLASSNMERR);
		}
		
		//system.out ���
		if(!SYSOUTERR.isEmpty()) {
//			System.out.println("\n\nsystem.out ���(" + (int)Math.floor((double)SYSOUTERR.size()/srvcnt*100) + "%\n" + SYSOUTERR);
		}
		
		//TABLE ���
		if(!CONTAINTABLE.isEmpty()) {
//			System.out.println("\n\nTABLE ���(" + CONTAINTABLE);
		}
		
		//TEXT ���
		if(!CONTAINTEXT.isEmpty()) {
//			System.out.println("\n\nTEXT ���(" + CONTAINTEXT);
		}
		
		//id��Ģ ����
		if(!DEFIDWARN.isEmpty()) {
//			System.out.println("\n\n������Ʈ id��Ģ ����(" + (int)Math.floor((double)DEFIDWARN.size()/cmpcnt*100) + "%\n" + DEFIDWARN);
		}
		
		//�޼������Ģ
		if(!MTHDNMWARN.isEmpty()) {
//			System.out.println("\n\n�޼����� ��Ģ ����(" + (int)Math.floor((double)MTHDNMWARN.size()/pmthdcnt*100) + "%\n" + MTHDNMWARN);
		}
		/*************************** ��� ���� ***************************/
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
					String title = "[" + uititle[0] + "]" + uititle[1].replaceAll("[(�˾�)]", "").replaceAll("\\s+", "");
					String titletext = attrs.getNamedItem("titletext").getNodeValue().replaceAll("\\s+", "");
					if(!title.equals(titletext)) {
						TITLEERR.add(src.getPath() + "//" + fileid + " : Ÿ��Ʋ=" + title + " - " + titletext + "\n");
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
		//Ŭ��������Ģ
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
		
		//gubun == 1 : ��������
		//gubun == 2 : ��������
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
