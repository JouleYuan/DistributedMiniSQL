package cn.edu.zju.minisql.distributed.server.region;

import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Attribute;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Table;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Index;

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

    static String getPrimaryKey(cn.edu.zju.minisql.distributed.service.Table t){
        return t.attributes.get(t.primaryKeyIndex).name;
    }


    public static class SqlTable extends Table {
        public SqlTable(String tableName, Vector<Attribute> attributes, String primaryKey) {
            super(tableName, attributes, primaryKey);
        }

        public SqlTable(cn.edu.zju.minisql.distributed.service.Table t){
            super(t.name, list2vector(t.attributes), getPrimaryKey(t));
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
            return "FLOAT";
        }
        if(t == cn.edu.zju.minisql.distributed.service.AttributeType.CHAR){
            return "CHAR";
        }
        if(t == cn.edu.zju.minisql.distributed.service.AttributeType.INT){
            return "INT";
        }
        return "";
    }
}