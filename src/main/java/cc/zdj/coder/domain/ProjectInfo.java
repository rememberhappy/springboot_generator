package cc.zdj.coder.domain;

import lombok.Data;

import java.util.List;

/**
 * 脚手架参数信息
 */
@Data
public class ProjectInfo {
    /**
     * group和artifactId 是pom文件中的 坐标 信息，用来在maven库中找唯一的项目。
     * 此处还用来指定包结构，生成的脚手架的包路径为：group值.artifactId值。artifactId用于包结构的时候，会将-去掉，大小写不作处理
     */
    private String group;
    private String artifactId;

    /**
     * 数据库基本信息，
     * 脚手架会根据 指定的数据库表信息生成 一整套的基础代码，
     */
    private String url;
    private String username;
    private String password;
    // 数据库名
    private String databaseName;

    /**
     * 数据库中的 表名以及表名对应的实体类名。可以是多个表名
     */
    private List<TableDomainNode> tableDomainNodeList;

    /**
     * 生成代码时，注释中的作者
     */
    private String author;

    /**
     * VUE 前端框架的版本
     */
    private Integer vueVersion;

    /*
     * 参数数据格式：
     * {
     *   "group": "cc.xxx.wdn",
     *   "artifactId": "teaching",
     *   "url": "jdbc:mysql://192.168.9.225:3307/xxx",
     *   "username": "root",
     *   "password": "xxx",
     *   "databaseName": "hrss",
     *   "author": "zhangdj",
     *   "vueVersion": 3,
     *   "tableDomainNodeList": [
     *     {
     *       "tableName": "exam",
     *       "domainName":"Exam"
     *     }
     *   ]
     * }
     */
//    {
//        "group": "cc.wdcloud.hrss",
//            "artifactId": "hrssplatformmarketing",
//            "url": "jdbc:mysql://rm-2zex25uupxs6di2z2.mysql.rds.aliyuncs.com/platform_marketing_system_fat",
//            "username": "fat",
//            "password": "Mn5GfUyu5I",
//            "databaseName": "hrss",
//            "author": "zhangdj",
//            "vueVersion": 3,
//            "tableDomainNodeList": [
//                {
//                    "tableName": "physical_card_code_user_equity",
//                        "domainName":"PhysicalCardCodeUserEquity"
//                }
//            ]
//    }
}

