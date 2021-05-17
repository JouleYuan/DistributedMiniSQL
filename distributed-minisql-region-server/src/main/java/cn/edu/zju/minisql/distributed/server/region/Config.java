package cn.edu.zju.minisql.distributed.server.region;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static class Zookeeper {
        public static String ip;
        public static int port;
        public static int timeout;
        public static int baseSleepTime;
        public static int maxRetries;
        public static String namespace;
    }

    public static class Thrift {
        public static int port;
    }

    public static class Minisql {
        public static String path;
    }

    static void init() {
        Properties properties = new Properties();
        InputStream inputStream = RegionServer.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);

            Zookeeper.ip = properties.getProperty("zookeeper.ip");
            Zookeeper.port = Integer.parseInt(properties.getProperty("zookeeper.port"));
            Zookeeper.timeout = Integer.parseInt(properties.getProperty("zookeeper.timeout"));
            Zookeeper.baseSleepTime = Integer.parseInt(properties.getProperty("zookeeper.baseSleepTime"));
            Zookeeper.maxRetries = Integer.parseInt(properties.getProperty("zookeeper.maxRetries"));
            Zookeeper.namespace = properties.getProperty("zookeeper.namespace");

            Thrift.port = Integer.parseInt(properties.getProperty("thrift.port"));

            Minisql.path = properties.getProperty("minisql.path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
