//package com.example.reactiveDemo.filters;
//
//import com.tiket.tix.common.monitor.StatsDClientWrapper;
//import com.tiket.tix.common.monitor.aspects.Monitor;
//import com.tiket.tix.common.monitor.enums.CustomTag;
//import com.tiket.tix.common.monitor.enums.ErrorCode;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@Order(2)
//public class CustomOncePerRequestFilter extends OncePerRequestFilter {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOncePerRequestFilter.class);
//
//    @Autowired
//    private StatsDClientWrapper statsDClientWrapper;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws IOException, ServletException {
//        LOGGER.info("Starting Transaction for request CustomOncePerRequestFilter :{}", request.getRequestURI());
//        filterChain.doFilter(request, response);
//        long startTime = System.currentTimeMillis();
//        String metricName = request.getRequestURI();
//        ErrorCode errorCode = ErrorCode.SUCCEED;
//        HttpStatus httpStatus = HttpStatus.OK;
//        Map<CustomTag, String> customTags = new HashMap<>();
//        try {
//            filterChain.doFilter(request, response);
//            httpStatus = HttpStatus.valueOf(response.getStatus());
//            if(!httpStatus.is2xxSuccessful()) {
//                errorCode = ErrorCode.FAILED;
//            }
//        } catch(Exception e){
//            customTags.put(CustomTag.EXCEPTION_NAME, e.getClass().toString());
//            errorCode = ErrorCode.FAILED;
//            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            LOGGER.error("Error in datadog request filtering {} ", e);
//            throw e;
//        } finally {
//            statsDClientWrapper.monitor(metricName, Monitor.ServiceGroup.API_IN, errorCode, customTags,
//                    System.currentTimeMillis()-startTime, httpStatus);
//        }
//    }
//
//}
//
