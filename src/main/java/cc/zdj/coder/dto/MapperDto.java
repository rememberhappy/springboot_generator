package cc.zdj.coder.dto;

import lombok.Data;

/**
 * mapper.xml相关内容
 */
@Data
public class MapperDto {
    private String tableName;
    private String fullDaoName;
    // 实体类的包全路径，mapper中使用
    private String fullDomainName;
    private String resultMap;
    private String columnListSql;
    private String findByIdSql;
    private String findByObjectSql;
    private String updateByIdSql;
    private String deleteByIdSql;
    private String insertSql;
}
