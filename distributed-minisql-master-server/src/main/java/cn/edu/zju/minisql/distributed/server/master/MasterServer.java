package cn.edu.zju.minisql.distributed.server.master;

import cn.edu.zju.minisql.distributed.server.master.service.ThriftService;
import cn.edu.zju.minisql.distributed.server.master.service.ZookeeperService;

import java.io.InputStream;
import java.util.Properties;

public class MasterServer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream inputStream = Object.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);
            System.out.println("Starting the master server...");
            ZookeeperService.listenRegionServer(
                    properties.getProperty("zookeeper.ip"),
                    properties.getProperty("zookeeper.port"),
                    Integer.parseInt(properties.getProperty("zookeeper.timeout")),
                    Integer.parseInt(properties.getProperty("zookeeper.baseSleepTime")),
                    Integer.parseInt(properties.getProperty("zookeeper.maxRetries")),
                    properties.getProperty("zookeeper.namespace"));
            ThriftService.exposeInterface(Integer.parseInt(properties.getProperty("thrift.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
