package cc.wdcloud.coder;

import cc.wdcloud.coder.util.FileUtil;
import cc.wdcloud.coder.util.RootPathHolder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置信息。获取脚手架生成的目录
 */
@Component
@Data
public class Config {

    @Value("${spring.profiles.active:local}")
    private String env;

    @Value("${bugu.path}")
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
