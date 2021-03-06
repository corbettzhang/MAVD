package cn.monkeyapp.mavd.common.manage;

import cn.monkeyapp.mavd.common.ProgressCallback;
import cn.monkeyapp.mavd.util.OsInfoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author zhangcong
 */
public class ExecuteHelper {

    private static final Logger LOGGER = LogManager.getLogger(ExecuteHelper.class);

    public void exec(String command, ProgressCallback callback) {
        if (OsInfoUtils.isWindows()) {
            executeCommand(command, callback);
        } else {
            try {
                executeCommandInBash(command, callback);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * execute comm
     * @param command command direct
     */
    public void executeCommand(String command, ProgressCallback callback) {
        LOGGER.log(Level.INFO, "Execute: " + command);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                callback.onProgressUpdate(line);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * bash方式执行命令
     *
     * @param shell    命令
     * @param callback 回调函数接口
     * @throws IOException IO异常
     */
    public void executeCommandInBash(String shell, ProgressCallback callback) throws IOException {
        BufferedReader br = null;
        //获取代表正在运行的Java虚拟机的名称。
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        try {
            LOGGER.log(Level.INFO, "Starting to exec{ " + shell + " }. PID is: " + pid);
            final Process process;
            final ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", shell);
            pb.environment();
            // 将错误流合并到标准流
            pb.redirectErrorStream(true);
            process = pb.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            final ProcessStreamGobbler localGob = new ProcessStreamGobbler(br, callback);
            ThreadPoolManager.getInstance().addThreadExecutor(localGob);
            process.waitFor();
        } catch (Exception ioe) {
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

}

class ProcessStreamGobbler implements Runnable {

    private BufferedReader br;
    private ProgressCallback callback;

    public ProcessStreamGobbler(BufferedReader br, ProgressCallback callback) {
        this.callback = callback;
        this.br = br;
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                progressUpdate(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void progressUpdate(String line) {
        callback.onProgressUpdate(line);
    }

}
