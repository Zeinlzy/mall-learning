package com.lzy.mall.tiny.component;

import cn.hutool.json.JSONUtil;
import com.lzy.mall.tiny.common.api.CommonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这代码是处理的是授权（权限不足）层面的失败，并且它只会在用户已经认证但权限不足时被调用
 * 当接口没有权限时，自定义的返回结果
 * 实现 Spring Security 的 AccessDeniedHandler 接口，用于处理授权失败的场景。
 * 当已认证用户访问没有足够权限的资源时，该类的 handle 方法会被调用。
 */
@Component
public class RestfulAccessDeniedHandler implements AccessDeniedHandler{

    /**
     * 当已认证用户访问没有足够权限的资源时，该方法会被调用。
     * 它负责向客户端发送一个表示权限不足的响应。
     *
     * @param request  导致授权失败的HTTP请求
     * @param response 用于发送响应的HTTP响应对象
     * @param e        捕获到的授权异常 (AccessDeniedException)
     * @throws IOException      如果在写入响应时发生I/O错误
     * @throws ServletException 如果发生Servlet相关的异常
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException, ServletException {
        // 设置响应的字符编码为UTF-8
        response.setCharacterEncoding("UTF-8");
        // 设置响应的内容类型为application/json
        response.setContentType("application/json");
        // 使用 CommonResult.forbidden 方法构建一个统一格式的权限不足响应对象
        // CommonResult.forbidden(e.getMessage()) 将使用 ResultCode.FORBIDDEN 的默认消息，
        // 并将 AccessDeniedException 的消息放在 data 字段中。
        CommonResult<?> forbiddenResult = CommonResult.forbidden(e.getMessage());
        // 使用 Hutool 的 JSONUtil 将 CommonResult 对象序列化为JSON字符串
        String jsonResponse = JSONUtil.parse(forbiddenResult).toString();
        // 将JSON字符串写入响应体
        response.getWriter().println(jsonResponse);
        // 刷新输出流
        response.getWriter().flush();
        // 授权处理完成后，请求处理通常在此处终止，不继续过滤链。
    }
}