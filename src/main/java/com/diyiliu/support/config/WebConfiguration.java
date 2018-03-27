package com.diyiliu.support.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Description: WebConfiguration
 * Author: DIYILIU
 * Update: 2018-03-26 16:00
 */

@Configuration
public class WebConfiguration {


    @Bean
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public JedisConnectionFactory jedisFactory(){
        JedisConnectionFactory jedisFactory = new JedisConnectionFactory();
        jedisFactory.setUsePool(true);

        return jedisFactory;
    }
}
