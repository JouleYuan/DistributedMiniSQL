package cn.edu.zju.minisql.distributed.server.region.service.Impl;

import cn.edu.zju.minisql.distributed.server.region.lib.API;
import cn.edu.zju.minisql.distributed.server.region.lib.Interpreter;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.CatalogManager;
import cn.edu.zju.minisql.distributed.server.region.TypesRedefinition;
import cn.edu.zju.minisql.distributed.service.Index;
import cn.edu.zju.minisql.distributed.service.RegionService;
import cn.edu.zju.minisql.distributed.service.Table;

import java.io.*;
import java.util.List;

public class ThriftServiceImpl implements RegionService.Iface {
    static {
        try {
            API.Initialize();
            System.out.println("initialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createTable(Table table) {
        System.out.println("creating table: " + table.name);
        return API.createTable(table.name, new TypesRedefinition.SqlTable(table));
    }

    @Override
    public boolean dropTable(String tableName) {
        System.out.println("dropping table: " + tableName);
        return API.dropTable(tableName);
    }

    @Override
    public boolean duplicateTable(String tableName, String ip, String path) {
        return false;
    }

    @Override
    public boolean duplicateCatalog(Table table, List<Index> indexes) {
        if(CatalogManager.createTable(new TypesRedefinition.SqlTable(table))){                     // add catalog of table first
            for (Index index : indexes) {
                boolean t = CatalogManager.createIndex(new TypesRedefinition.SqlIndex(index));     // then add catalog of each index
                if (!t) return false;
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String sqlRequest(String request) {
        System.out.println("receive request: " + request);

        BufferedReader reader = new BufferedReader(new StringReader(request));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        PrintStream old = System.out;
        System.setOut(printStream);

        try {
            Interpreter.resetFlags();
            Interpreter.Parsing(reader);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.flush();
        System.setOut(old);
        return byteArrayOutputStream.toString();
    }
}
