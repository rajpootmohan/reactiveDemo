package com.example.reactiveDemo.filters;

//import com.tiket.tix.common.monitor.StatsDClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.*;

// ClientHttpRequestInterceptor

@Component
@Order(1)
public class CustomFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomFilter.class);

//    @Autowired
//    private StatsDClientWrapper statsDClientWrapper;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Initializing filter CustomFilter :{}", this);
    }


    @Override
    public void destroy() {
        LOGGER.warn("Destructing filter CustomFilter :{}", this);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        LOGGER.info("entered in doFilter ....... ");
        HttpServletRequest req = (HttpServletRequest) request;
        LOGGER.info("Starting Transaction for req :{}", req.getRequestURI());
        chain.doFilter(request, response);
        LOGGER.info("Committing Transaction for req :{}", req.getRequestURI());
    }
}

