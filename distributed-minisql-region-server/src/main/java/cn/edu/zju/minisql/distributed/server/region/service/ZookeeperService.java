package cn.edu.zju.minisql.distributed.server.region.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;

public class ZookeeperService {
    public static void register(String ip, int port, int connectionTimeout, int sessionTimeout, int baseSleepTime,
                                int maxRetries, String namespace, int thriftPort, String path) {
        try {
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(ip + ":" + port)
                    .connectionTimeoutMs(connectionTimeout)
                    .sessionTimeoutMs(sessionTimeout)
                    .retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries))
                    .namespace(namespace)
                    .build();
            client.start();

            client.create().withMode(CreateMode.EPHEMERAL).forPath(
                    "/" + InetAddress.getLocalHost().getHostAddress() + ":" + thriftPort, path.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
