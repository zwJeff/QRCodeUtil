package com.jeff.utils;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * 说明： 应用请求root权限的util
 * 作者： 张武
 * 日期： 2016/11/15.
 * email:wuzhang4@creditease.cn
 */


public class RootUtil {

    /**
     * 申请root权限
     *
     * @param pkgCodePath getPackageCodePath( )
     * @return
     */
    public static boolean applyForRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }


    /**
     * 检查root权限
     */
    public static boolean checkRootPermission() {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf("echo test");
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            int result = localProcess.exitValue();
            if (result != -1)
                return true;
            else
                return false;
        } catch (Exception localException) {
            localException.printStackTrace();
            return false;
        }
    }


}
