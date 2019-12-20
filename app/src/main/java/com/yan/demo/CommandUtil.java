package com.yan.demo;

/**
 * @author xiyouMc/chao.ma@quvideo.com @https://github.com/xiyouMc
 * @version 1.0
 * @since 1/28/19
 */
public class CommandUtil {
    private CommandUtil() {
    }

    private static class SingletonHolder {
        private static CommandUtil INSTANCE = new CommandUtil();
    }

    public static CommandUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getProperty(String propName) {
        String value = null;
        Object roSecureObj;
        try {
            roSecureObj = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, propName);
            if (roSecureObj != null) value = (String) roSecureObj;
        } catch (Exception e) {
            value = null;
        } finally {
            return value;
        }
    }
}
