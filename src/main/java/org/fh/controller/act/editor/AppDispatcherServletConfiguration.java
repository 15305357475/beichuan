package org.fh.controller.act.editor;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.flowable.ui.modeler.rest.app.EditorGroupsResource;
import org.flowable.ui.modeler.rest.app.EditorUsersResource;
import org.flowable.ui.modeler.rest.app.StencilSetResource;

/**
 * 说明：flowable.ui流程编辑器配置
 * 作者：fsci
 * 授权：bsic
 */
@Configuration
@ComponentScan(value = { "org.flowable.ui.modeler.rest.app", }, excludeFilters = {
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EditorUsersResource.class),
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EditorGroupsResource.class),
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = StencilSetResource.class), })
@EnableAsync
public class AppDispatcherServletConfiguration implements WebMvcRegistrations {

	@Bean
	public SessionLocaleResolver localeResolver() {
		return new SessionLocaleResolver();
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		return localeChangeInterceptor;
	}

	@Override
	public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
	    RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
	    requestMappingHandlerMapping.setRemoveSemicolonContent(false);
	    Object[] interceptors = { localeChangeInterceptor() };
	    requestMappingHandlerMapping.setInterceptors(interceptors);
	    return requestMappingHandlerMapping;
	}
	
}