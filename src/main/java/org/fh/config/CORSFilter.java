package org.fh.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 说明：跨域访问处理
 * 作者：f-sci
 * 授权：bsic
 */
@Component
public class CORSFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.setContentType("textml;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "0");
		response.setHeader("Access-Control-Allow-Headers","Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
		response.setHeader("Access-Control-Allow-Credentials", "true"); // 是否支持cookie跨域        
		response.setHeader("XDomainRequestAllowed", "1");

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpStatus.NO_CONTENT.value());
			return;
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {

	}

	public static void main(String[] args) {
		int max=12,min=1;
		int ran2 = (int) (Math.random()*(max-min)+min);
		System.out.println(ran2);
		String shengxiao = "";
		switch (ran2){
			case 1:shengxiao="子鼠"; break;
			case 2:shengxiao="丑牛";break;
			case 3:shengxiao="寅虎";break;
			case 4:shengxiao="卯兔";break;
			case 5:shengxiao="辰龙";break;
			case 6:shengxiao="巳蛇";break;
			case 7:shengxiao="午马";break;
			case 8:shengxiao="未羊";break;
			case 9:shengxiao="申猴";break;
			case 10:shengxiao="酉鸡";break;
			case 11:shengxiao="戌狗";break;
			case 12:shengxiao="亥猪";break;
		};
		System.out.println("今日生肖卡为："+shengxiao);
	}
}
