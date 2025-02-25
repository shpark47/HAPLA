package com.hapla.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/profiles/**") // /profiles/로 시작하는 요청만 매핑
				.addResourceLocations("file:///c:/profiles/");
		registry.addResourceHandler("/uploadFiles/**") // /uploadFiles/로 시작하는 요청만 매핑
				.addResourceLocations("file:///c:/uploadFiles/");
		registry.addResourceHandler("/static/**") // /static/으로 시작하는 요청만 매핑
				.addResourceLocations("classpath:/static/");
	}
}
