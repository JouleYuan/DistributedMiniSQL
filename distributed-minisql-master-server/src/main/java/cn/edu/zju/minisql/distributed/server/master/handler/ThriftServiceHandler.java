package cn.edu.zju.minisql.distributed.server.master.handler;

import cn.edu.zju.minisql.distributed.service.thrift.Attribute;
import cn.edu.zju.minisql.distributed.service.thrift.MasterService;

import java.util.List;

public class ThriftServiceHandler implements MasterService.Iface {

    @Override
    public boolean createTable(String tableName, List<Attribute> attributes, int primaryKeyIndex){
        System.out.println("createTable(" + tableName + ")");
        System.out.print("attributeTypes: ");
        for(Attribute attribute: attributes) System.out.print(attribute.type + " ");
        System.out.println();
        System.out.print("attributeNames: ");
        for(Attribute attribute: attributes) System.out.print(attribute.name + " ");
        System.out.println();
        System.out.println("primaryKeyIndex: " + primaryKeyIndex);
        return true;
    }

    @Override
    public boolean dropTable(String tableName){
        System.out.println("dropTable(" + tableName + ")");
        return true;
    }
}
