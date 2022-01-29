### 设计

#### 模块组成

* 入口模块：aop拦截或者接口
* 解析模块：生成解析日志，需要拿到操作的业务数据
* 日志存储模块：操作日志持久化
* 组装模块：日志模块独立，客户端注解引入该模块

#### 模块说明

* 入口模块
  aop拦截有LogRecordAnnotation注解的方法,根据注解的属性，得到操作日志dto，打印并持久化(PS:方法内部调用不会走切面拦截，可以使用接口直接调用)
  接口调用：传入操作类型和操作bean，生成操作日志，使用此方法则不走解析模块，根据规则生成形如："增加用户"张三""

* 解析模块

  * 用户信息：
    默认实现：从mdc中获取用户相关属性，客户端需要配置拦截器，根据请求，从cookie或session中拿到用户相关属性，put到context中

    ```
    client:
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MDC.put("__operator_id", userVo.getId());
        MDC.put("__operator_name", userVo.getName());
        MDC.put("__operator_org_id_path", userVo.getOrgIdPath());
        MDC.put("__operator_org_name_path", userVo.getOrgNamePath());
        return true;
    }

    log-module:
    public OperatorDto getOperator() {
        OperatorDto operatorDto = new OperatorDto();
        operatorDto.setId(MDC.get("__operator_id"));
        operatorDto.setName(MDC.get("__operator_name"));
        operatorDto.setOrgIdPath(MDC.get("__operator_org_id_path"));
        operatorDto.setOrgNamePath(MDC.get("__operator_org_name_path"));
        return operatorDto;
    }
    ```

    

  * 另外支持客户自定义获取用户信息的接口，继承jar中的IOperatorService即可，实现getOperator()方法

* 操作类型，从注解中定义字段，目前必传且只支持引用jar的枚举类

* 操作内容：从注解中拿到操作内容模版，使用spel解析模版，支持自定义函数，将客户端自定义函数增加LogRecordFunction注解即可。

#### 接入方法

1. 引入模块依赖

```
<dependency>
	<groupId>com.aispeech.aistore</groupId>
	<artifactId>aistore-tp-admin-log</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

2. 在客户端注解引入日志模块

   ```
   @EnableLogRecord
   public class AistoreTpAdminApp {
       xxx
   }
   ```

3. 需要记录操作日志的方法进行注解，形如：

   ```
   @LogRecordAnnotation(category = OperateCategoryEnum.MODIFY, template = "'修改' +  #getLogModuleName(#tableName)")
   ```
    或者直接在业务逻辑中调用接口
    ```
    public void addApplication(ApplicationInfoDto applicationInfoDto) {
        ...
        ApplicationInfoPo applicationInfoPo = convert(applicationInfoDto);
        dao.save(applicationInfoPo);
        logAspectService.log(OperateCategoryEnum.ADD, applicationInfoPo);
    }

    ```

   

4. 客户端增加自定义方法，为spel表达式解析执行自定义函数

   ```
   @LogRecordFunction
   public String getLogModuleName(String tableName) {
   	return "hello_" + tableName;
   }
   ```

   



   