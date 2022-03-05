package cc.wdcloud.coder.dto;

import cc.wdcloud.coder.domain.ProjectInfo;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 生成代码所用到的实体类
 */
@Data
public class DomainDto {

    private String domainName;
    private String fullDomainName;

    private String daoName;
    private String serviceName;
    private String controllerName;

    private String basePackage;

    private String daoPackage;
    private String domainPackage;
    private String servicePackage;
    private String controllerPackage;
    private String mapperDri;

    private String objName;

    private String author;
    private String createDate;

    public DomainDto(ProjectInfo projectInfo, MybatisGenDto mybatisGenDto) {
        this.basePackage = String.format("%s.%s", projectInfo.getGroup(), projectInfo.getArtifactId());
        this.domainName = mybatisGenDto.getDomainName();
        this.objName = this.domainName.substring(0, 1).toLowerCase() + this.domainName.substring(1);
        this.domainPackage = String.format("%s.%s.domain", this.basePackage, this.objName).toLowerCase();
        this.daoPackage = String.format("%s.%s.dao", this.basePackage, this.objName).toLowerCase();
        this.servicePackage = String.format("%s.%s.service", this.basePackage, this.objName).toLowerCase();
        this.controllerPackage = String.format("%s.%s.controller", this.basePackage, this.objName).toLowerCase();

        this.fullDomainName = mybatisGenDto.getFullDomainName();
        this.daoName = String.format("%sDao", this.domainName);
        this.serviceName = String.format("%sService", this.domainName);
        this.controllerName = String.format("%sController", this.domainName);

        this.author = projectInfo.getAuthor();
        this.createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
