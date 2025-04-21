package com.lzy.mall.tiny.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.lzy.mall.tiny.common.utils.JwtTokenUtil;
import com.lzy.mall.tiny.domain.AdminUserDetails;
import com.lzy.mall.tiny.service.UmsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;


@Slf4j
@Service
public class UmsAdminServiceImpl implements UmsAdminService, UserDetailsService{
    /**
     * 存放默认用户信息
     */
    private List<AdminUserDetails> adminUserDetailsList = new ArrayList<>();
    private JwtTokenUtil jwtTokenUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UmsAdminServiceImpl(JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void init(){
        adminUserDetailsList.add(AdminUserDetails.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .authorityList(CollUtil.toList("ROLE_ADMIN"))
                .build());
        adminUserDetailsList.add(AdminUserDetails.builder()
                .username("macro")
                .password(passwordEncoder.encode("123456"))
                .authorityList(CollUtil.toList("ROLE_USER"))
                .build());
    }
    @Override
    public AdminUserDetails getAdminByUsername(String username) {
        List<AdminUserDetails> findList = adminUserDetailsList.stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(findList)){
            return findList.get(0);
        }
        return null;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            /*
            * 获取用户信息: 方法首先尝试调用 getAdminByUsername(username) 方法。
            * 在这个示例项目中，该方法是从内存列表 adminUserDetailsList 中查找匹配的用户信息（AdminUserDetails 对象）。（注意：在真实项目中，这里通常会查询数据库）。
            * */
            UserDetails userDetails = getAdminByUsername(username);
            if(userDetails==null){
                return token;
            }
            //原始密码 (password) 和存储的加密密码 (userDetails.getPassword())
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            //创建一个临时的 Spring Security 认证对象,这个对象包含了用户详情 (userDetails) 和用户的权限信息。
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            //设置安全上下文:将上一步创建的认证对象存入SecurityContext，即当前线程的安全上下文中。Spring Security会基于此对象判断用户的登录状态和权限
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUserDetails admin = getAdminByUsername(username);
        if (admin != null) {
            return admin;
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }
}
