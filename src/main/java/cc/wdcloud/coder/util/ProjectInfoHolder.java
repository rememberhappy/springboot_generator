package cc.wdcloud.coder.util;

import cc.wdcloud.coder.domain.ProjectInfo;

/**
 * 线程中存入参数信息
 */
public class ProjectInfoHolder {
    private static final ThreadLocal<ProjectInfo> holder = new ThreadLocal<>();

    public static ProjectInfo getProjectInfo() {
        return holder.get();
    }

    public static void setProjectInfo(ProjectInfo projectInfo) {
        holder.set(projectInfo);
    }
}
