package cn.edu.zju.minisql.distributed.server.master.service.impl;

import cn.edu.zju.minisql.distributed.service.Attribute;
import cn.edu.zju.minisql.distributed.service.MasterService;
import cn.edu.zju.minisql.distributed.service.Table;

import java.util.ArrayList;
import java.util.List;

public class ThriftServiceImpl implements MasterService.Iface {
    @Override
    public List<String> showTables(){
        System.out.println("showTables()");

        List<String> list = new ArrayList<>();
        list.add("student");
        return list;
    }

    @Override
    public List<String> createTable(Table table){
        System.out.println("createTable(" + table.getName() + ")");
        System.out.println("attributes:");
        for(Attribute attribute: table.getAttributes()) {
            System.out.print(attribute.getName() + " ");
            System.out.println(attribute.getType());
        }
        System.out.println("primaryKeyIndex: " + table.getPrimaryKeyIndex());

        List<String> list = new ArrayList<>();
        list.add("localhost:5000");
        list.add("192.168.0.1");
        return list;
    }

    @Override
    public boolean dropTable(String tableName){
        System.out.println("dropTable(" + tableName + ")");
        return true;
    }

    @Override
    public List<String> getRegionServers(String tableName){
        System.out.println("getRegionServers(" + tableName + ")");

        List<String> list = new ArrayList<>();
        list.add("localhost:5000");
        list.add("192.168.0.1");
        return list;
    }
}
