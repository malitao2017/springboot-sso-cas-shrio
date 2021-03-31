# springboot-sso-cas-shrio
springboot-sso-cas-shrio

github地址为：
https://github.com/malitao2017/springboot-sso-cas-shrio.git

文章来源：
springboot + shiro + cas 实现 登录 + 授权 + sso单点登录 
https://blog.csdn.net/qq_33101675/article/details/105440375

springboot + shiro + cas 实现 登录 + 授权 + sso单点登录 

############################################################################################
总简介：
1.tomcat生成ssl的8443安全端口，配置证书 密码123456，生成三个tomcat 
一个server：8443 8080 
两个client：18443 18080 ；28443 28080 
apache-tomcat-7.0.91-sso-ssl-server
apache-tomcat-7.0.91-sso-ssl-cas-client1
apache-tomcat-7.0.91-sso-ssl-cas-client2

2.host 配置：
127.0.0.1 sso.maple.com
127.0.0.1 client1.sso.maple.com
127.0.0.1 client2.sso.maple.com
3.中间件方式：
一个server：直接把war包放到server的tomcat下 cas-server-webapp-4.2.4.war
重命名为  cas.war，并复制到 tomcat里的webapps里

两个client：都是使用自带的examples作为载体
第一步：将 cas-client-core-3.2.1.jar 和 commons-logging-1.1.jar 放到 \webapps\examples\WEB-INF\lib下面
第二步：webapps\examples\WEB-INF\web.xml添加如下配置
详情见：《cas的client的web.xml中配置》
https://demo.micmiu.com:8443/cas/login
http://app1.micmiu.com:18080

https://demo.micmiu.com:8443/cas
http://app1.micmiu.com:18080

修改为：上面部署的server的cas的端口，和本client的端口	
修改host后：
client1：
https://sso.maple.com:8443/cas/login
http://client1.sso.maple.com:18080

https://sso.maple.com:8443/cas
http://client1.sso.maple.com:18080
	
client2：
https://sso.maple.com:8443/cas/login
http://client2.sso.maple.com:28080

https://sso.maple.com:8443/cas
http://client2.sso.maple.com:28080

启动三个tomcat后，访问：
一个server：
https://sso.maple.com:8443/cas/login 或：https://sso.maple.com:8443/cas
用户名为：casuser
默认密码：Mellon
退出：https://sso.maple.com:8443/cas/logout

两个client：
http://client1.sso.maple.com:18080/examples/servlets/
http://client2.sso.maple.com:28080/examples/servlets/
自带的欢迎页面为：
http://client1.sso.maple.com:18080/examples/servlets/servlet/HelloWorldExample 
http://client2.sso.maple.com:28080/examples/servlets/servlet/HelloWorldExample 


4.shiro的cas兼容的代码：
问题1：不能做http的访问
Application Not Authorized to Use CAS
D:\work\sso\apache-tomcat-7.0.91-sso-ssl-server\webapps\cas\WEB-INF\classes\services
下面的：Apereo-10000002.json HTTPSandIMAPS-10000001.json

参考1：解决
 该方法和简单，进入到cas服务端的web容器里，找到cas工程下的HTTPSandIMAPS-10000001.json文件，该文件在服务端的\WEB-INF\classes\services目录内

将这个文件里面的

"serviceId" : "^(https|imaps)://.*" 

修改为
    "serviceId" :"^(https|imaps|http)://.*"
————————————————
版权声明：本文为CSDN博主「陈南志」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/pucao_cug/article/details/69048359

参考2
做配置 :
最后查找资料知晓，需要更改CAS resources下services目录里面有两个json文件，将Apereo-10000002.json文件的"serviceId" 修改为 "^http:."，HTTPSandIMAPS-10000001.json 下的
"serviceId" 修改为 "^(http|imaps)://." ,我只是本地做测试 没有使用ssl验证，如果需要把http 更改为https就可以了 修改效果如下：

作者：BetterFuture
链接：https://www.jianshu.com/p/d4427f1517dd
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。



问题2：必须修改host，不然cas不认
host 配置：
127.0.0.1 sso.maple.com
127.0.0.1 client1.sso.maple.com
127.0.0.1 client2.sso.maple.com

