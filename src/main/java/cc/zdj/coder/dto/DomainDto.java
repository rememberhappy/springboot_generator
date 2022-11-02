package cc.zdj.coder.dto;

import cc.zdj.coder.domain.ProjectInfo;
import cc.zdj.coder.util.PackageUtil;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * VM生成代码所用到的实体类
 */
@Data
public class DomainDto {

    // 实体类名
    private String domainName;
    // 实体类全路径。cc.xx.xx.xx.domain.实体类名
    private String fullDomainName;

    // 生成的类名称
    private String daoName;
    private String serviceName;
    private String controllerName;

    // 基础包名称。group值.artifactId值
    private String basePackage;

    // 包名称
    private String daoPackage;
    private String domainPackage;
    private String servicePackage;
    private String controllerPackage;
//    private String mapperDri;

    // 实体类名首字母小写
    private String objName;
    // 实体类名全小写
    private String objPathName;

    // 注释中使用
    private String author;
    private String createDate;

    public DomainDto(ProjectInfo projectInfo, MybatisGenDto mybatisGenDto) {
        this.basePackage = PackageUtil.getBasePackage(projectInfo);
        this.domainName = mybatisGenDto.getDomainName();
        this.objName = this.domainName.substring(0, 1).toLowerCase() + this.domainName.substring(1);
        this.objPathName = this.domainName.toLowerCase();
        this.domainPackage = String.format("%s.%s.domain", this.basePackage, this.objName).toLowerCase();
        this.daoPackage = String.format("%s.%s.dao", this.basePackage, this.objName).toLowerCase();
        this.servicePackage = String.format("%s.%s.service", this.basePackage, this.objName).toLowerCase();
        this.controllerPackage = String.format("%s.%s.controller", this.basePackage, this.objName).toLowerCase();

        // 实体类全路径
        this.fullDomainName = mybatisGenDto.getFullDomainName();
        // dao名
        this.daoName = String.format("%sDao", this.domainName);
        // service名
        this.serviceName = String.format("%sService", this.domainName);
        // controller名
        this.controllerName = String.format("%sController", this.domainName);

        this.author = projectInfo.getAuthor();
        this.createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
