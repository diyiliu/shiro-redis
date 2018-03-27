package com.diyiliu.support.shiro;

import com.diyiliu.support.redis.RedisManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Description: RedisSessionDao
 * Author: DIYILIU
 * Update: 2018-03-26 16:58
 */

public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private RedisManager redisManager;

    @Override
    public void delete(Session session) {
        if(session == null || session.getId() == null){
            System.out.println("Session is null");
            return;
        }
        redisManager.hdelete(session.getId().toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        List<Session> list = redisManager.hmget();

        return list;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if(session == null || session.getId() == null){
            System.out.println("Session is null");
            return;
        }
        Serializable sessionId = session.getId();
        redisManager.hadd(sessionId.toString(), session);
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        //添加进redis
        redisManager.hadd(sessionId.toString(), session);

        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return redisManager.hget(sessionId.toString());
    }
}