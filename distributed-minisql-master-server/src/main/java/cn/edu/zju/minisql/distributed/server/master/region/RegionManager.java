package cn.edu.zju.minisql.distributed.server.master.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionManager {
    private static final Map<String, RegionServer> addressRegionServerMap = new ConcurrentHashMap<>();

    public static void addRegionServer(String address, String path) {
        addressRegionServerMap.put(address, new RegionServer(address, path));
    }

    public static void removeRegionServer(String address) {
        addressRegionServerMap.remove(address);
    }

    public static RegionServer getRegionServer(String address) {
        return addressRegionServerMap.get(address);
    }

    public static List<String> getAddressList() {
        return new ArrayList<>(addressRegionServerMap.keySet());
    }

    public static int getRegionNum() {
        return addressRegionServerMap.size();
    }
}

