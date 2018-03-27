package com.diyiliu.support.config;

import com.diyiliu.support.cache.RedisCacheManager;
import com.diyiliu.support.config.properties.ShiroProperties;
import com.diyiliu.support.redis.RedisSessionDao;
import com.diyiliu.support.shiro.FormLoginFilter;
import com.diyiliu.support.shiro.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: ShiroConfig
 * Author: DIYILIU
 * Update: 2018-03-26 16:00
 */

@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroConfig {

    @Autowired
    private ShiroProperties shiroProperties;

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


    /**
     * redisSessionDao
     *
     * @return
     */
    @Bean
    public RedisSessionDao redisSessionDao() {


        return new RedisSessionDao();
    }

    /**
     * redisCacheManager
     *
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager() {

        return new RedisCacheManager();
    }

    /**
     * 会话管理器
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // session 过期时间
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        // 会话cookie
        /*
        Cookie cookie = new SimpleCookie();
        cookie.setName("shareCookie");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        sessionManager.setSessionIdCookie(cookie);
        */

        // redis
        sessionManager.setSessionDAO(redisSessionDao());
        sessionManager.setCacheManager(redisCacheManager());

        return sessionManager;
    }

    /**
     * 安全管理器
     *
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(redisCacheManager());

        return securityManager;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);

        return autoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor attributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        attributeSourceAdvisor.setSecurityManager(securityManager());

        return attributeSourceAdvisor;
    }

    /**
     * shiro过滤器
     *
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager());
        factoryBean.setLoginUrl(shiroProperties.getLoginUrl());
        factoryBean.setSuccessUrl(shiroProperties.getSuccessUrl());

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", formAuthenticationFilter());
        factoryBean.setFilters(filters);

        factoryBean.setFilterChainDefinitionMap(shiroProperties.getFilterChainDefinitions());

        return factoryBean;
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
}
