

#### 一、先写一个原生的SpringBoot应用。

1、创建一个原生的SpringBoot应用。

```java
// 编写一个controller
@RestController
public class SampleChild1Controller {
 
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloworld() {
        return "helloworld, " + this.toString();
    }
}

// 编写启动类
@SpringBootApplication
public class SampleChild1AutoConfiguration {

    public static void main(String[] args) {
         SpringApplication.run(SampleChild1AutoConfiguration.class, args);
    }
}
```

2、运行启动类后，打开浏览器访问 http://127.0.0.1:8080/ 页面，可以看到使用SpringBoot开发起来非常简单。

#### 二、以Jushata模块的方式运行。

1、打包时将原生SpringBoot应用打成fat-jar，复制fat-jar到 /Users/didi/Workspace/jushata/jushata-samples/jushata-modules/ 目录（或者其它目录）。

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

2、创建一个新的SpringBoot应用做为Jushata容器工程，在pom文件中引入Jushata支持。

```xml
// 模块继承于jushata-parent
<parent>
    <groupId>com.didiglobal.jushata</groupId>
    <artifactId>jushata-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
 
// 引入jushata-boot支持
<dependency>
    <groupId>com.didiglobal.jushata</groupId>
    <artifactId>jushata-boot</artifactId>
    <version>${jushata-boot.version}</version>
</dependency>
```

3、在配置文件application.properties中配置Jushata模块加载路径。

```properties
# 步骤1中Jushata模块打包复制的目录
jushata.modules=/Users/didi/Workspace/jushata/jushata-samples/jushata-modules/
```

4、创建启动类，测试。

```java
@SpringBootApplication
public class JushataApplicationSampleTest {
 
    public static void main(String[] args) {
        JushataApplication.run(JushataApplicationSampleTest.class, args);
    }
}
```

5、运行启动类后，打开浏览器访问 http://127.0.0.1:8080/ 页面，发现和使用原生SpringBoot得到相同结果。

#### 三、补充说明。

1、通过示例可见，Jushata无缝支持原生SpringBoot应用。
2、如果同时运行多个Jushata模块（这也是Jushata的精髓），且模块都是web应用，则需要使用 @JushataBootApplication 替换 @SpringBootApplication，并指定模块的http端口（也可以通过在模块的application.properties配置文件中通过server.port来指定），如：

```java
// 模块1指定http端口为8081
@JushataBootApplication(httpPort = 8081)
public class SampleChild1AutoConfiguration {
}

// 模块2指定http端口为8082
@JushataBootApplication(httpPort = 8082)
public class SampleChild2AutoConfiguration {
}
```

3、本文示例可见 [jushata-samples](./jushata-samples)


