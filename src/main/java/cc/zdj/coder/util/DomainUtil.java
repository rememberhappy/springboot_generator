package cc.zdj.coder.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实体类相关的辅助类
 */
public class DomainUtil {

    /**
     * 通过mapper.xml中的数据获取domain类对应的字段
     *
     * @param content 第三方生成的mapper中的内容
     * @return java.util.List<java.lang.String>
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 16:06
     */
    public static List<String> getFieldList(String content) {
        List<String> fieldList = new ArrayList<>();
        if (StringUtils.isEmpty(content)) {
            return fieldList;
        }
        String regexStr = "(<result.*property=\")(\\w+)(\"\\s*/>)";
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            fieldList.add(matcher.group(2));
        }
        return fieldList;
    }

    /**
     * 获取findByObject 的参数类型
     *
     * @param content 第三方生成的mapper中的内容
     * @return java.lang.String
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 16:05
     */
    public static String getParameterType(String content) {
        String regexStr = "(<resultMap.*)(type=\".*\">)";
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(content);
        String res = "";
        while (matcher.find()) {
            res = matcher.group(2);
        }
        return res.substring(6, res.length() - 2);
    }

//    public static void main(String[] args) {
//        String content = "    <resultMap id=\"BaseResultMap\" type=\"co.bugu.tes.answer.domain.Answer\">\n" +
//                "        <id column=\"id\" jdbcType=\"BIGINT\" property=\"id\"/>\n" +
//                "        <result column=\"paper_id\" jdbcType=\"BIGINT\" property=\"paperId\"/>\n" +
//                "        <result column=\"question_id\" jdbcType=\"BIGINT\" property=\"questionId\"/>\n" +
//                "        <result column=\"question_type_id\" jdbcType=\"BIGINT\" property=\"questionTypeId\"/>\n" +
//                "        <result column=\"scene_id\" jdbcType=\"BIGINT\" property=\"sceneId\"/>\n" +
//                "        <result column=\"answer\" jdbcType=\"VARCHAR\" property=\"answer\"/>\n" +
//                "        <result column=\"time_used\" jdbcType=\"INTEGER\" property=\"timeUsed\"/>\n" +
//                "        <result column=\"time_left\" jdbcType=\"VARCHAR\" property=\"timeLeft\"/>\n" +
//                "        <result column=\"is_del\" jdbcType=\"INTEGER\" property=\"isDel\"/>\n" +
//                "        <result column=\"create_user_id\" jdbcType=\"BIGINT\" property=\"createUserId\"/>\n" +
//                "        <result column=\"create_time\" jdbcType=\"TIMESTAMP\" property=\"createTime\"/>\n" +
//                "        <result column=\"update_user_id\" jdbcType=\"BIGINT\" property=\"updateUserId\"/>\n" +
//                "        <result column=\"update_time\" jdbcType=\"TIMESTAMP\" property=\"updateTime\"/>\n" +
//                "    </resultMap>";
//        System.out.println(getParameterType(content));
////        getFieldList(content);
//    }
}
