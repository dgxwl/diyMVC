package com.example.demo.common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.annotation.RequestMapping;

/**
 * 映射处理器
 * 建立请求url与对应处理类及处理类方法的对应关系
 * @author Administrator
 *
 */
public class HandlerMapping {
	//key:请求路径, value:处理类及处理类方法
	private Map<String, Handler> handlerMap = new HashMap<>();
	
	public void process(List<Object> controllers) {
		for (Object con : controllers) {
			//获得controller的class
			Class<?> clazz = con.getClass();
			//获得所有方法
			Method[] methods = clazz.getDeclaredMethods();
			//查找带有@RequestMapping的方法(是处理请求的方法)
			for (Method mh : methods) {
				//获取方法上的注解
				RequestMapping rm = mh.getAnnotation(RequestMapping.class);
				if (rm == null) {
					continue;
				}
				String path = rm.value();
				handlerMap.put(path, new Handler(con, mh));
			}
		}
		System.err.println(handlerMap);
	}
	
	public Handler getHandler(String path) {
		return handlerMap.get(path);
	}
}
