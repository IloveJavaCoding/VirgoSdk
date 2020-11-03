package com.nepalese.virgosdk.Example.ParseXml2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 将需解析的xml对象独立出来， 增强可用性， 适用于每条元素的标签唯一
 * @author Administrator
 * @param <T>
 */
public class ParseXmlFile<T> extends DefaultHandler {

    private T xml;//需解析的xml文件的对象

    public ParseXmlFile(T obj) {
    	xml = obj;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        invokeValue(xml, qName, attributes);
    }
    
    public T parse(File file, String encodeType) {
        if (file == null || !file.exists()) {
            System.out.println("文件不存在");
            return null;
        }
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            InputSource is = new InputSource(new InputStreamReader(input, encodeType));
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            parser.parse(is, this);
            input.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } 
        
        return xml;
    }

	
	@SuppressWarnings("hiding")
	private <T> void invokeValue(T obj, String qName, Attributes attributes) {
        Class<?> clz = obj.getClass();
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field f : declaredFields) {
            XmlFiled xmlFiled = f.getAnnotation(XmlFiled.class);//获取属性上的@XmlFiled
            if (xmlFiled != null) {
                f.setAccessible(true);
                if ("class java.lang.String".equals(f.getGenericType().toString())) {// 字符串类型的才判断
                    String tag = xmlFiled.tag();
                    if (tag.equals(qName)) {
                        String fName = xmlFiled.attr();
                        if (fName.trim().length() == 0) {
                            fName = f.getName();
                        }
                        String value = attributes.getValue(fName);
                        try {
                            // ：写入读取到的xml属性值.
                            f.set(obj, value);
                        } catch (Throwable e) {
                        	e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}