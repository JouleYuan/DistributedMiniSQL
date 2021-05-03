package cn.edu.zju.minisql.distributed.server.master.table;

import cn.edu.zju.minisql.distributed.server.master.region.RegionManager;
import cn.edu.zju.minisql.distributed.server.master.region.RegionServer;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableManager {
    private static final Map<String, List<String>> tableMap = new ConcurrentHashMap<>();

    public static List<String> addTable(String tableName) {
        if(tableMap.containsKey(tableName)) return null;
        List<String> regionList = RegionManager.getAddressList();
        List<String> tableRegionList = new ArrayList<>();
        for(int i = 0; tableRegionList.size() < 3 && regionList.size() > 0; i++){
            String tableIdentifier = i + tableName;
            int index = tableIdentifier.hashCode()/regionList.size();
            tableRegionList.add(regionList.remove(index));
        }
        tableMap.put(tableName, tableRegionList);
        return tableRegionList;
    }

    public static boolean removeTable(String tableName) {
        return tableMap.remove(tableName) != null;
    }

    public static List<String> getTables() {
        return new ArrayList<>(tableMap.keySet());
    }

    public static List<String> getRegions(String tableName) {
        return tableMap.get(tableName);
    }

    public static void replaceRegion(String tableName, String regionIdentifier) {
        List<String> regionList = RegionManager.getAddressList();
        List<String> tableRegionList = tableMap.get(tableName);
        tableRegionList.remove(regionIdentifier);
        for(String region: tableRegionList) regionList.remove(region);
        for(int i = 0; tableRegionList.size() < 3 && regionList.size() > 0; i++) {
            String tableIdentifier = i + tableName;
            String regionAddress = regionList.remove(tableIdentifier.hashCode()/regionList.size());
            RegionServer regionServer = RegionManager.getRegionServer(regionAddress);
            String sourceRegionAddress = tableRegionList.get(0);
            RegionServer sourceRegionServer = RegionManager.getRegionServer(sourceRegionAddress);
            try {
                regionServer.getServiceClient().duplicateTable(
                        tableName,
                        sourceRegionAddress.split(":", 2)[0],
                        sourceRegionServer.getPath());
                tableRegionList.add(regionAddress);
            } catch (TException e) {
                e.printStackTrace();
            }

        }
    }
}
