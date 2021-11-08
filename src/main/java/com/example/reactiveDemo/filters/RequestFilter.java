//package com.example.reactiveDemo.filters;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.method.HandlerMethod;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.UUID;
//@Order(1)
//@Component
//public class RequestFilter implements Filter {
//    private static Logger log = LoggerFactory.getLogger(RequestFilter.class);
//
//    @Autowired
//    private DataDogClient dataDogClient;
//
//    @Qualifier("requestMappingHandlerMapping")
//    @Autowired
//    private RequestMappingHandlerMapping mapping;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        long startTime = System.currentTimeMillis();
//        HttpServletRequest httpReq = (HttpServletRequest) request;
//        HttpServletResponse httpResp = ((HttpServletResponse) response);
//        try {
//            String paytmRequestId = httpReq.getHeader(Constants.SERVICE_REQUEST_ID);
//            String appRequestId = httpReq.getHeader(Constants.APP_REQUEST_ID);
//            if (StringUtils.isEmpty(paytmRequestId)) {
//                paytmRequestId = UUID.randomUUID().toString();
//            }
//            if (StringUtils.isEmpty(appRequestId)) {
//                appRequestId = UUID.randomUUID().toString().toUpperCase().replace("-", "");
//            }
//            MDC.put(Constants.SERVICE_REQUEST_ID, paytmRequestId);
//            MDC.put(Constants.APP_REQUEST_ID, appRequestId);
//            httpResp.addHeader(Constants.SERVICE_REQUEST_ID, paytmRequestId);
//            httpResp.addHeader(Constants.APP_REQUEST_ID, appRequestId);
//            chain.doFilter(request, response);
//        } finally {
//            long endTime = System.currentTimeMillis();
//            String executionEntity = getExecutionEntity(httpReq);
//            if (!StringUtils.isEmpty(executionEntity)) {
//                HttpStatus httpStatus = HttpStatus.resolve(httpResp.getStatus());
//                dataDogClient.recordResponseCodeCount(Monitor.ServiceGroup.API_IN.name(), httpStatus, executionEntity);
//            }
//            String path = httpReq.getPathInfo() != null ? httpReq.getPathInfo().toString() : null;
//            if (path == null && httpReq.getRequestURI() != null && httpReq.getContextPath() != null) {
//                path = httpReq.getRequestURI().substring(httpReq.getContextPath().length());
//            }
//            log.info("Method:{} Path:{} TimeConsumed:{} ms", httpReq.getMethod(), path, (endTime - startTime));
//            MDC.clear();
//        }
//    }
//    private String getExecutionEntity(HttpServletRequest request) {
//        try {
//            Object reqHandler =
//                    Optional.ofNullable(mapping.getHandler(request)).map(chain -> chain.getHandler()).orElse(null);
//            if (Objects.isNull(reqHandler) || !(reqHandler instanceof HandlerMethod)) {
//                return null;
//            }
//            HandlerMethod handler = (HandlerMethod) reqHandler;
//            Monitor monitor = handler.getMethodAnnotation(Monitor.class);
//            return Optional.ofNullable(monitor).map(Monitor::name).orElse(null);
//        } catch (Exception e) {
//            log.error("Error while getting Execution Entity : ", e);
//        }
//        return null;
//    }
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//    @Override
//    public void destroy() {
//    }
//}
