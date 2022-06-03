package cc.zdj.coder.controller;

import cc.zdj.coder.domain.ProjectInfo;
import cc.zdj.coder.service.CoderService;
import cc.zdj.coder.util.FileUtil;
import cc.zdj.coder.util.ProjectInfoHolder;
import cc.zdj.coder.util.RootPathHolder;
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
     * 脚手架代码生成接口
     *
     * @param response
     * @param projectInfo
     * @return void
     * @Throws
     * @Author zhangdj
     * @date 2022/3/3 10:46
     */
    @RequestMapping("/generate")
    public void generateCode(HttpServletResponse response, @RequestBody ProjectInfo projectInfo) throws Exception {
        // 检查参数信息
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(projectInfo.getTableDomainNodeList()), "数据库表信息不能为空");

        // 线程中 存入当前线程的信息
        ProjectInfoHolder.setProjectInfo(projectInfo);

        // 生成代码文件
        coderService.generate(projectInfo);

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
}
