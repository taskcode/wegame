package com.wegame.tools.shiro;

import com.wegame.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * 自定义sesionid生成
 */
public class CustomSessionIdGenerator implements SessionIdGenerator {

    @Override
    public Serializable generateId(Session session) {
        System.out.println("-----------------自定义sesionid生成-----------------");
        return "wbh"+UUID.randomUUID().toString().replace("-","");

    }


}
