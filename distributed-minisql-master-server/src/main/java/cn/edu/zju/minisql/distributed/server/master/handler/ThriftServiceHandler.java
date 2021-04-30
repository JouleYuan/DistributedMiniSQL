package cn.edu.zju.minisql.distributed.server.master.handler;

import cn.edu.zju.minisql.distributed.service.thrift.MasterService;

import java.util.List;

public class ThriftServiceHandler implements MasterService.Iface {

    @Override
    public boolean createTable(String tableName, List<Integer> attributeTypes, List<String> attributeNames, int primaryKey){
        System.out.println("createTable(" + tableName + ")");
        System.out.print("attributeTypes: ");
        for(int type: attributeTypes) System.out.print(type + " ");
        System.out.println();
        System.out.print("attributeNames: ");
        for(String name: attributeNames) System.out.print(name + " ");
        System.out.println();
        System.out.print(primaryKey);
        return true;
    }

    @Override
    public boolean dropTable(String tableName){
        System.out.println("dropTable(" + tableName + ")");
        return true;
    }
}
