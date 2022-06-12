package cc.zdj.coder;

import cc.zdj.coder.util.FileUtil;
import cc.zdj.coder.util.RootPathHolder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置信息。获取脚手架生成的目录。根据不同的环境获取不同的配置
 */
@Component
@Data
public class Config {

    @Value("${spring.profiles.active:local}")
    private String env;

    @Value("${generate.path:D://}")
    private String path;

    public String getPath() {
        String res = path;
        if (!"server".equals(env)) {
            // 项目的 根目录
            res = FileUtil.getProjectRootPath();
        }
        RootPathHolder.setRootPath(res);
        return res;
    }
}
