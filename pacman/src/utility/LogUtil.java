/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogUtil {

    private void LogUtils() {
        // 私有构造函数，防止实例化
    }

    public static void writeToLogFile(String message,String currentFileName) {
        try {
            String logDirectoryName = "logDocument"; // 日志文件夹名称
            File logDirectory = new File(System.getProperty("user.dir"), logDirectoryName);
            if (!logDirectory.exists()) {
                logDirectory.mkdir(); // 创建日志文件夹
            }

            String logFileName = currentFileName + "_log.txt"; // 日志文件名为当前文件名加上后缀.log
            File logFile = new File(logDirectory, logFileName);

            FileWriter writer = new FileWriter(logFile, true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
