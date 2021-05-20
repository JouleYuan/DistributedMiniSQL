package cn.edu.zju.minisql.distributed.server.region.service.Impl;

import cn.edu.zju.minisql.distributed.server.region.lib.API;
import cn.edu.zju.minisql.distributed.server.region.lib.Interpreter;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.CatalogManager;
import cn.edu.zju.minisql.distributed.server.region.TypesRedefinition;
import cn.edu.zju.minisql.distributed.service.Index;
import cn.edu.zju.minisql.distributed.service.RegionService;
import cn.edu.zju.minisql.distributed.service.Table;

import cn.edu.zju.minisql.distributed.server.region.FTPTransferor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

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
            // 回写MiniSql的内存到磁盘
            try {
                minisql.API.close();
            } catch (final IOException ioe) {
                System.err.println("MiniSql closing failed");
                ioe.printStackTrace();
                break;
            }

            // 使用FTP传输所有记录
            ok = FTPTransferor.fromLocalFTPto(tableName, ip, path);
            if(!ok)
                break;

            final String baseDir = System.getProperty("user.dir");
            final String primaryKeyIndexFilePatternStr = "_prikey";
            final String indexFileExtName = ".index";
            final String indexFilePatternStr = "\\.index$"; // 以.index结尾的
            final Pattern indexFilePattern = Pattern.compile(indexFilePatternStr);
            final File dirFile = new File(baseDir);

            // 列出目录所有文件
            final File[] files = dirFile.listFiles();

            List<Index> indexes = new ArrayList<>();
            for(File f : files) {
                if(f.isDirectory())
                    continue;

                // 使用正则表达式匹配文件名
                Matcher matcher = indexFilePattern.matcher(f.getName());

                if(matcher.find()) {
                    // 若文件名匹配
                    System.out.println("find index: " + f.getName());

                    String indexName = f.getName().substring(
                            0, f.getName().indexOf(indexFileExtName)
                    );
                    if(indexName.contains(primaryKeyIndexFilePatternStr)) {
                        // 若是主键的索引，不加入indexes
                        continue;
                    }

                    minisql.CATALOGMANAGER.index miniSqlIndex =
                            minisql.CATALOGMANAGER.CatalogManager.getIndex(indexName);

                    if (miniSqlIndex.tableName.equals(tableName)) {
                        // 若index对应的tableName与参数相同，
                        // 将index加入列表
                        Index serviceIndex = new Index(
                                miniSqlIndex.indexName,
                                miniSqlIndex.tableName,
                                miniSqlIndex.attriName
                        );
                        indexes.add(serviceIndex);
                    }
                }
            }

            // 使用Thrift，调用备份对象重建Catalog与Index
            // TODO
            break;
        }

        try {
            minisql.API.close();
        } catch (final IOException ioe) {
            System.err.println("MiniSql initializing failed");
            ioe.printStackTrace();
        }

        if(ok) return false;
        else return true;
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
