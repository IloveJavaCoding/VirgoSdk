package com.nepalese.virgosdk.Manager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class RuntimeExec {
    public static final String INSTALL = "pm install -rl ";
    public static final String UNINSTALL = "pm uninstall ";
    public static final String AM = "am start ";
    public static final String CP = "cp ";

    private static volatile RuntimeExec instance;
    private Runtime runtime = Runtime.getRuntime();

    private RuntimeExec() {
    }

    public static RuntimeExec getInstance() {
        if (instance == null) {
            synchronized(RuntimeExec.class) {
                if (instance == null) {
                    instance = new RuntimeExec();
                }
            }
        }

        return instance;
    }

    //一般命令
    public void executeCommand(String... command) {
        InputStream inputStream = this.executeCommandGetInputStream(command);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }
    }

    //执行需root权限命令
    public void executeRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command);
            os.writeBytes("\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                assert process != null;
                process.destroy();
            } catch (Exception ignored) {
            }
        }
    }

    //截屏
    public static void takeScreenShot(String file) {
        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("screencap -p " + file);
            os.flush();
            process.waitFor();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                assert process != null;
                process.destroy();
            } catch (Exception ignored) {
            }
        }
    }

    private InputStream executeCommandGetInputStream(String... command) {
        try {
            Process process = command.length == 1 ? this.runtime.exec(command[0]) : this.runtime.exec(command);
            (new StreamGobbler(process.getErrorStream())).start();
            return process.getInputStream();
        } catch (IOException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    static class StreamGobbler extends Thread {
        InputStream mInputStream;

        public StreamGobbler(InputStream is) {
            this.mInputStream = is;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(this.mInputStream);
                BufferedReader br = new BufferedReader(isr);
                while(true) {
                    br.readLine();
                }
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }
    }
}
