package com.gzu.listenerdemo2;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class RequestLogginListener implements ServletRequestListener {
    private static final Logger logger = Logger.getLogger(RequestLogginListener.class.getName());

    //记录请求开始时间
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        Instant startTime = Instant.now();
        request.setAttribute("startTime", startTime);//保存开始时间

        String clientIP = request.getRemoteAddr();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString() != null ? request.getQueryString() : "N/A";
        String userAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "N/A";

        logger.info(String.format("Request started: Time=%s, IP=%s, Method+%s, URI=%s, User-Agent=%s",
                startTime, clientIP, method, requestURI, userAgent));
    }

    //记录请求结束时间，并计算处理时间
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        Instant endTime = Instant.now();
        Instant startTime = (Instant) request.getAttribute("startTime");
        Duration processingTime = Duration.between(startTime, endTime);

        String requestURI = request.getRequestURI();
        logger.info(String.format("Request finished: URI=%s, ProcessingTime=%d ms",
                requestURI, processingTime.toMillis()));
    }
}
