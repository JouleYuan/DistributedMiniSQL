package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Attribute;
import cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Table;
import cn.edu.zju.minisql.distributed.server.region.minisql.catalogmanager.Index;
import cn.edu.zju.minisql.distributed.service.AttributeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TypesRedefinition {
    static Vector<Attribute> list2vector(List<cn.edu.zju.minisql.distributed.service.Attribute> attrs){
        Vector<Attribute> res = new Vector<>();
        for(int i=0; i<attrs.size(); i++){
            res.add(new SqlAttribute(attrs.get(i)));
        }
        return res;
    }

    static Vector<Index> indexList2vector(List<cn.edu.zju.minisql.distributed.service.Index> indexes){
        Vector<Index> res = new Vector<>();
        for(int i=0; i<indexes.size(); i++){
            res.add(new SqlIndex(indexes.get(i)));
        }
        return res;
    }

    static String getPrimaryKey(cn.edu.zju.minisql.distributed.service.Table t){
        return t.attributes.get(t.primaryKeyIndex).name;
    }


    public static class SqlTable extends Table {
        public SqlTable(String tableName, Vector<Attribute> attributes, String primaryKey) {
            super(tableName, attributes, primaryKey);
        }

        public SqlTable(cn.edu.zju.minisql.distributed.service.Table t){
            super(t.name, list2vector(t.attributes), TypesRedefinition.getPrimaryKey(t));
        }

        public SqlTable(
                cn.edu.zju.minisql.distributed.service.Table t,
                List<cn.edu.zju.minisql.distributed.service.Index> indexes
        ){
            super(
                    t.name,
                    list2vector(t.attributes),
                    indexList2vector(indexes),
                    TypesRedefinition.getPrimaryKey(t),
                    t.tuples.size()
            );
        }
    };

    public static class SqlAttribute extends Attribute {

        public SqlAttribute(String attrName, String type, int length, boolean isU) {
            super(attrName, type, length, isU);
        }

        public SqlAttribute(cn.edu.zju.minisql.distributed.service.Attribute attr){
            super(attr.name, type2str(attr.type), attr.length, attr.isUnique);
        }
    }

    public static class SqlIndex extends Index {

        public SqlIndex(String indexName, String tableName, String attriName, int blockNum, int rootNum) {
            super(indexName, tableName, attriName, blockNum, rootNum);
        }

        public SqlIndex(String indexName, String tableName, String attriName){
            super(indexName, tableName, attriName);
        }
        public SqlIndex(cn.edu.zju.minisql.distributed.service.Index ind){
            super(ind.indexName, ind.tableName, ind.attributeName);
        }
    }

    public static String type2str(cn.edu.zju.minisql.distributed.service.AttributeType t){
        if(t == cn.edu.zju.minisql.distributed.service.AttributeType.FLOAT){
            return "float";
        }
        if(t == cn.edu.zju.minisql.distributed.service.AttributeType.CHAR){
            return "char";
        }
        if(t == cn.edu.zju.minisql.distributed.service.AttributeType.INT){
            return "int";
        }
        return "";
    }

    static List<cn.edu.zju.minisql.distributed.service.Attribute> vector2list(Vector<Attribute> miniSqlAttrs) {
        ArrayList<cn.edu.zju.minisql.distributed.service.Attribute> serviceAttrs = new ArrayList<>();
        for (Attribute miniSqlAttr : miniSqlAttrs) {
            serviceAttrs.add(new ServiceAttribute(miniSqlAttr));
        }
        return serviceAttrs;
    }

    public static cn.edu.zju.minisql.distributed.service.AttributeType str2type(String str) {
        if (str.equals("float"))
            return AttributeType.FLOAT;
        if (str.equals("char"))
            return AttributeType.CHAR;
        return AttributeType.INT;
    }

    public static class ServiceIndex extends cn.edu.zju.minisql.distributed.service.Index {
        public ServiceIndex(Index miniSqlIndex) {
            super (
                    miniSqlIndex.indexName,
                    miniSqlIndex.tableName,
                    miniSqlIndex.attriName
            );
        }
    }

    public static class ServiceAttribute extends cn.edu.zju.minisql.distributed.service.Attribute {
        public ServiceAttribute(Attribute miniSqlAttr) {
            super(
                    miniSqlAttr.getAttrName(),
                    str2type(miniSqlAttr.getType()),
                    miniSqlAttr.getLength(),
                    miniSqlAttr.isUnique()
            );
        }
    }

    public static class ServiceTable extends cn.edu.zju.minisql.distributed.service.Table {
        private static List<List<String>> getTuples(Table miniSqlTable) {
            List<List<String>> tuples = new ArrayList<>();
            for (int i=0;i<miniSqlTable.getTupleNum();i++) {
                // ??????????????????????????????
                tuples.add(new ArrayList<String>());
            }
            return tuples;
        }

        private static int getPrimaryKeyIdx(Vector<Attribute> miniSqlAttrs, String primaryKeyName) {
            // ?????????????????????
            int primaryKeyIdx = 0;
            for (Attribute attr : miniSqlAttrs) {
                if (attr.getAttrName().equals(primaryKeyName))
                    break;
                primaryKeyIdx++;
            }
            return primaryKeyIdx;
        }

        public ServiceTable(Table minisqlTable) {
            super(
                    minisqlTable.getTableName(),
                    vector2list(minisqlTable.getAttributes()),
                    getPrimaryKeyIdx(minisqlTable.getAttributes(), minisqlTable.getPrimaryKey()),
                    getTuples(minisqlTable)
            );
        }
    }
}