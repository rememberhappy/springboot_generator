package cc.zdj.coder.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 功能注释
 *
 * @author zhangdj
 * @version 1.0.0
 * @createdAt 2022/3/18 17:55
 * @updatedAt 2022/3/18 17:55
 */
public class LombokPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 设置实体类 类注释
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //实体类的import
//        topLevelClass.addImportedType("java.io.Serializable");
        topLevelClass.addImportedType("lombok.Data");
//        topLevelClass.addImportedType("lombok.Builder");
//        topLevelClass.addImportedType("lombok.NoArgsConstructor");
//        topLevelClass.addImportedType("lombok.AllArgsConstructor");

        //实体类的注解
        topLevelClass.addAnnotation("@Data");
//        topLevelClass.addAnnotation("@Builder");
//        topLevelClass.addAnnotation("@NoArgsConstructor");
//        topLevelClass.addAnnotation("@AllArgsConstructor");

        //实体类的注释
//        topLevelClass.addJavaDocLine("/**");
//        topLevelClass.addJavaDocLine(" * @author Sin");
//        topLevelClass.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy/MM/dd/hh:mm").format(new Date()));
//        topLevelClass.addJavaDocLine(" */");

        //接口
//        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("java.io.Serializable"));
        return true;
    }

//    @Override
//    public boolean clientGenerated(Interface anInterface, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        //Mapper文件的注释
//        anInterface.addJavaDocLine("/**");
//        anInterface.addJavaDocLine(" * @author Sin");
//        anInterface.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy/MM/dd/hh:mm").format(new Date()));
//        anInterface.addJavaDocLine(" */");
//        return true;
//    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //不生成getter
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //不生成setter
        return false;
    }
}
