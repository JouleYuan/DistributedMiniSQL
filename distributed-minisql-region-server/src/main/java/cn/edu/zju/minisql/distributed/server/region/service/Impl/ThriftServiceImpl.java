package cn.edu.zju.minisql.distributed.server.region.service.Impl;

import cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Attribute;
import cn.edu.zju.minisql.distributed.server.region.region.RegionServer;
import cn.edu.zju.minisql.distributed.server.region.minisql.API;
import cn.edu.zju.minisql.distributed.server.region.minisql.Interpreter;
import cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.CatalogManager;
import cn.edu.zju.minisql.distributed.server.region.TypesRedefinition;
import cn.edu.zju.minisql.distributed.service.Index;
import cn.edu.zju.minisql.distributed.service.RegionService;
import cn.edu.zju.minisql.distributed.service.Table;

import cn.edu.zju.minisql.distributed.server.region.FTPTransferor;
import org.apache.thrift.TException;
import org.checkerframework.checker.units.qual.A;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
        // 最终结果判定
        boolean ok = false;

        // 使用for简化流程
        for(;;) {
            // 关闭MiniSql以回写MiniSql的内存到磁盘
            try {
                API.close();
            } catch (final IOException ioe) {
                System.err.println("MiniSql closing failed");
                ioe.printStackTrace();
                ok = false;
                break;
            }

            // 使用FTP传输所有记录、索引文件
            final String onlyIP = ip.split(":", 2)[0];
            ok = FTPTransferor.fromLocalFTPto(tableName, onlyIP, path);
            if(!ok)
                break;

            // 重启MiniSql以获得各个Manager的数据
            try {
                API.Initialize();
            } catch (final IOException ioe) {
                System.err.println("MiniSql initializing failed");
                ioe.printStackTrace();
                ok = false;
                break;
            }

            // 获取MiniSql数据
            cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Table miniSqlTable =
                    CatalogManager.getTable(tableName);
            Vector<cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Index> miniSqlIndexes
                    = miniSqlTable.getIndexes();

            // 转换索引格式
            ArrayList<cn.edu.zju.minisql.distributed.service.Index> serviceIndexes = new ArrayList<>();
            for (cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Index idx
                    : miniSqlIndexes) {
                serviceIndexes.add(new TypesRedefinition.ServiceIndex(idx));
            }

            // 使用Thrift，调用备份对象重建Catalog与Index
            // 注意：这里是region.region.RegionServer
            RegionServer regionServer = new RegionServer(ip, path);
            try {
                regionServer.openTransport();
                regionServer.getServiceClient().
                        duplicateCatalog(
                                new TypesRedefinition.ServiceTable(miniSqlTable),
                                serviceIndexes
                        );
            } catch (TException e) {
                e.printStackTrace();
            } finally {
                regionServer.closeTransport();
            }
            break;
        }
        return !ok;
    }

    @Override
    public boolean duplicateCatalog(Table table, List<Index> indexes) {
        if(
                CatalogManager.createTable(
                        new TypesRedefinition.SqlTable(table, indexes)
                )
        ){                     // add catalog of table first
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