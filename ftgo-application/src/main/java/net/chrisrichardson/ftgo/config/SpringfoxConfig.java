package net.chrisrichardson.ftgo.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Workaround for Springfox 2.x incompatibility with Spring Boot 2.6+.
 *
 * Spring Boot 2.6+ introduced PathPatternParser-based handler mappings alongside
 * the existing AntPathMatcher ones. Springfox 2.x assumes all handler mappings use
 * PatternsRequestCondition (AntPathMatcher), and throws NPE when it encounters
 * handlers using PathPatternsRequestCondition (PathPatternParser).
 *
 * This BeanPostProcessor filters out PathPatternParser-based handler mappings from
 * Springfox's WebMvcRequestHandlerProvider so it only processes compatible handlers.
 */
@Configuration
public class SpringfoxConfig {

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean.getClass().getName().equals(
                        "springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider")) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(
                    List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
