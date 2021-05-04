package cn.edu.zju.minisql.distributed.server.master;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static int minRegionSize;

    public static class Zookeeper {
        public static String ip;
        public static String port;
        public static int timeout;
        public static int baseSleepTime;
        public static int maxRetries;
        public static String namespace;
    }

    public static class Thrift {
        public static int port;
    }

    static {
        Properties properties = new Properties();
        InputStream inputStream = Object.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);

            minRegionSize = Integer.parseInt(properties.getProperty("minRegionSize"));

            Zookeeper.ip = properties.getProperty("zookeeper.ip");
            Zookeeper.port = properties.getProperty("zookeeper.port");
            Zookeeper.timeout = Integer.parseInt(properties.getProperty("zookeeper.timeout"));
            Zookeeper.baseSleepTime = Integer.parseInt(properties.getProperty("zookeeper.baseSleepTime"));
            Zookeeper.maxRetries = Integer.parseInt(properties.getProperty("zookeeper.maxRetries"));
            Zookeeper.namespace = properties.getProperty("zookeeper.namespace");

            Thrift.port = Integer.parseInt(properties.getProperty("thrift.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
