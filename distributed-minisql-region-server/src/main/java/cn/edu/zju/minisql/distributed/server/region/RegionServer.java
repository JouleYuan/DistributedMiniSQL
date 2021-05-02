package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.region.service.ZookeeperService;

import java.io.InputStream;
import java.util.Properties;

public class RegionServer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream inputStream = Object.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);
            System.out.println("Starting the region server...");
            ZookeeperService.register(
                    properties.getProperty("zookeeper.ip"),
                    properties.getProperty("zookeeper.port"),
                    Integer.parseInt(properties.getProperty("zookeeper.timeout")),
                    Integer.parseInt(properties.getProperty("zookeeper.baseSleepTime")),
                    Integer.parseInt(properties.getProperty("zookeeper.maxRetries")),
                    properties.getProperty("zookeeper.namespace"),
                    System.getProperty("user.dir") + properties.getProperty("minisql.path"));
            ThriftService.exposeInterface(Integer.parseInt(properties.getProperty("thrift.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
