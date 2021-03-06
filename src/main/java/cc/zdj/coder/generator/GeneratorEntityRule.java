package cc.zdj.coder.generator;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * mybatis generator 扩展类，自定义的注释规则
 *
 * @author zhangdj
 * @version 1.0.0
 * @createdAt 2022/3/4 16:47
 * @updatedAt 2022/3/4 16:47
 */
public class GeneratorEntityRule extends DefaultCommentGenerator {
    // 系统信息
    private final Properties systemPro;
    // 定义一个是否使用修改后的模式的标识
    private final boolean suppressAllComments;
    // 当前日期字符串
    private final String currentDateStr;

    public GeneratorEntityRule() {
        super();
        systemPro = System.getProperties();
        suppressAllComments = true;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    /**
     * 设置实体类 类的注释
     *
     * @param topLevelClass
     * @param introspectedTable
     * @Author zhangdj
     * @date 2022/3/4 17:09
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 设置一行备注数据
        topLevelClass.addJavaDocLine("/**");

        // 获取表注释
        String tableRemarks = introspectedTable.getRemarks();
        StringBuilder sb = new StringBuilder();
        // 获取表名  表备注
        sb.append(" * ").append(introspectedTable.getFullyQualifiedTable()).append(" ").append(tableRemarks);
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" * ");

        sb.setLength(0);
        sb.append(" * @author ");
        sb.append(systemPro.getProperty("user.name"));
        topLevelClass.addJavaDocLine(sb.toString());

        sb.setLength(0);
        sb.append(" * @date ");
        sb.append(currentDateStr);
        topLevelClass.addJavaDocLine(sb.toString());

        topLevelClass.addJavaDocLine(" */");
    }

    /**
     * 设置实体类 属性注释
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            String remarks = introspectedColumn.getRemarks();
            if (StringUtils.isNotBlank(remarks)) {
                StringBuilder sb = new StringBuilder();
                field.addJavaDocLine("/**");
                sb.append(" * ");
                sb.append(remarks);
                field.addJavaDocLine(sb.toString().replace("\n", " "));
                field.addJavaDocLine(" */");
            }
        } else {
            super.addFieldComment(field, introspectedTable, introspectedColumn);
        }
    }

    /**
     * 设置实体类 getter注释
     */
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
                method.addJavaDocLine("//获取:" + introspectedColumn.getRemarks());
            }
        } else {
            super.addGetterComment(method, introspectedTable, introspectedColumn);
        }
    }

    /**
     * 设置实体类 setter注释
     */
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
                method.addJavaDocLine("//设置:" + introspectedColumn.getRemarks());
            }
        } else {
            super.addSetterComment(method, introspectedTable, introspectedColumn);
        }
    }

    /**
     * 去掉mapper原始注释
     */
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            super.addGeneralMethodComment(method, introspectedTable);
        } else {
            return;
        }
    }

    /**
     * 去掉mapping原始注释
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        if (suppressAllComments) {
            super.addComment(xmlElement);
        } else {
            return;
        }
    }
}