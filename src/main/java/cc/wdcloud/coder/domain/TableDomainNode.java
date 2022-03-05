package cc.wdcloud.coder.domain;

import lombok.Data;

/**
 * 表名和实体类名
 */
@Data
public class TableDomainNode {
    private String tableName;
    private String domainName;
}
