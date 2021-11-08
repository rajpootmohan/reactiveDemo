//package com.example.reactiveDemo.configuration;
//
//import com.example.reactiveDemo.filters.CustomOncePerRequestFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterRegistrationConfig {
//
//    @Bean
//    public FilterRegistrationBean<CustomOncePerRequestFilter> customOncePerRequestFilterFilterRegistrationBean() {
//        FilterRegistrationBean<CustomOncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new CustomOncePerRequestFilter());
//        registrationBean.addUrlPatterns("*");
//        return registrationBean;
//    }
//}
