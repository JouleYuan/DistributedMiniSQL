package cn.edu.zju.minisql.distributed.server.region.lib;

import java.io.IOException;
import java.util.Vector;

import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.CatalogManager;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Index;
import cn.edu.zju.minisql.distributed.server.region.lib.catalogmanager.Table;
import cn.edu.zju.minisql.distributed.server.region.lib.lexer.Comparison;
import cn.edu.zju.minisql.distributed.server.region.lib.buffermanager.BufferManager;
import cn.edu.zju.minisql.distributed.server.region.lib.indexmanager.IndexManager;
import cn.edu.zju.minisql.distributed.server.region.lib.recordmanager.ConditionNode;
import cn.edu.zju.minisql.distributed.server.region.lib.recordmanager.RecordManager;
import cn.edu.zju.minisql.distributed.server.region.lib.recordmanager.Tuple;

/*
 * 疑问：1. select和delete返回的结果是record,
 * 		   manager直接输出还是由interpreter输出,
 *   	   如果由interpreter输出, 结果如何返回
 *      2. insert 对于unique属性
 * 		   如何从数据库中找出是否有相同value的数据,
 *         通过record manager?
 *
 */
public class API {
    /**
     * 初始化API
     *
     * @throws IOException
     */

    public static void close() throws IOException {
        CatalogManager.storeCatalog();
        BufferManager.close();
    }
    /**
     * 关闭API
     *
     * @throws IOException
     */
    public static void Initialize() throws IOException {
        BufferManager.initialize();
        CatalogManager.InitialCatalog();
    }

    public static void showCatalog() {
        CatalogManager.showCatalog();
    }

    public static void showTableCatalog() {
        CatalogManager.showTableCatalog();
    }

    public static void showIndexCatalog() {
        CatalogManager.showIndexCatalog();
    }

    /**
     * 创建表
     *
     * @param newTable
     * @return
     */
    public static boolean createTable(String tableName, Table newTable) {
        if (RecordManager.createTable(tableName)
                && CatalogManager.createTable(newTable)){
            Index newIndex = new Index(tableName+"_prikey",tableName, CatalogManager.getPrimaryKey(tableName));
            IndexManager.createIndex(newIndex);
            CatalogManager.createIndex(newIndex);
            return true;
        }
        else
            return false;
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return
     */
    public static boolean dropTable(String tableName) {
        for(int i=0;i<CatalogManager.getTableAttriNum(tableName);i++){
            String indexName = CatalogManager.getIndexName(tableName,CatalogManager.getAttriName(tableName, i));
            if(indexName!=null)
                IndexManager.dropIndex(indexName);
        }
        if (RecordManager.dropTable(tableName)) {
            CatalogManager.dropTable(tableName);
        }
        return true;
    }

    /**
     * 创建索引
     *
     * @param newIndex
     * @return
     */
    public static boolean createIndex(Index newIndex) {
        boolean t = IndexManager.createIndex(newIndex);
        return t & CatalogManager.createIndex(newIndex);
    }

    /**
     * 删除索引
     *
     * @param indexName
     * @return
     */
    public static boolean dropIndex(String indexName) {
        boolean t = IndexManager.dropIndex(indexName);
        return t & CatalogManager.dropIndex(indexName);
    }


    /**
     * 插入记录
     *
     * @param tableName
     * @param theTuple
     * @return
     */
    public static boolean insertTuples(String tableName, Tuple theTuple) {

        int tupleOffset = RecordManager.insert(tableName, theTuple);
        //获取index列表
        int n = CatalogManager.getTableAttriNum(tableName);
        try{
            for(int i=0;i<n;i++){
                String attrName = CatalogManager.getAttriName(tableName, i);
                String indexName = CatalogManager.getIndexName(tableName,attrName);
                if(indexName==null)
                    continue;
                Index indexInfo = CatalogManager.getIndex(indexName);
                String key = theTuple.units.elementAt(CatalogManager.getAttriOffest(tableName, indexInfo.attriName));
                IndexManager.insertKey(indexInfo, key, 0, tupleOffset);
                CatalogManager.updateIndexTable(indexInfo.indexName, indexInfo);
            }
            CatalogManager.addTupleNum(tableName);
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 删除记录
     *
     * @param tableName
     * @param conditionNodes
     * @return
     */
    public static int deleteTuples(String tableName,
                                   ConditionNode conditionNodes) {
        int deleteNum = RecordManager.delete(tableName, conditionNodes);
        CatalogManager.deleteTupleNum(tableName, deleteNum);
        return deleteNum;
    }

    /**
     * 查询
     *
     * @param tableName
     * @param conditionNodes
     * @return
     */
    public static Vector<Tuple> selectTuples(String tableName,
                                             Vector<String> attriNames, ConditionNode conditionNodes) {
        Vector<Tuple> res = new Vector<Tuple>(0);
        if ( conditionNodes!=null && conditionNodes.left == null && conditionNodes.right == null
                && conditionNodes.op == Comparison.eq && CatalogManager.getIndexName(tableName,
                conditionNodes.attrName)!= null) {
            try {
                Vector<Integer> targets = IndexManager.searchRange(
                        CatalogManager.getIndex(CatalogManager.getIndexName(tableName,
                                conditionNodes.attrName)),
                        conditionNodes.value,
                        conditionNodes.value);
                if(targets != null){
                    res = RecordManager.getTuple(tableName, targets);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            res = RecordManager.select(tableName, conditionNodes);
        if (attriNames != null)
            return RecordManager.project(res, tableName, attriNames);
        else
            return res;
    }

    public static Vector<Tuple> selectTuples(String tableName,
                                             Vector<String> attrNames, ConditionNode conditionNodes,
                                             String orderAttr, boolean ins) {
        Vector<Tuple> res = RecordManager.select(tableName, conditionNodes, orderAttr, ins);
        if (attrNames != null)
            return RecordManager.project(res, tableName, attrNames);
        else
            return res;
    }
    public static Vector<Tuple> join(String tableName1,
                                     String attributeName1,
                                     String tableName2,
                                     String attributeName2){
        return RecordManager.join(tableName1, attributeName1, tableName2, attributeName2);
    }
}
