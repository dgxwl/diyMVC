package com.example.demo.common;

import java.lang.reflect.Method;

/**
 * 封装反射时某个controller对象和它被调用的某个方法
 * 
 * @author Administrator
 *
 */
public class Handler {
	private Object controller;
	private Method method;

	public Handler(Object controller, Method method) {
		this.controller = controller;
		this.method = method;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "Handler [controller=" + controller.getClass() + ", method=" + method + "]";
	}

}
