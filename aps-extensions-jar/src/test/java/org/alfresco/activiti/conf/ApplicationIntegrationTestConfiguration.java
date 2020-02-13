package org.alfresco.activiti.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.activiti.conf.ApplicationConfiguration;
import com.activiti.conf.Bootstrapper;
import com.activiti.conf.LicenseConfiguration;
import com.activiti.rest.service.api.version.AppVersionResource;

@Configuration
@PropertySources({
    @PropertySource(value="classpath:activiti-integrationtests.properties", ignoreResourceNotFound=false)
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
    excludeFilters = {
            @ComponentScan.Filter(value = ApplicationConfiguration.class, type = FilterType.ASSIGNABLE_TYPE),
            @ComponentScan.Filter(value = LicenseConfiguration.class, type = FilterType.ASSIGNABLE_TYPE),
            @ComponentScan.Filter(value = Bootstrapper.class, type = FilterType.ASSIGNABLE_TYPE),
            @ComponentScan.Filter(value = AppVersionResource.class, type = FilterType.ASSIGNABLE_TYPE)
    }
) 
public class ApplicationIntegrationTestConfiguration {

}
