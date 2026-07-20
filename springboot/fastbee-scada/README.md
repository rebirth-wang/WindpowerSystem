# Web组态-后端源码

注意注意，主系统和组态对应版本如下：
主V2.3 <=> 组V1.0.0;  主V2.4~V2.5.1 <=> 组V2.5.1; 其余版本对应起来就行，sql脚本使用对应版本的执行就行；

## 一、使用方法

1. 获取源码后，修改文件夹名称为 `fastbee-scada` ，然后放置于项目的 `wumei-smart/sprintboot` 文件夹下

   ```
   完整路径: /wumei-smart/sprintboot/fastbee-scada
   ```

2. 执行组态sql目录下的sql文件时，如果主系统是V2.3版本，请使用V1.0sql文件，建议先**比对**一下自己系统的数据（防止已有的会重复添加）；如果主系统是V2.4及之后的版本，找到相应版本的sql，可直接整合到主系统sql文件里，然后部署执行即可；

3. 图库文件夹（v1.0.0版本是avatar包，之后的版本是webconfig包）需要上传至服务器才可使用，因为文件夹太大，没有放在代码目录里，购买后单独发

   **注意**：如果是v1.0.0版本在服务器上/var/data/java/uploadPath目录下找到已有的avatar文件夹，如果没有，可直接将avatar文件夹上传至此目录下；如果已存在（**建议先备份**），然后需要自己比对两份文件夹内容，合并出新的一份avatar文件夹，然后再上传，**切勿直接替换，不然会把已有的文件弄没了**；
   如果是之后的版本，直接把webconfig压缩包解压出来，解压出来的文件名为webconfig，然后直接上传至/var/data/java/uploadPath目录下就行；

4. 默认启用V2.3新版多租户，如果不想使用新版多租户，请找到该文件com.fastbee.scada.utils.ScadaConstant，把以下代码改为false就行
    注意：后面切到多租户，请自行修改老数据，多租户版本组态是绑定到机构管理员账号上
    如果系统是2.3之前的版本，请把新版多租户相关报错的代码注释掉就行，其他报错的需自行调整一下

     ```
     /**
     * 多租户版本启用，不用多租户改为false
     */
    public static final Boolean ENABLE_TENANT = false;
     ```

## 二、整合到项目

**注意**：拉一下仓库最新代码，把组态文件夹复制到主系统，只需把springboot目录下pom.xml文件和fastbee-admin目录下pom.xml文件里面关于组态依赖配置代码注释放开就行

     ```
     // 提示：以下代码加在<modules> </modules> 里面
     <module>fastbee-scada</module>
     ```

     ```
     // 提示：以下代码加在<dependencies> </dependencies> 里面
     <!-- 组态模块 -->
     <dependency>
         <groupId>com.fastbee</groupId>
         <artifactId>fastbee-scada</artifactId>
         <version>${fastbee.version}</version>
     </dependency>
     ```

   - 在/wumei-smart/sprintboot/fastbee-admin/pom.xml配置文件里以下配置

     ```
     // 提示：以下加在<dependencies> </dependencies>  里面
     <!-- 组态模块 -->
     <dependency>
         <groupId>com.fastbee</groupId>
         <artifactId>fastbee-scada</artifactId>
     </dependency>
     ```

## 三、运行代码

