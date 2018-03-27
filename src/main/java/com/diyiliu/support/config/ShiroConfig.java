package com.diyiliu.support.config;

import com.diyiliu.support.cache.RedisCacheManager;
import com.diyiliu.support.config.properties.ShiroProperties;
import com.diyiliu.support.redis.RedisSessionDao;
import com.diyiliu.support.shiro.FormLoginFilter;
import com.diyiliu.support.shiro.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
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
     *
     * @param userRealm
     * @param sessionManager
     * @param redisCacheManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm,
                                                     SessionManager sessionManager,
                                                     RedisCacheManager redisCacheManager) {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);

        return securityManager;
    }

    /**
     * 会话管理器
     *
     * @param redisSessionDao
     * @param redisCacheManager
     * @return
     */
    @Bean
    public SessionManager sessionManager(RedisSessionDao redisSessionDao,
                                         RedisCacheManager redisCacheManager) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(1800);
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        // 会话cookie
        Cookie cookie = new SimpleCookie();
        cookie.setName("shareCookie");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        sessionManager.setSessionIdCookie(cookie);

        // redis
        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setCacheManager(redisCacheManager);

        return sessionManager;
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

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);

        return autoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor attributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        attributeSourceAdvisor.setSecurityManager(securityManager);

        return attributeSourceAdvisor;
    }
}
