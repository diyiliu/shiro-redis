package com.diyiliu.support.config;

import com.diyiliu.support.config.properties.ShiroProperties;
import com.diyiliu.support.shiro.FormLoginFilter;
import com.diyiliu.support.shiro.RedisSessionDao;
import com.diyiliu.support.shiro.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: ShiroConfiguration
 * Author: DIYILIU
 * Update: 2018-03-26 16:00
 */

@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroConfiguration {

    @Autowired
    private ShiroProperties shiroProperties;

    /**
     * shiro过滤器
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl(shiroProperties.getLoginUrl());

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", formAuthenticationFilter());
        factoryBean.setFilters(filters);

        factoryBean.setFilterChainDefinitionMap(shiroProperties.getFilterChainDefinitions());

        return factoryBean;
    }

    /**
     * 安全管理器
     * @param userRealm
     * @param sessionManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm,
                                                     DefaultWebSessionManager sessionManager) {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);

        return securityManager;
    }

    /**
     * 会话管理器
     * @param sessionDao
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager(RedisSessionDao sessionDao) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setDeleteInvalidSessions(true);

        // 会话cookie
        Cookie cookie = new SimpleCookie();
        cookie.setName("shareCookie");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        sessionManager.setSessionIdCookie(cookie);

        // 自定义sessionDAO
        sessionManager.setSessionDAO(sessionDao);
        sessionDao.setSessionIdGenerator(new JavaUuidSessionIdGenerator());

        return sessionManager;
    }

    /**
     * redisSessionDao
     *
     * @return
     */
    @Bean
    public RedisSessionDao sessionDao(){


        return  new RedisSessionDao();
    }


    /**
     * 表单身份验证过滤器
     *
     * @return
     */
    @Bean
    public FormLoginFilter formAuthenticationFilter() {
        FormLoginFilter formLoginFilter = new FormLoginFilter();
        formLoginFilter.setLoginUrl(shiroProperties.getLoginUrl());
        formLoginFilter.setSuccessUrl(shiroProperties.getSuccessUrl());
        formLoginFilter.setUsernameParam(shiroProperties.getUsernameParam());
        formLoginFilter.setPasswordParam(shiroProperties.getPasswordParam());
        formLoginFilter.setRememberMeParam(shiroProperties.getRememberMeParam());

        return formLoginFilter;
    }

    /**
     * realm实现
     *
     * @return
     */
    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName(shiroProperties.getHashAlgorithmName());
        matcher.setHashIterations(shiroProperties.getHashIterations());
        userRealm.setCredentialsMatcher(matcher);

        return userRealm;
    }
}
