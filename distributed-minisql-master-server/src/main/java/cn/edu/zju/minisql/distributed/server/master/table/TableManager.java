package cn.edu.zju.minisql.distributed.server.master.table;

import cn.edu.zju.minisql.distributed.server.master.Config;
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
        for(int i = 0; tableRegionList.size() < Config.minRegionSize && regionList.size() > 0; i++){
            String tableIdentifier = i + tableName;
            int index = Math.abs(tableIdentifier.hashCode() % regionList.size());
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
        if(tableRegionList.size() == 0) {
            removeTable(tableName);
            return;
        }
        for(String region: tableRegionList) regionList.remove(region);
        for(int i = 0; tableRegionList.size() < Config.minRegionSize && regionList.size() > 0; i++) {
            String tableIdentifier = i + tableName;
            String targetRegionAddress = regionList.remove(tableIdentifier.hashCode() % regionList.size());
            RegionServer targetRegionServer = RegionManager.getRegionServer(targetRegionAddress);
            String sourceRegionAddress = tableRegionList.get(0);
            RegionServer sourceRegionServer = RegionManager.getRegionServer(sourceRegionAddress);
            try {
                sourceRegionServer.openTransport();
                sourceRegionServer.getServiceClient().duplicateTable(
                        tableName,
                        targetRegionAddress,
                        targetRegionServer.getPath());
                sourceRegionServer.closeTransport();
                /*targetRegionServer.getServiceClient().duplicateTable(
                        tableName,
                        sourceRegionAddress.split(":", 2)[0],
                        sourceRegionServer.getPath());*/
                tableRegionList.add(targetRegionAddress);
            } catch (TException e) {
                e.printStackTrace();
            }

        }
    }
}
