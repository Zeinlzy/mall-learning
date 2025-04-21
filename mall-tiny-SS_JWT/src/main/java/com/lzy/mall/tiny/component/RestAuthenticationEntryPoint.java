package com.lzy.mall.tiny.component;

import cn.hutool.json.JSONUtil;
import com.lzy.mall.tiny.common.api.CommonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于处理 RESTful API 中未认证用户访问受保护资源时的认证入口点
 * 当未登录或token失效时，访问接口时自定义的返回结果
 * 实现 Spring Security 的 AuthenticationEntryPoint 接口，用于处理认证失败的场景。
 * 当未认证用户访问需要认证的资源时，该类的 commence 方法会被调用。
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 当未认证用户尝试访问受保护的资源时，该方法会被调用。
     * 它负责向客户端发送一个表示认证失败的响应。
     *
     * @param request       导致认证失败的HTTP请求
     * @param response      用于发送响应的HTTP响应对象
     * @param authException 捕获到的认证异常
     * @throws IOException      如果在写入响应时发生I/O错误
     * @throws ServletException 如果发生Servlet相关的异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // 设置响应的字符编码为UTF-8，确保中文字符正常显示
        response.setCharacterEncoding("UTF-8");

        //// 设置响应的内容类型为application/json，告知客户端响应体是JSON格式
        response.setContentType("application/json");

        // 使用 CommonResult.unauthorized 方法构建一个统一格式的未认证响应对象
        // authException.getMessage() 通常包含认证失败的具体原因，如 "Full authentication is required to access this resource"
        CommonResult<?> unauthorizedResult = CommonResult.unauthorized(authException.getMessage());

        // 使用 Hutool 的 JSONUtil 将 CommonResult 对象序列化为JSON字符串
        String jsonResponse = JSONUtil.parse(unauthorizedResult).toString();

        // 将JSON字符串写入响应体
        response.getWriter().println(jsonResponse);

        // 刷新输出流，确保数据被发送到客户端
        response.getWriter().flush();
    }
}
