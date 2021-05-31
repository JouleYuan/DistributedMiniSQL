package cn.edu.zju.minisql.distributed.server.region;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.CatalogManager;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import cn.edu.zju.minisql.distributed.server.region.minisql.*;

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

        final String tableNameAndIdxNameSep = "_";
        final String indexFilePatternStr = "\\.index$"; // 以.index结尾的
        final Pattern indexFilePattern = Pattern.compile(indexFilePatternStr);

        public MiniSQLTableFiles(String tableName) {
            final String baseDir = System.getProperty("user.dir");
            final String prefix = baseDir + File.separator + Config.Minisql.path;
            final File dirFile = new File(baseDir
                    + File.separator
                    + Config.Minisql.path.substring(0, Config.Minisql.path.length() - 1)
            );

            try {
                // file: <tableName>
                System.out.println("MiniSQLTableFiles. Add table: " + tableName);
                fileStreams.add(
                        new FileInputStream(prefix + tableName)
                );
                fileNames.add(tableName);

                // file: <tableName><indexName>.index
                final File[] files = dirFile.listFiles(); // 列出目录所有文件
                for(File f : files) {
                    if (f.isDirectory())
                        continue;

                    Matcher matcher = indexFilePattern.matcher(f.getName());

                    if (matcher.find()) {
                        // 若文件名匹配正则 indexFilePatternStr
                        System.out.println("MiniSQLTableFiles. Find index file: " + f.getName());

                        String tbNameOfIndexFile = tableNameOfIndexFile(f.getName());
                        if (tbNameOfIndexFile.equals(tableName)) {
                            // 若表名匹配参数 tableName
                            System.out.println("MiniSQLTableFiles. Add index: " + f.getName());
                            fileStreams.add(
                                    new FileInputStream(prefix + f.getName())
                            );
                            ok = fileNames.add(f.getName());
                        }
                    }
                }

                ok = true;
            } catch (final IOException ioe) {
                close();
                ioe.printStackTrace();
                System.err.println("Local file doesn't exist");
            }
        }

        private String tableNameOfIndexFile(String fileName)
        {
            int afterTbName = fileName.indexOf(tableNameAndIdxNameSep);
            return fileName.substring(0, afterTbName);
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
            System.out.println("FTPTransferor. Connect to " + ip + ":" + FTPTransferorConfig.port);

            final int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                System.err.println("FTPTransferor. FTP server refused connection");
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
            System.err.println("FTPTransferor. Could not connect to " + ip + ":" + FTPTransferorConfig.port);
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
                System.err.println("FTPTransferor. Login for " + FTPTransferorConfig.maxRetry + " times, still failed");
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
                    if (
                            !client.storeFile(
                                    files.fileNames.get(idx),
                                    fileStream
                            )
                    ) {
                        retry++;
                    } else
                        break;
                }
                if(retry == FTPTransferorConfig.maxRetry)  {
                    System.err.println("FTPTransferor. Upload for " + FTPTransferorConfig.maxRetry + " times, still failed");
                    return false;
                }

                System.out.println("FTPTransferor. " + files.fileNames.get(idx) + " transferred");
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