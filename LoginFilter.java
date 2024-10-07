package com.gzu.filtertest;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*") // 应用于所有URL路径
public class LoginFilter implements Filter {

    // 不需要登录就能访问的路径列表
    private static final List<String> EXCLUDED_PATHS = Arrays.asList("/login", "/register", "/public");

    // 记住我 Cookie 名称
    private static final String REMEMBER_ME_COOKIE = "remember-me";
    // 模拟用户登录的令牌
    private static final String REMEMBER_ME_TOKEN = "remember-me-token";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化逻辑
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request,
                         jakarta.servlet.ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取当前请求的路径
        String path = httpRequest.getRequestURI();

        // 检查当前请求路径是否在排除列表中
        if (isExcludedPath(path)) {
            chain.doFilter(request, response); // 如果是排除路径，继续请求
            return;
        }

        // 获取当前会话并检查用户是否已登录
        HttpSession session = httpRequest.getSession(false); // 不创建新会话
        if (session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response); // 用户已登录，继续处理请求
        } else {
            // 检查 "记住我" Cookie 是否存在
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (REMEMBER_ME_COOKIE.equals(cookie.getName()) && REMEMBER_ME_TOKEN.equals(cookie.getValue())) {
                        // 如果找到有效的记住我 Cookie，则创建用户会话
                        HttpSession newSession = httpRequest.getSession(true);
                        newSession.setAttribute("user", "auto-logged-in-user");
                        chain.doFilter(request, response); // 自动登录，继续处理请求
                        return;
                    }
                }
            }

            // 如果没有登录或记住我 Cookie，重定向到登录页面
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // 销毁时的清理逻辑（可选）
    }

    // 检查路径是否在排除列表中
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
