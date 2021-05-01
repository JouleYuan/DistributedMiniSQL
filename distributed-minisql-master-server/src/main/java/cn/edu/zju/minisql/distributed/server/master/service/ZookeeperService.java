package cn.edu.zju.minisql.distributed.server.master.service;

import cn.edu.zju.minisql.distributed.server.master.service.impl.ZookeeperServiceImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperService {
    private static final String ADDRESS = "localhost:2181";
    private static final String PATH = "region-server";

    public static void listenRegionServer() {
        try {
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(ADDRESS)
                    .connectionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(1000,3))
                    .namespace(PATH)
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
