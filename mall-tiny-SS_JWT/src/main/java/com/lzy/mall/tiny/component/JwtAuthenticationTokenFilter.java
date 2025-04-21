package com.lzy.mall.tiny.component;

import com.lzy.mall.tiny.common.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;// JWT存储的请求头名称，默认为Authorization
    @Value("${jwt.tokenHead}")
    private String tokenHead;// JWT令牌内容的前缀，默认为'Bearer '


    /**
     * JWT认证过滤器。
     * 拦截所有HTTP请求，解析并验证请求头中的JWT令牌。
     * 如果令牌有效且未过期，则加载用户详细信息，并将认证信息设置到Spring Security的SecurityContext中，
     * 以便后续的授权机制能够基于此信息进行权限判断。
     * 该过滤器继承自OncePerRequestFilter，确保每个请求只执行一次。
     */
    /**
     * JwtAuthenticationTokenFilter 在 Spring Security 的安全过滤链 (Security Filter Chain) 中执行
     * doFilterInternal:拦截请求,对于每一个到来的 HTTP 请求，它都会被执行一次
     * @param request: HTTP 请求对象，用于获取请求头、参数等信息
     * @param response：HTTP 响应对象，可修改响应状态或内容
     * @param chain：过滤器链，用于将请求传递给下一个过滤器或目标资源
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        //从请求头中获取指定名称的令牌头（通常为 Authorization）
        String authHeader = request.getHeader(this.tokenHeader);

        //检查令牌头是否存在且以指定前缀（如 Bearer ）开头
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            //移除令牌头前缀（如 Bearer ），获取原始 JWT 字符串
            String authToken = authHeader.substring(this.tokenHead.length());
            //从 JWT 中解析用户名
            String username = jwtTokenUtil.extractUsername(authToken);
            LOGGER.info("checking username:{}", username);

            /**
             * 用户名有效（非空）且 SecurityContext 中尚未存储认证信息（避免重复认证）。
             * 目的：仅当用户未认证时才进行后续操作，提升性能并防止覆盖现有认证。
             */
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //根据用户名从数据库或其他存储加载用户详细信息（如权限、密码等）
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                //验证 JWT 有效性
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    /**
                     * 参数解析：
                     * principal：用户身份（UserDetails）。
                     * credentials：密码（JWT 无需密码，设为 null）。
                     * authorities：用户的权限列表（从 UserDetails 获取）。
                     * 作用：构建一个已认证的 Authentication 对象，表示用户已成功登录。
                     */
                    //创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    //将请求的 IP 地址、Session ID 等附加到认证对象中，用于后续审计或日志
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    LOGGER.info("authenticated user:{}", username);
                    //将认证对象存储到 SecurityContext 中，供后续过滤器或控制器使用
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        //继续执行过滤器链
        chain.doFilter(request, response);
    }
}
