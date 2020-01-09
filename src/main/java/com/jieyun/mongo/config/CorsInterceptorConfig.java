package com.jieyun.mongo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API跨域全局配置
 *
 * @author wanghaocun
 * @date 2018-11-13
 */
@Configuration
public class CorsInterceptorConfig implements WebMvcConfigurer {

    /**
     * 配置拦截器
     *
     * @param registry 跨域注册信息
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名（允许访问的客户端域名）
                .allowedOrigins("*")
                // 是否允许证书（是否允许请求带有验证信息）
                .allowCredentials(true)
                // 设置允许的方法（允许访问的方法名,GET POST等）
                .allowedMethods("*")
                // 设置允许的请求头（允许服务端访问的客户端请求头）
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600L);
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        @SuppressWarnings("unchecked")
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

}
