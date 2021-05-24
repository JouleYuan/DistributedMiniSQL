package cn.edu.zju.minisql.distributed.server.region;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPTransferor {
    public static class FTPTransferorConfig {
        // 需要所有region server都一致
        //public static final String userName = "anonymous";
        public static final String userName = "anonymous";
        public static final String password = "";
        public static final int port = 21;
        public static final int maxRetry = 5;
    }

    private static class MiniSQLTableFiles {
        public ArrayList<InputStream> fileStreams = new ArrayList<>();
        public ArrayList<String> fileNames = new ArrayList<>();
        public boolean ok = false;

        public MiniSQLTableFiles(String tableName) {
            String prefix = System.getProperty("user.dir") + File.separator + Config.Minisql.path;

            try {
                // tableName
                fileStreams.add(new FileInputStream(
                        prefix + tableName
                ));
                fileNames.add(tableName);

                ok = true;
            } catch (final IOException ioe) {
                close();
                ioe.printStackTrace();
                System.err.println("Local file doesn't exist");
            }
        }

        public void close() {
            try {
                for (InputStream fileStream : fileStreams) {
                    fileStream.close();
                }
            } catch (final IOException ioee) {
                ioee.printStackTrace();
            }
        }
    }


    public static boolean fromLocalFTPto(String tableName, String ip, String path) {
        // 读取本地文件
        MiniSQLTableFiles files = new MiniSQLTableFiles(tableName);
        if (!files.ok)
            return false;

        // 暂不使用SFTP客户端
        final FTPClient client = new FTPClient();

        // 连接
        try {
            client.connect(ip, FTPTransferorConfig.port);
            System.out.println("Connect to " + ip + ":" + FTPTransferorConfig.port);

            final int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                System.err.println("FTP server refused connection");
                return false;
            }
        } catch (final IOException ioe) {
            // 释放资源
            files.close();

            // 断连
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (final IOException ioee) {
                    ioee.printStackTrace();
                }
            }
            System.err.println("Could not connect to " + ip + ":" + FTPTransferorConfig.port);
            ioe.printStackTrace();
            return false;
        }

        try {
            int retry = 0;

            // 登录
            while (retry != FTPTransferorConfig.maxRetry) {
                if (!client.login(FTPTransferorConfig.userName, FTPTransferorConfig.password)) {
                    client.logout();
                    retry++;
                } else
                    break;
            }
            if (retry == FTPTransferorConfig.maxRetry) {
                System.err.println("Login for " + FTPTransferorConfig.maxRetry + " times, still failed");
                return false; // 达到重试上限
            }

            // 配置
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();  // 使用被动模式
            client.setControlEncoding("UTF-8");

            // 切换FTP目录
            client.changeWorkingDirectory(path);

            // 上传
            int idx = 0;
            for (InputStream fileStream : files.fileStreams) {
                retry = 0;
                while (retry != FTPTransferorConfig.maxRetry) {
                    if (!client.storeFile(
                            files.fileNames.get(idx),
                            fileStream
                    )) {
                        retry++;
                    } else
                        break;
                }
                if(retry == FTPTransferorConfig.maxRetry)  {
                    System.err.println("Upload for " + FTPTransferorConfig.maxRetry + " times, still failed");
                    return false;
                }

                System.out.println(files.fileNames.get(idx) + " transferred");
                idx++;
            }
        } catch (final IOException ioe) {
            ioe.printStackTrace();
            return false;
        } finally {
            // 释放资源
            files.close();

            // 断连
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (final IOException ioee) {
                    ioee.printStackTrace();
                }
            }
        }

        return true;
    }
}