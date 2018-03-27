package com.diyiliu.support.redis;

import org.apache.shiro.session.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: RedisManager
 * Author: DIYILIU
 * Update: 2018-03-26 17:01
 */

@Component
public class RedisManager {

    @Resource
    private RedisTemplate<String, Session> redisTemplate;

    private static final String KEY = "shareSessionMap";

    public void hadd(String sessionId, byte[] bytes) {
        redisTemplate.boundHashOps(KEY).put(sessionId, bytes);
    }

    public void hadd(String sessionId, Session session) {
        redisTemplate.boundHashOps(KEY).put(sessionId, session);
    }

    public void hdelete(String sessionId) {
        redisTemplate.boundHashOps(KEY).delete(sessionId);
    }

    public Session hget(String sessionId) {
        return (Session) redisTemplate.boundHashOps(KEY).get(sessionId);
    }

    public List<Session> hmget() {
        List<Session> list = new ArrayList<>();

        List<Object> values = redisTemplate.boundHashOps(KEY).values();
        for (Object object : values) {
            list.add((Session) object);
        }
        return list;
    }
}
