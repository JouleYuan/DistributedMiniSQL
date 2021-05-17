package cn.edu.zju.minisql.distributed.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static class Master {
        public static String ip;
        public static int port;
    }

    static void init() {
        Properties properties = new Properties();
        InputStream inputStream = Client.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);

            Master.ip = properties.getProperty("master.ip");
            Master.port = Integer.parseInt(properties.getProperty("master.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
