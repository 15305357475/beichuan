package org.fh.controller.act.editor;

import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.flowable.ui.modeler.servlet.ApiDispatcherServletConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 说明：flowable.ui流程编辑器配置
 * 作者：fsci
 * 授权：bsic
 */
@Configuration
@EnableConfigurationProperties(FlowableModelerAppProperties.class)
@ComponentScan(basePackages = {
"org.flowable.ui.modeler.repository", "org.flowable.ui.modeler.service",
"org.flowable.ui.common.service", "org.flowable.ui.common.repository",
"org.flowable.ui.common.tenant" }, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = RemoteIdmService.class), })// 移除 RemoteIdmService
public class ApplicationConfiguration {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletRegistrationBean modelerApiServlet(ApplicationContext applicationContext) {
		AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
		dispatcherServletConfiguration.setParent(applicationContext);
		dispatcherServletConfiguration.register(ApiDispatcherServletConfiguration.class);
		DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, "/api/*");
		registrationBean.setName("Flowable Modeler App API Servlet");
		registrationBean.setLoadOnStartup(1);
		registrationBean.setAsyncSupported(true);
		return registrationBean;
	}
	
}
