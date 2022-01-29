# aop-log
### 表结构设计：operate_log
字段名| 字段类型| 字段释义| 备注
--|:--:|:--:|--:
id| 		long| 		ID| 						| 
system| 	keyword| 	日志所属系统 如：aistore-tpaistore-ds|		 |	
tenant_id| 	long| 		多租户ID| 				日志多租户标识|
user_id| 	keyword| 	用户ID|				 用户的唯一标识|
user_name|	keyword|	用户姓名 |				|
org_id_path| 	keyword| 	用户组织架构ID路径 |	|
org_name_path|	keyword| 	用户组织架构名称路径|	| 
moduleId| 	long| 		操作模块ID|	| 
module_name| 	keyword| 	模块名称 |	|
biz_id| 	keyword| 	操作对象业务ID| 	如orderId、applicatId等|
category| 	keyword| 	操作种类 add-新增delete-删除modify-修改publish-发布login-登录export-导出import-导入distribute-复核分配refresh-刷新结果review-人工复核examine-审核||
detail| 	text| 		操作详细信息| |
result| 	keyword| 	操作结果| 			succeedfailedcancel等|
time| 		date| 		操作时间| 			yyyy-MM-dd HH:mm:ss|
duration| 	long| 		记录日志耗时| 		单位毫秒|

### 实现逻辑设计

#### 模块组成

* 拦截模块：aop拦截&判断逻辑
* 解析模块：生成解析日志，需要拿到操作的业务数据
* 日志存储模块：操作日志持久化
* 组装模块：日志模块独立，客户端注解引入该模块

#### 模块说明

* 拦截模块
  aop拦截有LogRecordAnnotation注解的方法,根据注解的属性，得到操作日志dto，打印并持久化

* 解析模块

  * 用户信息：
    默认实现：从mdc中获取用户相关属性，客户端需要配置拦截器，根据请求，从cookie或session中拿到用户相关属性，put到context中

    ```
    public OperatorDto getOperator() {
        String operatorId = MDC.get("__operator_id");
        String operatorName = MDC.get("__operator_name");
        String orgIdPath = MDC.get("__operator_org_id_path");
        String orgNamePath = MDC.get("__operator_org_name_path");
        OperatorDto operatorDto = new OperatorDto();
        operatorDto.setId(operatorId);
        operatorDto.setName(operatorName);
        operatorDto.setOrgIdPath(orgIdPath);
        operatorDto.setOrgNamePath(orgNamePath);
        return operatorDto;
    }
    ```

    

  * 另外支持客户自定义获取用户信息的接口，继承jar中的IOperatorService即可，实现getOperator()方法

* 操作类型，从注解中定义字段，支持引用jar的枚举类，或自定义操作类型字符串，优先使用字符串属性值

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



2. 需要记录操作日志的方法进行注解，形如：

   ```
   @LogRecordAnnotation(category = OperateCategoryEnum.MODIFY, template = "'修改' +  #getLogModuleName(#tableName)")
   ```

   

3. 增加自定义方法，为spel表达式解析执行自定义函数

   ```
   @LogRecordFunction
   public String getLogModuleName(String tableName) {
   	return "hello_" + tableName;
   }
   ```

   

4. 在客户端注解引入日志模块

   ```
   @EnableLogRecord
   public class AistoreTpAdminApp {
       xxx
   }
   ```

   
