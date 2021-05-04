package cn.edu.zju.minisql.distributed.server.master.service;

import cn.edu.zju.minisql.distributed.server.master.service.impl.ZookeeperServiceImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperService {
    public static void listenRegionServer(String ip, int port, int timeout, int baseSleepTime,
                                          int maxRetries, String namespace) {
        try {
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(ip + ":" + port)
                    .connectionTimeoutMs(timeout)
                    .retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries))
                    .namespace(namespace)
                    .build();
            client.start();

            CuratorCache cache = CuratorCache.build(client, "/");
            CuratorCacheListener listener = CuratorCacheListener.builder()
                    .forCreates(ZookeeperServiceImpl::onCreation)
                    .forDeletes(ZookeeperServiceImpl::onDeletion)
                    .build();
            cache.listenable().addListener(listener);
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
