package cc.zdj.coder.util;

import cc.zdj.coder.dto.DomainDto;
import cc.zdj.coder.dto.MapperDto;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mapper.xml文件辅助类
 */
public class MapperUtil {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(MapperUtil.class);

    private static final String UPPER_WHERE = "WHERE";
    private static final String LOWER_WHERE = "where";

    /**
     * 获取resultMap中的sql语句
     *
     * @param content
     * @return
     */
    public static String getResultMapSql(String content) {
        String res = getSqlByType("<resultMap([\\s|\\S]*)</resultMap>", content);
        StringBuffer buffer = new StringBuffer();
        String[] lines = res.split("\n");
        String first = lines[0];
        // </resultMap>标签
        String last = lines[lines.length - 1];
        buffer.append(first).append("\n");
        for (String line : lines) {
            if (line.contains("<id ") || line.contains("<result ")) {
                buffer.append("\t").append(line).append("\n");
            }
        }
        if (StringUtils.isNotEmpty(last)) {
            last = last.replace("  ", "\t");
            buffer.append(last);
        }
        return buffer.toString();
    }

    /**
     * 通过正则表达式获取sql语句
     *
     * @param regStr  正则表达式
     * @param content sql字符串
     * @return java.lang.String
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 16:20
     */
    private static String getSqlByType(String regStr, String content) {
        Pattern pattern = Pattern.compile(regStr);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getColumnListSql(String content) {
        String res = getSqlByType("<sql([\\s|\\S]*?)Base_Column_List([\\s|\\S]*)</sql>", content);
        if (StringUtils.isEmpty(res)) {
            return res;
        } else {
            res = res.replace("  </sql>", "\t</sql>");
        }
        // 判断是否包含 大数据类型
        int index = res.indexOf("<sql id=\"Blob_Column_List\">");
        if (index == -1) {
            return res;
        }
        res = res.replace("<sql id=\"Blob_Column_List\">", "");
        res = res.replaceFirst("</sql>", ",");
        return res;
    }

    /**
     * <select id="findByObject" parameterType="cc.zdj.hrss.exam.coursepracticequestion.domain.CoursePracticeQuestion" resultMap="BaseResultMap">
     * select
     * <include refid="Base_Column_List" />
     * from course_practice_question
     * <where>
     * is_del = 0
     * </where>
     * </select>
     *
     * @param content
     * @return java.lang.String
     * @Throws
     * @Author zhangdj
     * @date 2022/3/3 17:31
     */
    public static String getFindByObjectSql(String content) {
        String parameterType = DomainUtil.getParameterType(content);
        String res = getSqlByType("<select([\\s|\\S]*?)selectByPrimaryKey([\\s|\\S]*)</select>", content);
        if (StringUtils.isNotEmpty(res)) {
            if (res.contains(LOWER_WHERE)) {
                res = res.substring(0, res.lastIndexOf(LOWER_WHERE));
            }
            if (res.contains(UPPER_WHERE)) {
                res = res.substring(0, res.lastIndexOf(UPPER_WHERE));
            }
            String[] lines = res.split("\n");
            String first = lines[0];

            StringBuffer buffer = new StringBuffer();
            buffer.append(first).append("\n");
            for (String line : lines) {
                if (StringUtils.isBlank(line.trim()) ||
                        line.contains("Blob_Column_List") ||
                        line.contains("<select ") ||
                        line.contains("</select>")
                        || ",".equals(line.trim())) {
                    continue;
                }
                buffer.append(line).append("\n");
            }
            buffer.append("\t<where>\n\t\tis_del = 0 \n\t</where>\n</select>\t");
            res = buffer.toString();
            res = res.replace("ResultMapWithBLOBs", "BaseResultMap");
            res = res.replace("selectByPrimaryKey", "findByObject");
            res = res.replace("java.lang.Long", parameterType);
            return res;
        }
        return null;
    }

    public static String getSelectByIdSql(String content) {
        String res = getSqlByType("<select id=\"selectByPrimaryKey\"[\\s|\\S]*?</select>", content);
        if (StringUtils.isNotEmpty(res)) {
            if (res.contains("<include refid=\"Blob_Column_List\" />")) {
                res = res.replace("<include refid=\"Blob_Column_List\" />", "");
                res = res.replaceFirst(",", "");
            }
            return res.replace("selectByPrimaryKey", "findById");
        }
        return null;
    }

    public static String getInsertSql(String content) {
        String res = getSqlByType("<insert id=\"insertSelective\"[\\s|\\S]*?</insert>", content);
        if (StringUtils.isNotEmpty(res)) {
            return "  " + res.replace("insertSelective", "insert").replaceAll("  ", "\t");
        }
        return null;
    }

    public static String getUpdateByIdSql(String content) {
        String res = getSqlByType("<update id=\"updateByPrimaryKeySelective\"[\\s|\\S]*?</update>", content);
        if (StringUtils.isNotEmpty(res)) {
            return "  " + res.replace("updateByPrimaryKeySelective", "updateById").replaceAll("  ", "\t");
        }
        return null;
    }

    public static String getDeleteByIdSql(String content) {
        String res = getSqlByType("<delete([\\s|\\S]*?)</delete>", content);
        if (StringUtils.isNotEmpty(res)) {
            return "  " + res.replace("deleteByPrimaryKey", "deleteById").replaceAll("  ", "\t");
        }
        return null;
    }

    /**
     * 从参数中获取sql信息
     *
     * @param content 指定格式的sql字符串。使用第三方生成的sql mapper.xml 文件中的sql转换来的
     * @return cc.zdj.coder.dto.MapperDto
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 16:18
     */
    public static MapperDto generateMapperTemplate(String content) {
        MapperDto template = new MapperDto();
        template.setResultMap(getResultMapSql(content));
        template.setColumnListSql(getColumnListSql(content));
        template.setInsertSql(getInsertSql(content));
        template.setDeleteByIdSql(getDeleteByIdSql(content));
        template.setUpdateByIdSql(getUpdateByIdSql(content));
        template.setFindByIdSql(getSelectByIdSql(content));
        // 生成 findByObject 通用方法，暂时去掉，使用mapperVM中模板生成
//        template.setFindByObjectSql(getFindByObjectSql(content));
        return template;
    }

    /**
     * 生成两个mapper.xml文件
     *
     * @param template
     * @param domainDto
     * @return void
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 16:18
     */
    public static void generateXmlFile(MapperDto template, DomainDto domainDto) throws IOException, IllegalAccessException {
        File mapperDir = new File(String.format("%s%smapper", FileUtil.getTargetResourcesPath(), FileUtil.SEPARATOR));
        if (!mapperDir.exists()) {
            mapperDir.mkdirs();
        }
        String targetBasePath = String.format("%s%s%s%s%sBaseMapper.xml", FileUtil.getTargetResourcesPath(), FileUtil.SEPARATOR, "mapper", FileUtil.SEPARATOR, domainDto.getDomainName());
        FileUtil.generateFileFromVm("assets/mapper/baseMapper.vm", targetBasePath, template);
        String targetExtPath = String.format("%s%s%s%s%sExtMapper.xml", FileUtil.getTargetResourcesPath(), FileUtil.SEPARATOR, "mapper", FileUtil.SEPARATOR, domainDto.getDomainName());
        FileUtil.generateFileFromVm("assets/mapper/extMapper.vm", targetExtPath, template);
    }
}
