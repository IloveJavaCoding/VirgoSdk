package com.nepalese.virgosdk.Example.ParseXml2;

import java.util.ArrayList;
import java.util.List;

/**
 * 	<Book name = "Java" num = "324562139644" price = "30$"></Book>
 * 	<Food name = "枸杞子"  brand = "百花堂"  weight = "150g" price = "15￥"></Food>
 * 	<Cloth brand = "PlayBoy" type = "fleece" price = "99￥" ></Cloth>
 * @author Administrator
 */
public class ShopStore {
	public final List<String> qNameList = new ArrayList<>();

	//与目标xml文件内元素标签名一致
    {
        qNameList.add("Book");
        qNameList.add("Food");
        qNameList.add("Cloth");
    }
    
    //Book:
    @XmlFiled(tag = "Book", attr = "name")
    private String bName = "";
    @XmlFiled(tag = "Book", attr = "num")
    private String bNum = "";
    @XmlFiled(tag = "Book", attr = "price")
    private String bPrice = "";
    
    //Food
    @XmlFiled(tag = "Food", attr = "name")
    private String fName = "";
    @XmlFiled(tag = "Food", attr = "brand")
    private String fBrand = "";
    @XmlFiled(tag = "Food", attr = "weight")
    private String fWeight = "";
    @XmlFiled(tag = "Food", attr = "price")
    private String fPrice = "";
   
    //Cloth
    @XmlFiled(tag = "Cloth", attr = "brand")
    private String cBrand = "";
    @XmlFiled(tag = "Cloth", attr = "type")
    private String cType = "";
    @XmlFiled(tag = "Cloth", attr = "price")
    private String cPrice = "";
    
    
	public String getbName() {
		return bName;
	}
	public void setbName(String bName) {
		this.bName = bName;
	}
	public String getbNum() {
		return bNum;
	}
	public void setbNum(String bNum) {
		this.bNum = bNum;
	}
	public String getbPrice() {
		return bPrice;
	}
	public void setbPrice(String bPrice) {
		this.bPrice = bPrice;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getfBrand() {
		return fBrand;
	}
	public void setfBrand(String fBrand) {
		this.fBrand = fBrand;
	}
	public String getfWeight() {
		return fWeight;
	}
	public void setfWeight(String fWeight) {
		this.fWeight = fWeight;
	}
	public String getfPrice() {
		return fPrice;
	}
	public void setfPrice(String fPrice) {
		this.fPrice = fPrice;
	}
	public String getcBrand() {
		return cBrand;
	}
	public void setcBrand(String cBrand) {
		this.cBrand = cBrand;
	}
	public String getcType() {
		return cType;
	}
	public void setcType(String cType) {
		this.cType = cType;
	}
	public String getcPrice() {
		return cPrice;
	}
	public void setcPrice(String cPrice) {
		this.cPrice = cPrice;
	}
	
	public String toBookString() {
		return "Book [bName=" + bName + ", bNum=" + bNum + ", bPrice=" + bPrice + "]";
	}
	
	public String toFoodString() {
		return "Food [fName=" + fName + ", fBrand=" + fBrand + ", fWeight=" + fWeight + ", fPrice=" + fPrice + "]";
	}

	public String toClothString() {
		return "Cloth [cBrand=" + cBrand + ", cType=" + cType + ", cPrice=" + cPrice + "]";
	}
	
	@Override
	public String toString() {
		return "ShopStore [bName=" + bName + ", bNum=" + bNum + ", bPrice=" + bPrice + ", fName=" + fName + ", fBrand="
				+ fBrand + ", fWeight=" + fWeight + ", fPrice=" + fPrice + ", cBrand=" + cBrand + ", cType=" + cType
				+ ", cPrice=" + cPrice + "]";
	}
}
