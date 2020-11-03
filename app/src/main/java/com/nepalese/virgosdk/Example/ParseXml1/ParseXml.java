package com.nepalese.virgosdk.Example.ParseXml1;

import java.io.File;
import java.util.List;

public class ParseXml {

	private static final String xmlFilePath = "E:\\ZSLWORK\\temp\\Students.xml";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File(xmlFilePath);
		if(file.exists()) {
			ParseStudent parser = new ParseStudent();
			List<Student> list= parser.parse(file, "GBK");
			for (Student source : list ){
                System.out.println(source.toString());
            }
		}else {
			System.out.println("文件不存在！");
		}
	}
}
