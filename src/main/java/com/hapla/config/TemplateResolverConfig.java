package com.hapla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateResolverConfig {
	
	@Bean
	public ClassLoaderTemplateResolver commResolver() {
		ClassLoaderTemplateResolver commResolver = new ClassLoaderTemplateResolver();
		commResolver.setPrefix("templates/views/Community/");
		commResolver.setSuffix(".html");
		commResolver.setTemplateMode(TemplateMode.HTML);
		commResolver.setCharacterEncoding("UTF-8");
		commResolver.setCacheable(false);
		commResolver.setCheckExistence(true);
		return commResolver;
	}
	
	@Bean
	public ClassLoaderTemplateResolver reviewResolver() {
		ClassLoaderTemplateResolver reviewResolver = new ClassLoaderTemplateResolver();
		reviewResolver.setPrefix("templates/views/Community/");
		reviewResolver.setSuffix(".html");
		reviewResolver.setTemplateMode(TemplateMode.HTML);
		reviewResolver.setCharacterEncoding("UTF-8");
		reviewResolver.setCacheable(false);
		reviewResolver.setCheckExistence(true);
		return reviewResolver;
	}
}
