package cc.zdj.coder.dto;

import lombok.Data;

/**
 * 第三方生成代码的DTO
 */
@Data
public class MybatisGenDto {
    private String tableName;
    private String domainName;
    // 实体类的包全路径，mapper中使用
    private String fullDomainName;

    private String mysqlJarPath;
    private String dbUrl;
    private String username;
    private String password;

    private String domainPackage;
    private String targetProjectFolder;

    private String daoPackage;
    // 生成的 脚手架 代码存放的路径
    private String path;
}
