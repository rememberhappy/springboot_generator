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
        // 实体类的import
//        topLevelClass.addImportedType("java.io.Serializable");
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.Builder");
        topLevelClass.addImportedType("lombok.NoArgsConstructor");
        topLevelClass.addImportedType("lombok.AllArgsConstructor");

        // 实体类的注解
        // 单独使用 @Data 编译后会生成无参构造方法
        topLevelClass.addAnnotation("@Data");
        // 用来生成对象，并且可以为对象链式赋值。单独使用会生成 全属性参数的构造方法
        // 注意 @Builder 注解会使 实体类中的默认值失效，需要在属性上添加注解保证 属性默认值的有效：@Builder.Default
        // 使用方式：UserPopup userPopup = UserPopup.builder().createUserId(CommonInfoHolder.getUserId()).id(id).channelType(1).build();
        topLevelClass.addAnnotation("@Builder");
        // 如果直接使用 @Data和@Builder 两个注解，编译后会没有无参构造方法。如果此时需要添加无参构造注解：@NoArgsConstructor 会报错。解决办法，顺带添加 有参构造注解：@AllArgsConstructor
        topLevelClass.addAnnotation("@NoArgsConstructor");
        topLevelClass.addAnnotation("@AllArgsConstructor");

        // 实体类上的注释
//        topLevelClass.addJavaDocLine("/**");
//        topLevelClass.addJavaDocLine(" * @author Sin");
//        topLevelClass.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy/MM/dd/hh:mm").format(new Date()));
//        topLevelClass.addJavaDocLine(" */");

        // 实体类实现的接口
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