问题3：必须使用域名做证书的开头，jdk做证书的时候，第一个是域名，剩下的全部都是回车，参考下面的jdk做证书过程，若是做错了，需要做证书的删除，以及电脑的重启
我用的全部是 mlt ，会报错，还是直接使用 sso.maple.com ，单点登录需要配置成域名，不能是ip
后面会用到 sso.maple.com，已经做了 host 映射
https://www.cqmaple.com/201703/error-java-security-cert-certificateexception-no-name-matching-localhost-found.html
使用tomcat开启https 需要使用keytool命令来生产一个证书。


5.改造cas-server，集成我们的DB
https://sso.maple.com:8443/cas
账户：zhangsan
密码：zhangsan 
默认的账密：casuser/Mellon
退出：https://sso.maple.com:8443/cas/logout
详细配置参考：下面的 《4、改造cas-server，集成我们的DB》
粗流程：
在这一步当中：我这里用的是4.2版本，因为他是maven构建的，5以上的版本是gradle构建的，我对gradle没有maven熟练。
源码：git clone https://github.com/apereo/cas-overlay-template/tree/4.2
分支：4.2
需要：
修改1：把路径改对WEB-INF/spring-configuration/propertyFileConfigurer.xml
<util:properties id="casProperties" location="file:D:\beijing\project\my\cas-overlay-template-4.2\etc\cas.properties" />
修改2：新建数据库：test_sso 建表：user 并插入数据
修改3：引入pom
修改4：修改 deployerConfigContext.xml文件
修改 D:\beijing\project\my\cas-overlay-template-4.2\target\war\work\org.jasig.cas\cas-server-webapp\WEB-INF\deployerConfigContext.xml 这个文件
修改5：cas.properties 文件
把 etc/cas.properties 里面的 cas.jdbc.authn.query.sql 前面的注释去掉，加上 sql 


6.到目前为止：分为了两个：
1、D:\work\sso\1 中间件方式
2、D:\work\sso\2 代码集成shiro的cas
第二种的方式里，
只有一个tomcat，用于部署cas的代码shiro改编。idea使用war的远程部署方式，部署
而两个client，使用springboot的内置tomcat方式启动

代码：
https://github.com/malitao2017/springboot-sso-cas-shrio.git

前提：
第一次尝试：必须把server部署到tomcat中，mysql必须启动才能用
第二次尝试：mysql不启动也是可以的，都是按照手写的代码走
第三次尝试：直接用idea把server部署到tomcat中，idea中tomcat启动配置： https://sso.maple.com:8443/cas
最终证明不可以，
deployerConfigContext.xml 和 HTTPSandIMAPS-10000001.json
会自动补充，并且不是自己修改的内容，只能打成war放到tomcat中运行
第四次尝试：使用idea用exploded方式进行部署，参考的恒泰的uad的配置，uad是把所有的CAS自动生成的文件放到项目里进行修改，而本次自己的修改只是采用CAS自己的jdbc数据库访问，以及自己做的密码验证，自己拿出来的文件有：
deployerConfigContext.xml 放到 webapp/WEB-INF/下
cas.properties 放到 webapp/WEB-INF/下，同时webapp/WEB-INF/spring-configuration中修改：<util:properties id="casProperties" location="${cas.properties.config.location:/WEB-INF/cas.properties}"/>
HTTPSandIMAPS-10000001.json 放到 /main/resouces/services 下
原理是，idea自己加载到tomcat中的时候，这三个文件就会自动覆盖目标
最重要的是可以打断点

server启动方式是war包，部署到tomcat中，两个客户端使用springboot的内置tomcat方式启动
cas的server是基于上一步的配置mysql的方式进行，自己的测试客户端 member 使用的是8082
member:
http://localhost:8082/index
order:
http://localhost:8083/index

单点登录服务：配置了证书，必须使用8443连接
https://sso.maple.com:8443/cas
账户：zhangsan
密码：zhangsan
默认的账密：casuser/Mellon 使用了数据库方式，就不能用了。自己手动更改，可以用了
退出：https://sso.maple.com:8443/cas/logout

文件：增加http的访问
HTTPSandIMAPS-10000001.json
由："serviceId" : "^(https|imaps)://.*",
改为：
"serviceId" : "^(https|imaps|http)://.*",
