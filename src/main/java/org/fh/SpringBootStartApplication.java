package org.fh;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 说明：项目以war包方式运行时用到
 * 作者：f-sci
 * 授权：bsic
 */
public class SpringBootStartApplication extends SpringBootServletInitializer {
 
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(F_sciMainApplication.class);  //这里要指向原先用main方法执行的F_sciMainApplication启动类
    }
}