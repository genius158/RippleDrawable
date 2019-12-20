package com.yan.demo;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author xiyouMc/chao.ma@quvideo.com @https://github.com/xiyouMc
 * @version 1.0
 * @since 1/24/19
 */
public class SecurityCheckUtil {
  private static final String TAG = "SecurityCheckUtil";
  private final String XPOSED_HELPERS = "de.robv.android.xposed.XposedHelpers";
  private final String XPOSED_BRIDGE = "de.robv.android.xposed.XposedBridge";

  private static class SingletonHolder {
    private static final SecurityCheckUtil singleInstance = new SecurityCheckUtil();
  }

  private SecurityCheckUtil() {
  }

  public static SecurityCheckUtil getInstance() {
    return SingletonHolder.singleInstance;
  }

  public boolean isXposedExists() {
    try {
      ClassLoader.getSystemClassLoader().loadClass(XPOSED_HELPERS).newInstance();
      ClassLoader.getSystemClassLoader().loadClass(XPOSED_BRIDGE).newInstance();
    } catch (ClassNotFoundException e) {
      VivaLog.e(TAG, "ClassNotFoundException", e);
      return false;
    } catch (InstantiationException e) {
      VivaLog.e(TAG, "InstantiationException", e);
      return true;
    } catch (IllegalAccessException e) {
      VivaLog.e(TAG, "IllegalAccessException", e);
      return true;
    }
    return true;
  }

  public boolean isXposedExistByThrow() {
    try {
      throw new Exception("gg");
    } catch (Exception e) {
      for (StackTraceElement stackTraceElement : e.getStackTrace()) {
        if (stackTraceElement.getClassName().contains(XPOSED_BRIDGE)) return true;
      }
      return false;
    }
  }

  public boolean tryShutdownXposed() {
    if (isXposedExistByThrow()) {
      Field xpdisabledHooks = null;
      try {
        xpdisabledHooks = ClassLoader.getSystemClassLoader()
            .loadClass(XPOSED_BRIDGE)
            .getDeclaredField("disableHooks");
        xpdisabledHooks.setAccessible(true);
        xpdisabledHooks.set(null, Boolean.TRUE);
        return true;
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
        return false;
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        return false;
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        return false;
      }
    } else {
      return true;
    }
  }

  public boolean isRoot() {
    int secureProp = getroSecureProp();
    if (secureProp == 0)//eng/userdebug版本，自带root权限
    {
      return true;
    } else {
      return isSUExist();//user版本，继续查su文件
    }
  }

  /**
   * adb 的root 权限是在system/core/adb/adb.c 中控制。
   * 主要根据ro.secure 以及 ro.debuggable 等system property 来控制。默认即档ro.secure 为0 时，即开启root
   * 权限，为1时再根据ro.debuggable 等选项来确认是否可以用开启root 权限。为此如果要永久性开启adb 的root 权限，有两种修改的方式:
   * 1. 修改system property ro.secure， 让ro.secure=0。
   * 2. 修改adb.c 中开启root 权限的判断逻辑。
   */
  private int getroSecureProp() {
    int secureProp;
    String roSecureObj = CommandUtil.getInstance().getProperty("ro.secure");
    if (roSecureObj == null) {
      secureProp = 1;
    } else {
      if ("0".equals(roSecureObj)) {
        secureProp = 0;
      } else {
        secureProp = 1;
      }
    }
    return secureProp;
  }

  public boolean isSUExist() {
    File file = null;
    String[] paths = {
        "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
        "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"
    };
    for (String path : paths) {
      file = new File(path);
      if (file.exists()) return true;
    }
    return false;
  }

  private static  class VivaLog {
    public static void e(String tag,String message,Exception e) {

    }
  }
}
