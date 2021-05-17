package cn.edu.zju.minisql.distributed.server.master.service.impl;

import cn.edu.zju.minisql.distributed.server.master.region.RegionManager;
import cn.edu.zju.minisql.distributed.server.master.region.RegionServer;
import cn.edu.zju.minisql.distributed.server.master.table.TableManager;
import cn.edu.zju.minisql.distributed.service.MasterService;
import cn.edu.zju.minisql.distributed.service.Table;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class ThriftServiceImpl implements MasterService.Iface {
    @Override
    public List<String> showTables(){
        System.out.println("showTables()");

        return TableManager.getTables();
    }

    @Override
    public List<String> createTable(Table table){
        System.out.println("createTable(" + table.getName() + ")");

        List<String> regionAddressList = TableManager.addTable(table.getName());
        if(regionAddressList == null) return new ArrayList<>();

        for (String regionAddress : regionAddressList) {
            RegionServer regionServer = RegionManager.getRegionServer(regionAddress);
            try {
                regionServer.openTransport();
                regionServer.getServiceClient().createTable(table);
                regionServer.closeTransport();
                regionServer.getTables().add(table.getName());
            } catch (TException e) {
                e.printStackTrace();
            }
        }

        return regionAddressList;
    }

    @Override
    public boolean dropTable(String tableName){
        System.out.println("dropTable(" + tableName + ")");

        List<String> regionAddressList = TableManager.getRegions(tableName);
        if(regionAddressList == null) return false;

        for(String regionAddress: regionAddressList) {
            RegionServer regionServer = RegionManager.getRegionServer(regionAddress);
            try {
                regionServer.openTransport();
                regionServer.getServiceClient().dropTable(tableName);
                regionServer.closeTransport();
                regionServer.getTables().remove(tableName);
            } catch (TException e) {
                e.printStackTrace();
            }
        }

        return TableManager.removeTable(tableName);
    }

    @Override
    public List<String> getRegionServers(String tableName){
        System.out.println("getRegionServers(" + tableName + ")");

        List<String> regions = TableManager.getRegions(tableName);

        if(regions != null) return regions;
        else return new ArrayList<>();
    }
}
