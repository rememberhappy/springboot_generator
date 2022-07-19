package cc.zdj.coder.controller;

import cc.zdj.coder.domain.ProjectInfo;
import cc.zdj.coder.generatorFreemarker.MybatisFreemarkerTemplateEngine;
import cc.zdj.coder.service.CoderService;
import cc.zdj.coder.util.FileUtil;
import cc.zdj.coder.util.ProjectInfoHolder;
import cc.zdj.coder.util.RootPathHolder;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/coder")
public class CoderController {

    @Autowired
    CoderService coderService;

    /**
     * 脚手架代码生成接口，返回服务器上代码生成的路径
     *
     * @param projectInfo 请求参数
     * @return java.lang.String 服务器上代码生成的路径
     * @Author zhangdj
     * @date 2022/3/3 10:46
     */
    @RequestMapping("/generate")
    public String generateCode(@RequestBody ProjectInfo projectInfo) throws Exception {
        // 检查参数信息
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(projectInfo.getTableDomainNodeList()), "数据库表信息不能为空");

        // 线程中 存入当前线程的信息
        ProjectInfoHolder.setProjectInfo(projectInfo);

        // 生成代码文件
        String generatePath = coderService.generate(projectInfo, true);

        RootPathHolder.clean();
        return generatePath;
    }

    /**
     * 脚手架代码生成接口，返回ZIP文件包
     *
     * @param response    响应zip文件
     * @param projectInfo 请求参数
     * @Author zhangdj
     * @date 2022/3/3 10:46
     */
    @RequestMapping("/generateZip")
    public void generateCodeToZip(HttpServletResponse response, @RequestBody ProjectInfo projectInfo) throws Exception {
        // 检查参数信息
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(projectInfo.getTableDomainNodeList()), "数据库表信息不能为空");

        // 线程中 存入当前线程的信息
        ProjectInfoHolder.setProjectInfo(projectInfo);

        // 生成代码文件
        coderService.generate(projectInfo, false);

        // 打包文件，返回地址
        String zipPath = coderService.packageFile();

        // 处理下载
        response.setContentType("application/zip;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((projectInfo.getArtifactId() + "-server.zip").getBytes(), StandardCharsets.ISO_8859_1));
        OutputStream os = response.getOutputStream();
        InputStream is = new BufferedInputStream(new FileInputStream(zipPath));
        byte[] buffer = new byte[1024];

        while (is.read(buffer) != -1) {
            os.write(buffer);
        }
        os.close();
        is.close();
        RootPathHolder.clean();

        // 删除本地生成的文件
        String targetFolder = FileUtil.getTargetFolder();
        FileUtil.deleteFile(new File(targetFolder).getParentFile());
    }

    // 第二种方式生成代码
    public static void main(String[] args) {
        // 全局策略配置
        GlobalConfig globalConfig = GeneratorBuilder.globalConfigBuilder()
                // 覆盖已生成文件
                .fileOverride()
                // 生成后是否打开生成目录
//                .openDir(true)
                // 指定输出目录 默认值: windows:D:// linux or mac : /tmp
                .outputDir("D:\\output")
                // 生成swagger注解
                .enableSwagger()
                // 作者名
                .author("zhangdj")
                // 时间策略
                .dateType(DateType.TIME_PACK)
                // 注释日期格式
                .commentDate("yyyy-MM-dd")
                // 禁止打开文件夹
                .disableOpenDir()
                .build();

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig
                .Builder("jdbc:mysql://192.168.9.225:3307/marketing?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai", "root", "Wdcloud998")// 驱动连接的URL、数据库连接用户名、数据库连接密码
                // 类型转换,数据库=》JAVA类型
                .typeConvert(new MySqlTypeConvert())
                // 关键字处理 ,这里选取了mysql5.7文档中的关键字和保留字（含移除）
                .keyWordsHandler(new MySqlKeyWordsHandler())
                // 数据库信息查询类,默认由 dbType 类型决定选择对应数据库内置实现
                .dbQuery(new MySqlQuery())
                // 数据库 schema name
                .schema("mybatis-plus")
                .build();

        // 包配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                // 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
                .parent("cc.wd.com")
                // 父包模块名
                .moduleName("user")
                .build();

        // 配置模板
        //TemplateConfig templateConfig = new TemplateConfig.Builder().disable().build();//激活所有默认模板

//        // 添加以上配置到AutoGenerator中
//        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig); // 数据源配置
//        autoGenerator.global(globalConfig); // 全局策略配置
//        autoGenerator.packageInfo(packageConfig);    // 包配置
//        //autoGenerator.template(templateConfig); // 配置模板
//        // 生成代码
//        autoGenerator.execute();

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig.Builder()
                .controller("/assets/freemarker/controller.java")
                //指定自定义模板路径, 位置：/resources/templates/entity2.java.ftl(或者是.vm)
                //注意不要带上.ftl(或者是.vm), 会根据使用的模板引擎自动识别
                .build();

        // 添加以上配置到AutoGenerator中
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig); // 数据源配置
        autoGenerator.global(globalConfig); // 全局策略配置
        autoGenerator.packageInfo(packageConfig);    // 包配置
        autoGenerator.template(templateConfig); // 配置模板
        // 指定引擎 生成代码
        autoGenerator.execute(new MybatisFreemarkerTemplateEngine());
    }

}
