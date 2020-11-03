package com.nepalese.virgosdk.Example.ParseXml2;

import java.io.File;

public class ParseXml {

	private static final String xmlFilePath = "E:\\ZSLWORK\\temp\\ShopStore.xml";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File(xmlFilePath);
		if(file.exists()) {
			ParseXmlFile<ShopStore> parser = new ParseXmlFile<ShopStore>(new ShopStore());
			ShopStore shopStore = (ShopStore) parser.parse(file, "UTF-8");
			
			System.out.println(shopStore.toBookString());
			System.out.println(shopStore.toFoodString());
			System.out.println(shopStore.toClothString());
		}else {
			System.out.println("文件不存在！");
		}
	}

}
