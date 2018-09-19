package com.example.demo.common;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图解析类
 * 最终处理并响应请求
 * @author Administrator
 *
 */
public class ViewResolver {

	public void process(Object vname, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String viewName = vname.toString();
		String contextPath = request.getContextPath();
		//视图名是否以"redirect:"开头, 是则重定向, 否则转发
		if (viewName.trim().startsWith("redirect:")) {
			response.sendRedirect(contextPath + viewName.trim().substring("redirect:".length()));
		} else {
			request.getRequestDispatcher("/web/" + viewName + ".jsp").forward(request, response);
		}
		
	}
}
