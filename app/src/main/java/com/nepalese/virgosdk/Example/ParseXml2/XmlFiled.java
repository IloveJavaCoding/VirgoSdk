package com.nepalese.virgosdk.Example.ParseXml2;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlFiled {
	//{@link XmlFiled#value()}.
	
	String tag() default "";//  元素标签
    String attr() default  "";// 元素属性
}
