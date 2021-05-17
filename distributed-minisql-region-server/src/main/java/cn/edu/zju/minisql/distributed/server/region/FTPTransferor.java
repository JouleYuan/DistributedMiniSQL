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
        public static final String userName = "ftpuser";
        public static final String password = "lemontree";
        public static final int port = 21;
        public static final int maxRetry = 5;
    }

    private static class MiniSQLTableFiles {
        public ArrayList<InputStream> fileStreams = new ArrayList<>();
        public ArrayList<String> fileNames = new ArrayList<>();
        public boolean ok = false;

        public MiniSQLTableFiles(String tableName) {
            String prefix = System.getProperty("user.dir");
            String env = System.getProperty("env");
            String delimiter = env.equals("unix") ? "/" : "\\";

            try {
                // <tableName>
                fileStreams.add(new FileInputStream(
                        new File(prefix + delimiter + tableName)
                ));
                fileNames.add(tableName);

                // <tableName>_prikey.index
                fileStreams.add(new FileInputStream(
                        new File(prefix + delimiter + tableName + "_prikey.index"))
                );
                fileNames.add(tableName + "_prikey.index");

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
            } catch (final IOException ioe) {
                ioe.printStackTrace();
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
                } catch (final IOException e) {
                    e.printStackTrace();
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
                System.err.println("Login error");
                return false; // 达到重试上限
            }

            // 配置
            client.setFileType(FTP.BINARY_FILE_TYPE);
            //client.enterLocalPassiveMode(); // 考虑防火墙使用被动模式
            client.setControlEncoding("UTF-8");

            // 切换FTP目录
            client.changeWorkingDirectory(path);

            // 上传
            int idx = 0;
            for (InputStream fileStream : files.fileStreams) {
                retry = 0;
                while (retry != FTPTransferorConfig.maxRetry) {
                    System.out.println(files.fileNames.get(idx) + " retry: " + retry);
                    if (!client.storeFile(
                            files.fileNames.get(idx),
                            fileStream
                    )) {
                        retry++;
                    } else
                        break;
                }
                if(retry == FTPTransferorConfig.maxRetry)  {
                    System.err.println("Upload error");
                    return false;
                }
                idx++;
                System.out.println("finished: " + files.fileNames.get(idx));
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
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return true;
    }
}
