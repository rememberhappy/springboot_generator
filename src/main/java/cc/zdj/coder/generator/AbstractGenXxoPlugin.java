package cc.zdj.coder.generator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.*;

/**
 * 功能注释
 *
 * @author zhangdj
 * @version 1.0.0
 * @createdAt 2022/6/30 21:09
 * @updatedAt 2022/6/30 21:09
 */
public abstract class AbstractGenXxoPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    protected String getXXoType() {
        return "";
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();
        GeneratedJavaFile generatedJavaFile = generatedJavaFiles.get(0);
        TopLevelClass topLevelClass = (TopLevelClass) generatedJavaFile.getCompilationUnit();
        List<String> annotations = topLevelClass.getAnnotations();
        List<Field> fields = topLevelClass.getFields();

        String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String xxoType = getXXoType();
        String xxoName = join(targetPackage.substring(0, targetPackage.lastIndexOf(".")), xxoType).toLowerCase();

        Set<String> importSet = new HashSet<>();
        // 要排除的字段
        ArrayList<String> removerFieldList = new ArrayList<>();
        removerFieldList.add("isDel");
        removerFieldList.add("saasId");
        removerFieldList.add("branchId");
        removerFieldList.add("createTime");
        removerFieldList.add("updateTime");
        removerFieldList.add("createUserId");
        removerFieldList.add("updateUserId");
        TopLevelClass root = new TopLevelClass(join(xxoName, domainObjectName + xxoType));
        for (Field field : fields) {
            String fieldName = field.getName();
            // 排除指定字段
            if (removerFieldList.contains(field.getName())) {
                continue;
            }
            FullyQualifiedJavaType fieldType = field.getType();
            String fullyQualifiedName = fieldType.getFullyQualifiedName();
            if (StringUtils.isNotBlank(fullyQualifiedName) && !fullyQualifiedName.contains("java.lang.Long")) {
                importSet.add(fullyQualifiedName);
            }
            List<String> javaDocLines = field.getJavaDocLines();
            if (CollectionUtils.isNotEmpty(javaDocLines) && javaDocLines.size() > 2) {
                String replace = javaDocLines.get(1).replace(" * ", "");
                Field fieldWrite = new Field(fieldName, fieldType);
                fieldWrite.addJavaDocLine("@ApiModelProperty(value = \"" + replace + "\")");
                if (StringUtils.isNotBlank(fullyQualifiedName) && fullyQualifiedName.contains("java.util.Date")) {
                    importSet.add("com.alibaba.fastjson.annotation.JSONField");
                    fieldWrite.addJavaDocLine("@JSONField(format=\"yyyy-MM-dd HH:mm:ss\")");
                }
                root.addField(fieldWrite);
            }
        }
        // 导入的类
        for (String importStr : importSet) {
            root.addImportedType(importStr);
        }
        root.addImportedType("io.swagger.annotations.ApiModelProperty");
        root.addImportedType("lombok.AllArgsConstructor");
        root.addImportedType("lombok.Builder");
        root.addImportedType("lombok.Data");
        root.addImportedType("lombok.NoArgsConstructor");
        root.addImportedType("java.util.Date");
        // 继承的类
        // root.setSuperClass(queryVoName);
        root.setVisibility(JavaVisibility.PUBLIC);
        for (String annotation : annotations) {
            root.addAnnotation(annotation);
        }

        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        GeneratedJavaFile gjf = new GeneratedJavaFile(
                root,
                targetProject,
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter()
        );
        return Collections.singletonList(gjf);
    }

    protected String join(String... strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str).append(".");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}