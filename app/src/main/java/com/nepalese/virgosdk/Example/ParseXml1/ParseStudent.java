package com.nepalese.virgosdk.Example.ParseXml1;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 *  解析xml文件， 单类型（需与xml文件一一对应）， 1-n 条同类型元素,  可拓展至不同类型元素
 */
public class ParseStudent extends DefaultHandler {
	// 元素标签
	public static final String ELEMENT_STUDENT = "Student";

	private List<Student> mList;
	private Student mSource;

	public ParseStudent() {
	}

	/**
	 * @供外部调用接口
	 * @param file       xml 文件
	 * @param encodeType xml 声明中的编码类型
	 * @return Student 列表
	 */
	public List<Student> parse(File file, String encodeType) {
		if (file == null || !file.exists()) {
			System.out.println("文件不存在！");
			return null;
		}

		InputStream input = null;

		try {
			input = new FileInputStream(file);
			InputSource inputSource = new InputSource((new InputStreamReader(input, encodeType)));
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			parser.parse(inputSource, this);
			input.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return mList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 打开文件时 初始化 list
		mList = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// 按元素头 (头标签) 区分解析每一条元素
		switch (qName) {
		case ELEMENT_STUDENT:
			mSource = new Student();
			System.out.println("startElement: new source");

			if (attributes != null) {
				int size = attributes.getLength();
				for (int i = 0; i < size; i++) {
					mSource.setValue(attributes.getQName(i), attributes.getValue(i));
				}
			}
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// 按元素尾 (尾标签) 标识解析完一条元素 插入到 list
		switch (qName) {
		case ELEMENT_STUDENT:
			mList.add(mSource);
			System.out.println("endElement: add source, list size = " + mList.size());
			break;
		}
	}
}
