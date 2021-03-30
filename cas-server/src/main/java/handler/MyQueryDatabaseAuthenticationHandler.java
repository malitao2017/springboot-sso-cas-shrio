package handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.security.GeneralSecurityException;

/**
 * @Description 一般项目中，user表会有个salt盐值，DB里存的密码 = Sha256/MD5（password + salt）
 * @Date 2020/4/18 15:08
 * @Created by 王弘博
 */
@Component("myQueryDatabaseAuthenticationHandler")
public class MyQueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    @NotNull
    private String sql;

    @NotNull
    private String saltSql;

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential)
            throws GeneralSecurityException, PreventedException {

        if (StringUtils.isBlank(this.sql) || getJdbcTemplate() == null) {
            throw new GeneralSecurityException("Authentication handler is not configured correctly");
        }

        //用户输入的用户名
//        final String username = credential.getUsername();
        String username = credential.getUsername();

        //用户输入的密码，进行加密之后的结果
        //final String encryptedPassword = this.getPasswordEncoder().encode(credential.getPassword());

        try {
//            //查询DB里存的密码
//            final String dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, username);
//            //查询DB里存的盐值
//            final String dbSalt = getJdbcTemplate().queryForObject(this.saltSql, String.class, username);
            String dbPassword = "";//查询DB里存的密码
            String dbSalt = "";//查询DB里存的盐值
            //本项目是可以自动从数据库中取的，这里做一个处理，数据库断的时候，或者直接跳过数据，做一个处理，这样，代码就会简洁很多
            try {
                dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, username);
                dbSalt = getJdbcTemplate().queryForObject(this.saltSql, String.class, username);
            }catch (Exception e){
                logger.error("数据库不能连接：可以从数据库中取，这里跳过，直接手动处理");
            }
            logger.error("手动赋值，有：zhangsan/zhangsan casuser/Mellon ");
//        密码：zhangsan 盐值：123456  加密后：d329571893ea0e41a3718a568a10794f
//        密码：123456   盐值：123456  加密后：fa89e5302256d3da3007cad3234742a4
//       对默认的密码也做一个处理：默认的账密：casuser/Mellon
//        密码：Mellon   盐值：casuser  加密后：6ab427a990d9385355b35d7659262407
            if("zhangsan".equals(username)){
                dbPassword = "d329571893ea0e41a3718a568a10794f";
                dbSalt = "123456";
            }
            if("casuser".equals(username)){
                dbPassword = "6ab427a990d9385355b35d7659262407";
                dbSalt = "casuser";
            }
            logger.error("目前：前端传入用户："+username+" 前端传入密码："+credential.getPassword()+" 自己处理后盐值为："+dbSalt+" 自己处理后的加密后："+dbPassword);

            String encryptedPassword = new Md5Hash(credential.getPassword(), dbSalt, 1024).toHex();

            //加密（用户输入的+salt） 和 DB里存的 进行比较
            if (!dbPassword.equals(encryptedPassword)) {

                throw new FailedLoginException("Password does not match value on record.");
            }

        } catch (final IncorrectResultSizeDataAccessException e) {

            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            } else {
                throw new FailedLoginException("Multiple records found for " + username);
            }

        } catch (final DataAccessException e) {

            throw new PreventedException("SQL exception while executing query for " + username, e);
        }

        return createHandlerResult(credential, this.principalFactory.createPrincipal(username), null);
    }

    /**
     * @param sql The sql to set.
     */
    @Autowired
    public void setSql(@Value("${cas.jdbc.authn.query.sql:}") final String sql) {
        this.sql = sql;
    }

    /**
     * @param saltSql The saltSql to set.
     */
    @Autowired
    public void setSaltSql(@Value("${cas.jdbc.authn.query.salt.sql:}") final String saltSql) {
        this.saltSql = saltSql;
    }

    @Override
    @Autowired(required = false)
    public void setDataSource(@Qualifier("queryDatabaseDataSource") final DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    public static void main(String[] args) {
//        密码：zhangsan 盐值：123456  加密后：d329571893ea0e41a3718a568a10794f
//        密码：123456   盐值：123456  加密后：fa89e5302256d3da3007cad3234742a4
//       对默认的密码也做一个处理：默认的账密：casuser/Mellon
//        密码：Mellon   盐值：casuser  加密后：6ab427a990d9385355b35d7659262407
        String encryptedPassword1 = new Md5Hash("zhangsan", "123456", 1024).toHex();
        System.out.println(encryptedPassword1);

        String encryptedPassword = new Md5Hash("123456", "123456", 1024).toHex();
        System.out.println(encryptedPassword);

        String encryptedPassword2 = new Md5Hash("Mellon", "casuser", 1024).toHex();
        System.out.println(encryptedPassword2);


    }
}
