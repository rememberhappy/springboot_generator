package cc.zdj.coder.util;

import cc.zdj.coder.domain.ProjectInfo;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.plexus.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

/**
 * 文件处理帮助类
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    // win和Linux的文件分隔符不一样
    public static final String SEPARATOR = File.separator;

    static ThreadLocal<String> targetHolder = new ThreadLocal<>();

    /**
     * 根据模板生成文件
     *
     * @param vmPath     vm文件位置
     * @param targetFile 目标文件位置，需要定位后缀
     */
    public static void generateFileFromVm(String vmPath, String targetFile, Object templateObj) throws IllegalAccessException, IOException {
        // 处理不同系统之间的 文件分割符
        vmPath = vmPath.replaceAll("/", Matcher.quoteReplacement(FileUtil.SEPARATOR));
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty(Velocity.ENCODING_DEFAULT, "utf-8");
        ve.setProperty(Velocity.INPUT_ENCODING, "utf-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "utf-8");
        ve.init();
        Template template = null;
        try {
            template = ve.getTemplate(vmPath);
        } catch (Exception e) {
            logger.error("生成template失败", e);
            logger.error("发生异常，系统退出。。。");
            System.exit(0);
        }
        VelocityContext context = new VelocityContext();
        context.put("doller", "$");
        if (null != templateObj) {
            List<Field> fieldList = ReflectionUtils.getFieldsIncludingSuperclasses(templateObj.getClass());
            if (CollectionUtils.isNotEmpty(fieldList)) {
                for (Field field : fieldList) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object value = field.get(templateObj);
                    context.put(fieldName, value);
                }
            }
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        File file = new File(targetFile);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        Writer fileWriter = new FileWriter(new File(targetFile));
        IOUtils.write(writer.toString(), fileWriter);
        fileWriter.flush();
        fileWriter.close();
        writer.close();
    }

    /**
     * 获取工程根目录
     */
    public static String getProjectRootPath() {
        String path = FileUtil.class.getClassLoader().getResource("").getPath();
        // 处理在不同系统下 获取的路径，并进行转换，转换为和系统一致。【windows获取的路径：/D:/IntelliJ%20IDEA%202022.1/projects/springboot_generator/target/classes/ -> windows的路径分隔符应为 \ 】
        if (path.contains("/")) {
            path = path.replaceAll("/", Matcher.quoteReplacement(SEPARATOR));
        } else {
            path = path.replaceAll("\\\\", Matcher.quoteReplacement(SEPARATOR));
        }
        if (path.endsWith(SEPARATOR)) {
            // 去掉路径的最后一个 分隔符。\E:\JetBrains\IntelliJ%20IDEA%202020.3.3\IdeaProjects\springboot_generator\target\classes\
            path = path.substring(0, path.length() - 1);
        }
        // 去掉最后一层路径  \E:\JetBrains\IntelliJ%20IDEA%202020.3.3\IdeaProjects\springboot_generator\target
        String rootPath = path.substring(0, path.lastIndexOf(SEPARATOR));
        // 去掉最后一层路径  \E:\JetBrains\IntelliJ%20IDEA%202020.3.3\IdeaProjects\springboot_generator
        return rootPath.substring(0, rootPath.lastIndexOf(SEPARATOR));
    }

    public static Boolean deleteMapper() {
        String mapperDir = getSourcePath() + SEPARATOR + "mapper";
        return deleteFile(new File(mapperDir));
    }

    /**
     * 删除文件，连带删除子文件
     *
     * @param file 要删除的文件
     * @return boolean
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 16:09
     */
    public static boolean deleteFile(File file) {
        boolean result = true;
        if (file == null || !file.exists()) {
            logger.warn("要删除的file不存在");
            return result;
        }
        if (file.isFile() || file.listFiles() == null) {
            result = file.delete();
            logger.info("要删除的文件路径为： {}，删除结果： {}", file.getPath(), result);
        } else {
            File[] files = file.listFiles();
            if (!ArrayUtils.isEmpty(files)) {
                for (File item : files) {
                    deleteFile(item);
                }
            }
            result = file.delete();
            logger.info("要删除的目录路径为：{}，删除结果： {}", file.getPath(), result);
        }
        return result;
    }

    public static String getTargetResourcesPath() {
        return String.format("%s%s%s%s%s%s%s", getTargetFolder(), SEPARATOR, "src", SEPARATOR, "main", SEPARATOR, "resources");
    }

    /**
     * 获取java source路径
     */
    public static String getSourcePath() {
        return String.format("%s%s%s%s%s%s%s%s%s", getProjectRootPath(), SEPARATOR, "generator", SEPARATOR, "src", SEPARATOR, "main", SEPARATOR, "java");
    }

    /**
     * 获取 生成代码的存放 路径
     * 代码生成指定的文件地址(配置信息中配置的目录地址)/generator/当前时间的毫秒数-1000以内的随机数/项目的坐标-server/src/main/java
     */
    public static String getTargetSourcePath() {
        return String.format("%s%s%s%s%s%s%s", getTargetFolder(), SEPARATOR, "src", SEPARATOR, "main", SEPARATOR, "java");
    }

    /**
     * 存放生成脚手架的目录地址
     * 地址：代码生成指定的文件地址(配置信息中配置的目录地址)/generator/当前时间的毫秒数-1000以内的随机数/项目的坐标-server
     */
    public static String getTargetFolder() {
        String targetFolder = targetHolder.get();
        if (StringUtils.isNotBlank(targetFolder)) {
            return targetFolder;
        }
        ProjectInfo projectInfo = ProjectInfoHolder.getProjectInfo();
        Preconditions.checkArgument(null != projectInfo);
        Preconditions.checkArgument(null != projectInfo.getArtifactId());
        String path = String.format("%s%s%s%s%d-%d%s%s-server", RootPathHolder.getRootPath(), SEPARATOR, "generator", SEPARATOR,
                System.currentTimeMillis(), new Random().nextInt(1000), SEPARATOR, projectInfo.getArtifactId());
        // 地址：代码生成指定的文件地址(配置信息中配置的目录地址)/generator/当前时间的毫秒数-1000以内的随机数/项目的坐标-server
        targetHolder.set(path);
        return path;
    }

    /**
     * 创建指定包名所在的文件夹
     *
     * @param packageInfo
     */
    public static void createDir(String packageInfo) {
        String sourcePath = getTargetSourcePath();
        String res = sourcePath + "." + packageInfo;
        res = res.replaceAll("\\.", SEPARATOR);
        File file = new File(res);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getPackagePath(String packageInfo) {
        String sourcePath = getSourcePath();
        String res = String.format("%s.%s", sourcePath, packageInfo);

        res = res.replaceAll("\\.", SEPARATOR);
        return res;
    }

    public static String getFilePath(String className, String suffix) {
        String res = className.replaceAll("\\.", SEPARATOR);
        return String.format("%s.%s", res, suffix);
    }

}
