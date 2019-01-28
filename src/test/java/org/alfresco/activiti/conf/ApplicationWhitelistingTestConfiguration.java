/**
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
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
		"com.activiti.rest", // Extra for testing: the dispatcher servlet is not found by default
		"com.activiti.api", // Extra for testing: the dispatcher servlet is not found by default
		},
        excludeFilters= {
			@ComponentScan.Filter(value = ApplicationConfiguration.class, type = FilterType.ASSIGNABLE_TYPE), // Do not import main config, it will override the properties with the default property values!
			@ComponentScan.Filter(value = Bootstrapper.class, type = FilterType.ASSIGNABLE_TYPE)
		} 
) 
public class ApplicationWhitelistingTestConfiguration {
	
	/**
	 * This is needed to make property resolving work on annotations ...
	 * (see http://stackoverflow.com/questions/11925952/custom-spring-property-source-does-not-resolve-placeholders-in-value) 
	 */
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
}