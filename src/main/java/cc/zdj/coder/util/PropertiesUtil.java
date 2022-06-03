package cc.zdj.coder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 配置文件辅助类
 */
public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

//    public static CodeParam getCodeParam() throws IOException {
//        CodeParam codeParam = new CodeParam();
//        Properties properties = loadProperties();
//        String basePackage = properties.getProperty("package.base");
//        codeParam.setBasePackage(basePackage);
//        codeParam.setDomainPackage(basePackage + "." + properties.getProperty("package.domain"));
//        codeParam.setDaoPackage(basePackage + "." + properties.getProperty("package.dao"));
//        codeParam.setBaseDaoFullName(codeParam.getDaoPackage() + "." + properties.getProperty("domain.name") + "Dao");
//        codeParam.setControllerPackage(basePackage + "." + properties.getProperty("package.controller"));
//        codeParam.setApiPackage(basePackage + "." + properties.getProperty("package.api"));
//        codeParam.setServicePackage(basePackage + "." + properties.getProperty("package.service"));
//        codeParam.setServiceImplPackage(codeParam.getServicePackage() + ".impl");
//        codeParam.setGeneratorPath(FileUtil.getRootPath() + "/generator");
//        codeParam.setCommonPackage(properties.getProperty("package.common"));
//        codeParam.setBaseDaoFullName(codeParam.getCommonPackage() + ".dao.BaseDao");
//        codeParam.setBaseDaoPackage(codeParam.getCommonPackage() + ".dao");
//        codeParam.setDomainSimpleName(properties.getProperty("domain.name"));
//        codeParam.setDomainFullName(codeParam.getDomainPackage() + "." + codeParam.getDomainSimpleName());
//        codeParam.setObjName(codeParam.getDomainSimpleName().substring(0, 1).toLowerCase() + codeParam.getDomainSimpleName().substring(1));
//        codeParam.setTargetProject(FileUtil.getSourcePath());
//        codeParam.setDaoFullName(codeParam.getDaoPackage() + "." + codeParam.getDomainSimpleName() + "Dao");
//
//        codeParam.setDbUrl(properties.getProperty("db.url"));
//        codeParam.setDbUsername(properties.getProperty("db.username"));
//        codeParam.setDbPassword(properties.getProperty("db.password"));
//        codeParam.setTableName(properties.getProperty("table.name"));
//        codeParam.setPrimaryKey(properties.getProperty("table.primaryKey"));
//        codeParam.setAuthor(properties.getProperty("author", "系统生成"));
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        codeParam.setCreateDate(format.format(new Date()));
//        return codeParam;
//    }

//    /**
//     * 获取工程相关信息
//     * @return
//     * @throws IOException
//     */
//    public static ProjectParam getProjectParam() throws IOException {
//        Properties properties = new Properties();
//        InputStream inputStream = new FileInputStream(FileUtil.getRootPath() + "/src/main/resources/project.properties");
//        properties.load(inputStream);
//        inputStream.close();
//        ProjectParam projectParam = new ProjectParam();
//        projectParam.setArtifactId(properties.getProperty("project.artifactId"));
//        projectParam.setGroupName(properties.getProperty("project.group"));
//        projectParam.setAuthor(properties.getProperty("author"));
//        projectParam.setBasePackage(projectParam.getGroupName() + "." + projectParam.getArtifactId());
//        projectParam.setDbUrl(properties.getProperty("db.url"));
//        projectParam.setDbUsername(properties.getProperty("db.username"));
//        projectParam.setDbPassword(properties.getProperty("db.password"));
//        return projectParam;
//    }

    /**
     * 加载配置文件
     * 文件位置  classpath:system.properties
     *
     * @return
     * @throws IOException
     */
    public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
//        properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("system.properties"));//该方法会造成缓存，不会读取动态生成的数据
//        properties.load(PropertiesUtil.class.getClassLoader().getResource("system.properties").openStream());
        InputStream inputStream = new FileInputStream(new File(FileUtil.getProjectRootPath() + "/src/main/resources/assets/system.properties"));
        properties.load(inputStream);
        return properties;
    }

    /**
     * 加载数据库表信息
     * @return
     * @throws IOException
     */
    public static Map<String, String> loadTableInfo() throws IOException {
        Properties properties= new Properties();
        properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("table.properties"));

        Set<String> keySet = properties.stringPropertyNames();
        Map<String, String> res = new HashMap<>();
        for(String key: keySet){
            res.put(key, properties.getProperty(key).trim());
        }
        return res;
    }

    /**
     * 获取主键信息
     * @return
     * @throws IOException
     */
    public static Map<String, String> loadPkInfo() throws IOException {
        Properties properties = new Properties();
        properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("pk.properties"));

        Set<String> keySet = properties.stringPropertyNames();
        Map<String, String> pkInfo = new HashMap<>();
        for(String key: keySet){
            pkInfo.put(key, properties.getProperty(key));
        }
        return pkInfo;
    }
}
