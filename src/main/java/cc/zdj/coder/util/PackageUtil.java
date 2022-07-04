package cc.zdj.coder.util;

import cc.zdj.coder.domain.ProjectInfo;

import java.util.regex.Matcher;

/**
 * 包工具类，为不同的类生成不同的包名称
 */
public class PackageUtil {

    // 根据参数生成包路径 group值.artifactId值
    public static String getBasePackage(ProjectInfo projectInfo) {
        String artifactId = projectInfo.getArtifactId();
        if (artifactId.contains("-")) {
            artifactId = artifactId.replaceAll("-", "");
        }
        return String.format("%s.%s", projectInfo.getGroup(), artifactId);
    }

    // 生成 dao层包路径：group值.artifactId值.实体类名小写.dao
    public static String getDaoPackage(ProjectInfo projectInfo, String domainName) {
        String res = String.format("%s.%s.dao", getBasePackage(projectInfo), domainName.toLowerCase());
        return res.toLowerCase();
    }

    // 生成 domain（实体类）层包路径：group值.artifactId值.实体类名小写.domain
    public static String getDomainPackage(ProjectInfo projectInfo, String domainName) {
        String res = String.format("%s.%s.domain", getBasePackage(projectInfo), domainName.toLowerCase());
        return res.toLowerCase();
    }

    // 生成 DTO 层包路径：group值.artifactId值.实体类名小写.dto
    public static String getDtoPackage(ProjectInfo projectInfo, String domainName) {
        String res = String.format("%s.%s.dto", getBasePackage(projectInfo), domainName.toLowerCase());
        return res.toLowerCase();
    }

    // 生成 domain（实体类）层类路径：group值.artifactId值.实体类名小写.domain.实体类名
    public static String getFullDomainName(ProjectInfo projectInfo, String domainName) {
        return String.format("%s.%s", getDomainPackage(projectInfo, domainName), domainName);
    }

    // 生成 service（逻辑层）层包路径：group值.artifactId值.实体类名小写.service
    public static String getServicePackage(ProjectInfo projectInfo, String domainName) {
        String res = String.format("%s.%s.service", getBasePackage(projectInfo), domainName.toLowerCase());
        return res.toLowerCase();
    }

    // 生成 controller（控制层）层包路径：group值.artifactId值.实体类名小写.controller
    public static String getControllerPackage(ProjectInfo projectInfo, String domainName) {
        String res = String.format("%s.%s.controller", getBasePackage(projectInfo), domainName.toLowerCase());
        return res.toLowerCase();
    }

    public static String getDaoFile(ProjectInfo projectInfo, String domainName) {
        String fullName = String.format("%s%s%s%s%sDao", FileUtil.getTargetSourcePath(), FileUtil.SEPARATOR, getDaoPackage(projectInfo, domainName).replaceAll("\\.", Matcher.quoteReplacement(FileUtil.SEPARATOR)), FileUtil.SEPARATOR, domainName);
        return fullName + ".java";
    }

    public static String getDomainFile(ProjectInfo projectInfo, String domainName) {
        String fullName = String.format("%s.%s.%s", FileUtil.getTargetSourcePath(), getDomainPackage(projectInfo, domainName).replaceAll("\\.", "/"), domainName);
        return fullName + ".java";
    }

    public static String getServiceFile(ProjectInfo projectInfo, String domainName) {
        String fullName = String.format("%s%s%s%s%sService", FileUtil.getTargetSourcePath(), FileUtil.SEPARATOR, getServicePackage(projectInfo, domainName).replaceAll("\\.", Matcher.quoteReplacement(FileUtil.SEPARATOR)), FileUtil.SEPARATOR, domainName);
        return fullName + ".java";
    }

    public static String getControllerFile(ProjectInfo projectInfo, String domainName) {
        String fullName = String.format("%s%s%s%s%sController", FileUtil.getTargetSourcePath(), FileUtil.SEPARATOR, getControllerPackage(projectInfo, domainName).replaceAll("\\.", Matcher.quoteReplacement(FileUtil.SEPARATOR)), FileUtil.SEPARATOR, domainName);
        return fullName + ".java";
    }

    public static String getRootApplicationFile(ProjectInfo projectInfo) {
        String fullName = String.format("%s%s%s%sApplication", FileUtil.getTargetSourcePath(), FileUtil.SEPARATOR, getBasePackage(projectInfo).replaceAll("\\.", Matcher.quoteReplacement(FileUtil.SEPARATOR)), FileUtil.SEPARATOR);
        return fullName + ".java";
    }

    public static String getYmlPath() {
        String path = String.format("%s%sapplication", FileUtil.getTargetResourcesPath(), FileUtil.SEPARATOR);
        return path + ".yml";
    }

    public static String getPomPath() {
        String path = String.format("%s%spom", FileUtil.getTargetFolder(), FileUtil.SEPARATOR);
        return path + ".xml";
    }

    public static String getGitIgnorePath() {
        return FileUtil.getTargetFolder() + FileUtil.SEPARATOR + ".gitignore";
    }

    /**
     * 获取自动生成的mapper路径
     *
     * @return java.lang.String 生成的路径
     * @Author zhangdj
     * @date 2022/3/4 16:21
     */
    public static String getGenMapperPath() {
        return String.format("%s/%s", FileUtil.getSourcePath(), "mapper");
    }

    public static String getListPath(String domainName, String artifactId) {
        String path = String.format("%s/pages/%s/%s/list", FileUtil.getTargetResourcesPath(), artifactId, domainName).toLowerCase();
        return path.replaceAll("\\.", "/") + ".vue";
    }

    public static String getListStylPath(String domainName, String artifactId) {
        String path = String.format("%s/pages/%s/%s/css/list", FileUtil.getTargetResourcesPath(), artifactId, domainName).toLowerCase();
        return path.replaceAll("\\.", "/") + ".scss";
    }

    public static String getEditPath(String domainName, String artifactId) {
        String path = String.format("%s/pages/%s/%s/edit", FileUtil.getTargetResourcesPath(), artifactId, domainName).toLowerCase();
        return path.replaceAll("\\.", "/") + ".vue";
    }

    public static String getEditStylPath(String domainName, String artifactId) {
        String path = String.format("%s/pages/%s/%s/css/edit", FileUtil.getTargetResourcesPath(), artifactId, domainName).toLowerCase();
        return path.replaceAll("\\.", "/") + ".scss";
    }

    public static String getRouterJsPath(String artifactId) {
        String path = String.format("%s/pages/%s/", FileUtil.getTargetResourcesPath(), artifactId).toLowerCase();
        return path.replaceAll("\\.", "/") + artifactId.toLowerCase() + ".router.js";
    }

    public static String getJsPath(String domainName) {
        String path = String.format("%s/pages/%s/%s", FileUtil.getTargetResourcesPath(), domainName,
                domainName.substring(0, 1) + domainName.substring(1));
        return path.replaceAll("\\.", "/") + ".js";
    }
}
