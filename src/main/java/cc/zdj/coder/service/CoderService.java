package cc.zdj.coder.service;

import cc.zdj.coder.Config;
import cc.zdj.coder.domain.ProjectInfo;
import cc.zdj.coder.domain.TableDomainNode;
import cc.zdj.coder.dto.DomainDto;
import cc.zdj.coder.dto.MapperDto;
import cc.zdj.coder.dto.MybatisGenDto;
import cc.zdj.coder.util.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 代码生成的 service
 */
@Service
public class CoderService {

    // 日志
    private final Logger logger = LoggerFactory.getLogger(CoderService.class);

    // 配置信息
    @Autowired
    Config config;

    public String generate(ProjectInfo projectInfo) throws Exception {
        String url = projectInfo.getUrl();
        if (StringUtils.isBlank(url)) {
            throw new Exception("数据库URL不能为空");
        } else if (!url.contains("useSSL")) {
            // 兼容数据库版本8
            if (url.contains("?")) {
                projectInfo.setUrl(url + "&useSSL=false");
            } else {
                projectInfo.setUrl(url + "?useSSL=false");
            }
        }
        String path = config.getPath();

        // 文件工具类   获取脚手架生成的代码的存放路径
        String targetFolder = FileUtil.getTargetSourcePath();
        logger.info("脚手架生成的代码的存放路径{}", targetFolder);

        // 根据路径生成文件对象
        File file = new File(targetFolder);
        // 判断文件是否存在，不存在的话生成目录
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new Exception("生成文件目录出错");
            }
        }

        MybatisGenDto mybatisGenDto;
        // 获取 数据库表信息 集合节点信息
        for (TableDomainNode node : projectInfo.getTableDomainNodeList()) {
            // 获取表名和实体类名
            String tableName = node.getTableName();
            String domainName = node.getDomainName();

            mybatisGenDto = new MybatisGenDto();
            // 生成 dao层 包路径
            mybatisGenDto.setDaoPackage(PackageUtil.getDaoPackage(projectInfo, domainName));
            mybatisGenDto.setDbUrl(projectInfo.getUrl());
            mybatisGenDto.setUsername(projectInfo.getUsername());
            mybatisGenDto.setPassword(projectInfo.getPassword());
            mybatisGenDto.setTableName(tableName);
            mybatisGenDto.setDomainName(domainName);
            // 生成 domain（实体类）层类全路径，mapper文件中使用
            mybatisGenDto.setFullDomainName(PackageUtil.getFullDomainName(projectInfo, domainName));
            // 生成 domain（实体类）层包路径
            mybatisGenDto.setDomainPackage(PackageUtil.getDomainPackage(projectInfo, domainName));
            mybatisGenDto.setPath(path);
            // 脚手架生成的代码的存放路径
            mybatisGenDto.setTargetProjectFolder(targetFolder);
            logger.info("生成Mybatis配置文件所需要的参数：{}", JSON.toJSONString(mybatisGenDto, true));

            // 生成 mybatis 的配置文件
            String configPath = createMybatisGenerateConfiguration(mybatisGenDto);

            // 生成mapper.xml，生成mapper.java和对应的domain（实体类）。生成的方法是第三方的包中自定义的方法
            generateByMybatisGenerator(configPath);

            // 根据第三方生成的文件内容进行修改。生成controller，service，dao，pom.xml，启动类等文件。创建工程文件，包含压缩文件
            createProjectFile(projectInfo, mybatisGenDto);
        }
        return FileUtil.getTargetFolder();
    }

    /**
     * 生成mybatis的配置文件。用于第三方工具的执行
     *
     * @param mybatisGenDto mybatis-generator-template.vm中的值获取
     * @return java.lang.String 生成mybatis-generator-template.xml的路径
     * @Author zhangdj
     * @date 2022/3/5 12:16
     */
    public String createMybatisGenerateConfiguration(MybatisGenDto mybatisGenDto) throws IOException, IllegalAccessException {
        String rootPath = FileUtil.getProjectRootPath();
        String generatorTempXmlPath = "src/main/resources/mybatis-generator-template.xml".replaceAll("/", Matcher.quoteReplacement(FileUtil.SEPARATOR));
        String targetPath = rootPath + FileUtil.SEPARATOR + generatorTempXmlPath;
        FileUtil.generateFileFromVm("assets/mybatis-generator-template.vm", targetPath, mybatisGenDto);
        return targetPath;
    }

    /**
     * 使用mybatis生成代码，官网提供。使用会生成 实体类，dao接口和对应的mapper.xml文件
     *
     * @param configPath mybatis-generator的XML配置文件路径
     * @Author zhangdj
     * @date 2022/3/5 12:18
     */
    public void generateByMybatisGenerator(String configPath) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        // 运行生成配置文件
        List<String> warnings = new ArrayList<>();
        File mybatisConf = new File(configPath);
        ConfigurationParser parser = new ConfigurationParser(warnings);
        Configuration configuration = parser.parseConfiguration(mybatisConf);

        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);

        ProgressCallback progressCallback = new VerboseProgressCallback();
        myBatisGenerator.generate(progressCallback);
    }

    /**
     * 生成工程文件
     *
     * @param projectInfo   参数信息，用来获取表名和实体类名，坐标等信息
     * @param mybatisGenDto mybatis第三方工具的信息
     * @Author zhangdj
     * @date 2022/3/5 12:19
     */
    public void createProjectFile(ProjectInfo projectInfo, MybatisGenDto mybatisGenDto) throws IOException, IllegalAccessException {

        // 配置文件处理，生成增删改文件，以及查询和批量插入文件
        String mapperPath = mybatisGenDto.getTargetProjectFolder() + "/mapper/" + mybatisGenDto.getDomainName() + "Mapper.xml";
        logger.info("mapper.xml路径为： {}", mapperPath);
        InputStream inputStream = new FileInputStream(mapperPath);
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        logger.info("content: {}", content);
        inputStream.close();
        // 获得实体类中的字段
        List<String> fieldList = DomainUtil.getFieldList(content);

        DomainDto domainDto = new DomainDto(projectInfo, mybatisGenDto);
        // 获取sql信息
        MapperDto mapperDto = MapperUtil.generateMapperTemplate(content);
        mapperDto.setFullDaoName(String.format("%s.%s", domainDto.getDaoPackage(), domainDto.getDaoName()));
        mapperDto.setFullDomainName(mybatisGenDto.getFullDomainName());
        mapperDto.setTableName(mybatisGenDto.getTableName());

        // 生成两个mapper文件
        MapperUtil.generateXmlFile(mapperDto, domainDto);

        // 生成对应的dao文件
        String daoPath = PackageUtil.getDaoFile(projectInfo, domainDto.getDomainName());
        FileUtil.generateFileFromVm("assets/boot/Dao.java.vm", daoPath, domainDto);

        // 生成 逻辑层类 文件，逻辑层接口由 pom 中引入的公共的BaseService中实现
        String servicePath = PackageUtil.getServiceFile(projectInfo, domainDto.getDomainName());
        FileUtil.generateFileFromVm("assets/boot/Service.java.vm", servicePath, domainDto);

        // 生成 控制层类 文件
        String controllerPath = PackageUtil.getControllerFile(projectInfo, domainDto.getDomainName());
        FileUtil.generateFileFromVm("assets/boot/Api.java.vm", controllerPath, domainDto);

        // 生成springBoot 启动类文件
        String applicationJavaPath = PackageUtil.getRootApplicationFile(projectInfo);
        FileUtil.generateFileFromVm("assets/Application.java.vm", applicationJavaPath, domainDto);

        // 生成 yml 配置文件
        String ymlPath = PackageUtil.getYmlPath();
        FileUtil.generateFileFromVm("assets/application.yml.vm", ymlPath, projectInfo);

        // 生成 pom 文件
        String pomPath = PackageUtil.getPomPath();
        FileUtil.generateFileFromVm("assets/pom.xml.vm", pomPath, projectInfo);

        Param param = new Param();
        param.setFieldList(fieldList);
        param.setObjName(domainDto.getObjName());
        param.setArtifactId(projectInfo.getArtifactId().toLowerCase());

//        Integer vueVersion = projectInfo.getVueVersion();
//        if (null == vueVersion) {
//            vueVersion = 3;
//        }
//
//        String listPath = PackageUtil.getListPath(domainDto.getDomainName(), projectInfo.getArtifactId());
//        if (vueVersion == 3) {
//            FileUtil.generateFileFromVm("assets/fe/list.vue3.vm", listPath, param);
//        } else {
//            FileUtil.generateFileFromVm("assets/fe/list.vue.vm", listPath, param);
//        }
//
//        String editPath = PackageUtil.getEditPath(domainDto.getDomainName(), projectInfo.getArtifactId());
//        if (vueVersion == 3) {
//            FileUtil.generateFileFromVm("assets/fe/edit.vue3.vm", editPath, param);
//        } else {
//            FileUtil.generateFileFromVm("assets/fe/edit.vue.vm", editPath, param);
//        }
//
//        String listStylPath = PackageUtil.getListStylPath(domainDto.getDomainName(), projectInfo.getArtifactId());
//        FileUtil.generateFileFromVm("assets/fe/list.scss.vm", listStylPath, param);
//
//        String editStylPath = PackageUtil.getEditStylPath(domainDto.getDomainName(), projectInfo.getArtifactId());
//        FileUtil.generateFileFromVm("assets/fe/edit.scss.vm", editStylPath, param);
//
//        String routerJsPath = PackageUtil.getRouterJsPath(projectInfo.getArtifactId());
//        FileUtil.generateFileFromVm("assets/fe/router.js.vm", routerJsPath, projectInfo);

        String ignorePath = PackageUtil.getGitIgnorePath();
        FileUtil.generateFileFromVm("assets/gitignore.vm", ignorePath, param);

        // 将第三方 生成的 mapper.xml和dao层的mapper.java
        // 删除mapper目录
        FileUtil.deleteFile(new File(String.format("%s/mapper", FileUtil.getTargetSourcePath())));
        // 删除生成的mapper.java文件
        String info = String.format("%s/%s/%sMapper", FileUtil.getTargetSourcePath(), mybatisGenDto.getDaoPackage().replaceAll("\\.", "/"), mybatisGenDto.getDomainName());
        boolean delete = new File(info + ".java").delete();
        logger.info("删除【第三方 生成的 mapper.xml和dao层的mapper.java】: {}，删除路径为：{}", (delete ? "成功" : "失败"), info);
    }

    /**
     * 打包文件，将文件夹打包成一个 zip 压缩包
     *
     * @return java.lang.String zip压缩包路径
     * @Throws
     * @Author zhangdj
     * @date 2022/3/4 15:53
     */
    public String packageFile() throws IOException {
        // 打包工程文件
        String srcPath = new File(FileUtil.getTargetFolder()).getParent();
        String targetZip = String.format("%s/%s-server.zip", srcPath, ProjectInfoHolder.getProjectInfo().getArtifactId());
        OutputStream os = new FileOutputStream(targetZip);
        ZipUtils.toZip(FileUtil.getTargetFolder(), os, true);
        os.flush();
        os.close();
        logger.info("打包下载的文件地址: {}", targetZip);
        return targetZip;
    }

//    public static void main(String[] args) throws InterruptedException, SQLException, InvalidConfigurationException, XMLParserException, IOException, IllegalAccessException {
//        new CoderService().generateByMybatisGenerator("D:/code/wecoder-generator/src/main/resources/mybatis-generator-template.xml");
//        List<String> fieldList = new ArrayList<>();
//        fieldList.add("id");
//        fieldList.add("name");
//        Map<String, Object> param = new HashMap<>();
//        param.put("objName", "user");
//        param.put("fieldList", fieldList);
//        Param item = new Param();
//        item.setFieldList(fieldList);
//        item.setObjName("admin");
//
////        生成vue
//        String vuePath = "d:/code/wecoder-generator/generator/pages/admin/list.vue3.vm.vm.vm";
//        FileUtil.generateFileFromVm("assets/page/list.vue.vm", vuePath, item);
//
//        String jsPath = "d:/code/wecoder-generator/generator/pages/admin/api.js";
//        FileUtil.generateFileFromVm("assets/page/admin.js.vm", jsPath, item);
//    }
}
