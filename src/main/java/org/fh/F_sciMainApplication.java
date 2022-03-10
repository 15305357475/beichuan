package org.fh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 说明：启动类
 * 作者：f-sci
 * 授权：bsic
 * 
 */
@MapperScan("org.fh.mapper")
@EnableCaching
@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class F_sciMainApplication {
	public static void main(String[] args) {
		SpringApplication.run(F_sciMainApplication.class, args);
	}
}
