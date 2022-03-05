package cc.wdcloud.coder.service;

import lombok.Data;

import java.util.List;

/**
 * 生成文件所需的字段
 */
@Data
public class Param {
    private String objName;
    private List<String> fieldList;
    private String artifactId;
}
