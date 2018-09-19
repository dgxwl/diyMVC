package com.example.demo.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.example.demo.common.Handler;
import com.example.demo.common.HandlerMapping;
import com.example.demo.common.ViewResolver;

/**
 * 请求分发器
 * @author Administrator
 *
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private HandlerMapping hm;
	private ViewResolver vr;
	
	@Override
	public void init() throws ServletException {
		try {
			//读取web.xml配置的配置文件名和位置
			String configFileName = getServletConfig().getInitParameter("config");
			InputStream in = getClass().getClassLoader().getResourceAsStream(configFileName);
			//解析配置文件: 读取bean节点
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> beanNodes = root.elements();
			//解析配置文件: 将类实例化后存入list
			List<Object> controllers = new ArrayList<>(beanNodes.size());
			for (Element e : beanNodes) {
				String className = e.attributeValue("class");
				Object controller = Class.forName(className).newInstance();
				controllers.add(controller);
			}
			//将controllerlist交给handlermapping处理得到路径与处理方法的映射
			hm = new HandlerMapping();
			hm.process(controllers);
			
			//实例化视图解析器
			vr = new ViewResolver();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得完整请求资源路径
		String uri = request.getRequestURI();
		//获得项目根目录
		String contextPath = request.getContextPath();
		//截取出请求对应处理器的路径
		String path = uri.substring(contextPath.length());
		//根据路径获得对应处理器及方法
		Handler handler = hm.getHandler(path);
		//如果请求路径没有对应处理方法, 返回404
		if (handler == null) {
			response.sendError(404);
			return ;
		}
		Object controller = handler.getController();
		Method method = handler.getMethod();
		//视图名, 即调用方法后的返回值
		Object viewName = null;
		//获取该方法的所有参数的类型
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		try {
			if (parameterTypes.length > 0) {  //方法有参
				Object[] args = new Object[parameterTypes.length];
				//遍历参数列表, 如果含有request, response, session则将他们赋值给参数
				for (int i = 0; i < parameterTypes.length; i++) {
					if (parameterTypes[i] == HttpServletRequest.class) {
						args[i] = request;
					}
					if (parameterTypes[i] == HttpServletResponse.class) {
						args[i] = response;
					}
					if (parameterTypes[i] == HttpSession.class) {
						args[i] = request.getSession();
					}
				}
				viewName = method.invoke(controller, args);
			} else {  //方法无参
				viewName = method.invoke(controller);
			}
			vr.process(viewName, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}
