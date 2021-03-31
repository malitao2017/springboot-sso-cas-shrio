package com.gane.maple.member.config.shiro;

import com.gane.maple.member.model.User;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 * @Description UserShiroCasRealm
 * @Date 2020/4/9 19:59
 * @Created by 王弘博
 */
public class UserShiroRealm extends AuthorizingRealm {

    /**
     * 权限授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //表明当前登录者的角色(真实项目中这里会去查询DB，拿到用户的角色，存到redis里)
        info.addRole("admin");

        //表明当前登录者的角色(真实项目中这里会去查询DB，拿到该角色的资源权限，存到redis里)
        info.addStringPermission("admin:manage");

        return info;
    }

    /**
     * 登录认证
     *
     * @param authenticationToken
     * @return
     */
//    第一节：只用shiro，放开注释
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        //通过token去查询DB，获取用户的密码，这里密码直接写死
        User user = new User();
        user.setUsername(token.getUsername());
        //测试情况： 用户：maple 密码为：123456 盐值：maple 加密后：26bfdfe8689183e9235b1f0beb7a6f46
        return new SimpleAuthenticationInfo(user, "26bfdfe8689183e9235b1f0beb7a6f46",
                ByteSource.Util.bytes(user.getUsername()), getName());

    }

    /**
     * 密码(123456) + salt(maple)，得出存进数据库里的密码：26bfdfe8689183e9235b1f0beb7a6f46
     *
     * @param args
     */
    public static void main(String[] args) {
        String hashAlgorithName = "MD5";
        String password = "123456";
        int hashIterations = 1024;//加密次数
        ByteSource credentialsSalt = ByteSource.Util.bytes("maple");
        SimpleHash simpleHash = new SimpleHash(hashAlgorithName, password, credentialsSalt, hashIterations);
        String s = simpleHash.toHex();
        System.out.println(s);
    }

}
