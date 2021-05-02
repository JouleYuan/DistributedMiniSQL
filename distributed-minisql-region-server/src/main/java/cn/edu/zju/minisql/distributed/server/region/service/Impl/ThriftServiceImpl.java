package cn.edu.zju.minisql.distributed.server.region.service.Impl;

import cn.edu.zju.minisql.distributed.service.ConditionNode;
import cn.edu.zju.minisql.distributed.service.Index;
import cn.edu.zju.minisql.distributed.service.RegionService;
import cn.edu.zju.minisql.distributed.service.Table;

import java.util.List;

public class ThriftServiceImpl implements RegionService.Iface {
    @Override
    public boolean createTable(Table table) {
        // do something...
        return true;
    }

    @Override
    public boolean dropTable(String tableName) {
        // do something...
        return true;
    }

    @Override
    public boolean duplicateTable(String tableName, String ip, String path) {
        // do something...
        return true;
    }

    @Override
    public boolean duplicateCatalog(Table table, List<Index> indexes) {
        // do something...
        return true;
    }

    @Override
    public Table select(
            String tableName,
            List<String> attributeNames,
            ConditionNode conditions,
            String orderKeyAttributeName,
            boolean isIncreased) {
        // do something...
        return new Table();
    }

    @Override
    public boolean insert(String tableName, List<String> tuple) {
        // do something...
        return true;
    }

    @Override
    public int deleteRecords(String tableName, ConditionNode conditions) {
        // do something...
        return 0;
    }

    @Override
    public boolean createIndex(Index index) {
        // do something...
        return true;
    }

    @Override
    public boolean dropIndex(String indexName) {
        // do something...
        return true;
    }
}
