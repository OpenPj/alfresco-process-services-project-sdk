package org.alfresco.activiti.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.activiti.conf.ApplicationConfiguration;
import com.activiti.conf.Bootstrapper;

@Configuration
@PropertySources({
    @PropertySource(value="classpath:/META-INF/activiti-app-test/TEST-db.properties", ignoreResourceNotFound=false),
    @PropertySource(value="classpath:/META-INF/activiti-app-test/TEST-activiti-app-whitelisting.properties", ignoreResourceNotFound=false),
    @PropertySource(value="classpath:/META-INF/activiti-app-test/TEST-activiti-ldap.properties", ignoreResourceNotFound=false)
})
@ComponentScan(basePackages = {
        "com.activiti.conf",
        "com.activiti.repository",
        "com.activiti.service",
        "com.activiti.repo",
        "com.activiti.security",
        "com.activiti.model.component",
        "com.activiti.runtime.bean",
        "com.activiti.runtime.activiti.bean",
		"com.activiti.reporting",
		"com.activiti.rest",
		"com.activiti.api",
		},
        excludeFilters= {
			@ComponentScan.Filter(value = ApplicationConfiguration.class, type = FilterType.ASSIGNABLE_TYPE),
			@ComponentScan.Filter(value = Bootstrapper.class, type = FilterType.ASSIGNABLE_TYPE)
		} 
) 
public class ApplicationWhitelistingTestConfiguration {
	
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
}