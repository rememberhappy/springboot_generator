package cc.wdcloud.coder.util;

/**
 * 生成的 脚手架 代码存放的路径
 */
public class RootPathHolder {
    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static String getRootPath() {
        return holder.get();
    }

    public static void setRootPath(String rootPath) {
        holder.set(rootPath);
    }

    public static void clean() {
        holder.set("");
    }
}
