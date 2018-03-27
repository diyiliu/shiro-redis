package com.diyiliu.support.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.net.InetAddress;

/**
 * Description: UserRealm
 * Author: DIYILIU
 * Update: 2017-11-24 10:50
 */

public class UserRealm extends AuthorizingRealm {

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String) token.getPrincipal();
        /*User user = userService.findUser(username);

        if (user == null) {
            // 找不到用户
            throw new UnknownAccountException();
        }*/

        /*if (Boolean.TRUE.equals(user.getLocked())) {

            // 用户锁定
            throw new LockedAccountException();
        }*/

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                "admin",
                "19a096da58f072f8dba15ed0402d9e99",
                ByteSource.Util.bytes("admin57c5eec31d71ee4e74f86e6750ad73cf"),
                getName());


        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            Session session = SecurityUtils.getSubject().getSession();

            session.setAttribute("host", hostAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return authenticationInfo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        String username = (String) principals.getPrimaryPrincipal();
/*
        List<Role> roleList = userService.findUserRoles(username);
        Set<String> roleCodes = roleList.stream().map(Role::getRoleCode).collect(Collectors.toSet());
        Set<Long> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toSet());

        List<Privilege> privilegeList = userService.findPrivileges("role", roleIds);
        Set<String> permissions = privilegeList.stream().map(Privilege::getPermission).collect(Collectors.toSet());
*/

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        authorizationInfo.setRoles(roleCodes);
//        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }
}
