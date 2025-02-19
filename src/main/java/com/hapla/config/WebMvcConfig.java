package com.hapla.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")		  			                              // 매핑할 uri 설정 (파일을 가지고 올 때 경로를 어떻게 쓸 지 결정)
				.addResourceLocations("file:///c:/uploadFiles/", "classpath:/static/", "file:///c:/profiles/");	  // 정적 리소스 위치 (자원이 있는 위치)
		
	}
}
