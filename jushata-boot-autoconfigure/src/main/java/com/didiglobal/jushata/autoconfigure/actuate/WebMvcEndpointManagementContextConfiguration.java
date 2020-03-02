package com.didiglobal.jushata.autoconfigure.actuate;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMapper;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.web.servlet.ControllerEndpointHandlerMapping;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ManagementContextConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnBean({ DispatcherServlet.class, WebEndpointsSupplier.class })
@EnableConfigurationProperties(CorsEndpointProperties.class)
public class WebMvcEndpointManagementContextConfiguration {

    @Bean
    @ConditionalOnEnabledEndpoint
    public ModuleManagementEndpoint moduleManagementEndpoint() {
        return new ModuleManagementEndpoint();
    }

    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
                                            WebEndpointsSupplier webEndpointsSupplier,
                                            ServletEndpointsSupplier servletEndpointsSupplier,
                                            ControllerEndpointsSupplier controllerEndpointsSupplier,
                                            EndpointMediaTypes endpointMediaTypes,
                                            CorsEndpointProperties corsProperties,
                                            WebEndpointProperties webEndpointProperties) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        EndpointMapping endpointMapping = new EndpointMapping(webEndpointProperties.getBasePath());
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
            corsProperties.toCorsConfiguration(),
            new EndpointLinksResolver(allEndpoints, webEndpointProperties.getBasePath()));
    }

    @Bean
    public ControllerEndpointHandlerMapping controllerEndpointHandlerMapping(
                                                ControllerEndpointsSupplier controllerEndpointsSupplier,
                                                CorsEndpointProperties corsProperties,
                                                WebEndpointProperties webEndpointProperties) {
        EndpointMapping endpointMapping = new EndpointMapping(webEndpointProperties.getBasePath());
        return new ControllerEndpointHandlerMapping(endpointMapping, controllerEndpointsSupplier.getEndpoints(),
            corsProperties.toCorsConfiguration());
    }

    @Bean
    public WebEndpointDiscoverer webEndpointDiscoverer(
                                    ApplicationContext applicationContext,
                                    ParameterValueMapper parameterValueMapper,
                                    EndpointMediaTypes endpointMediaTypes,
                                    PathMapper webEndpointPathMapper,
                                    ObjectProvider<Collection<OperationInvokerAdvisor>> invokerAdvisors,
                                    ObjectProvider<Collection<EndpointFilter<ExposableWebEndpoint>>> filters) {
        return new WebEndpointDiscoverer(applicationContext, parameterValueMapper, endpointMediaTypes,
            webEndpointPathMapper, invokerAdvisors.getIfAvailable(Collections::emptyList),
            filters.getIfAvailable(Collections::emptyList));
    }
}