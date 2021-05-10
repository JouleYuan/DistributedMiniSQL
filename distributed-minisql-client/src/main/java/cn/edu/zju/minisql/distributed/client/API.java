package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class API {
    private static TTransport masterTransport;
    private static MasterService.Client masterServiceClient;
    private static HashMap<String,List<String>> tableToRegion = new HashMap<>(); // regionServer缓存

    public static void init(){
        try{
            Config.init();

            // connect to master server
            masterTransport = new TSocket(Config.Master.ip, Config.Master.port);
            masterTransport.open();

            TProtocol protocol = new TBinaryProtocol(masterTransport);
            masterServiceClient = new MasterService.Client(protocol);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        masterTransport.close();
    }

    public static void createTable(String tableName, Table newTable) {
        try{
            // 没有同名表
            if(masterServiceClient.getRegionServers(tableName).size() == 0){
                List<String> regions =  masterServiceClient.createTable(newTable);
                if(regions.size() > 0){
                    tableToRegion.put(tableName, regions);
                    System.out.println("Query OK, 0 rows affected");
                }
                else{
                    System.out.println("ERROR: Fail to create table " + tableName);
                }
            }
            else{
                System.out.println("ERROR: Table " + tableName + " already exists.");
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void dropTable(String tableName) {
        try{
            // 存在表
            if(masterServiceClient.getRegionServers(tableName).size() > 0){
                if(masterServiceClient.dropTable(tableName)){
                    System.out.println("Query OK, 0 rows affected");
                }
                else{
                    System.out.println("ERROR: Fail to drop table " + tableName);
                }
            }
            else{
                System.out.println("ERROR: Unknown table " + tableName);
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void createIndex(String indexName, String tableName, String attrName) {
        try{
            List<String> regions = tableToRegion.containsKey(tableName)? tableToRegion.get(tableName) : masterServiceClient.getRegionServers(tableName);

            // 存在表
            if(regions.size() > 0){
                tableToRegion.put(tableName, regions);

                for(String regionServer: regions){
                    TTransport regionTransport;
                    RegionService.Client regionServiceClient;
                    regionTransport = new TSocket(regionServer.split(":")[0], Integer.parseInt(regionServer.split(":")[1]));
                    regionTransport.open();

                    TProtocol protocol = new TBinaryProtocol(regionTransport);
                    regionServiceClient = new RegionService.Client(protocol);

                    //todo:attrIndex获取 或者 传入String indexName, String tableName, String attrName而不是Index
                    if(!regionServiceClient.createIndex(new Index())){
                        System.out.println("ERROR: " + regionServer + " fails to create index.");
                    }
                    regionTransport.close();
                }
                System.out.println("Query OK, 0 rows affected");
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist");
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void dropIndex(String tableName, String indexName) {
        try{
            List<String> regions = tableToRegion.containsKey(tableName)? tableToRegion.get(tableName) : masterServiceClient.getRegionServers(tableName);

            // 存在表
            if(regions.size() > 0){
                tableToRegion.put(tableName, regions);

                for(String regionServer: regions){
                    TTransport regionTransport;
                    RegionService.Client regionServiceClient;
                    regionTransport = new TSocket(regionServer.split(":")[0], Integer.parseInt(regionServer.split(":")[1]));
                    regionTransport.open();

                    TProtocol protocol = new TBinaryProtocol(regionTransport);
                    regionServiceClient = new RegionService.Client(protocol);

                    //todo:dropIndex(tableName, indexName)
                    if(!regionServiceClient.dropIndex(indexName)){
                        System.out.println("ERROR: " + regionServer + " fails to create index.");
                    }
                    regionTransport.close();
                }
                System.out.println("Query OK, 0 rows affected");
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist");
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void insertTuple(String tableName, List<String> tuples) {
        try{
            List<String> regions = tableToRegion.containsKey(tableName)? tableToRegion.get(tableName) : masterServiceClient.getRegionServers(tableName);

            // 存在表
            if(regions.size() > 0){
                tableToRegion.put(tableName, regions);

                for(String regionServer: regions){
                    TTransport regionTransport;
                    RegionService.Client regionServiceClient;
                    regionTransport = new TSocket(regionServer.split(":")[0], Integer.parseInt(regionServer.split(":")[1]));
                    regionTransport.open();

                    TProtocol protocol = new TBinaryProtocol(regionTransport);
                    regionServiceClient = new RegionService.Client(protocol);

                    if(!regionServiceClient.insert(tableName, tuples)){
                        System.out.println("ERROR: " + regionServer + " fails to insert.");
                    }
                    regionTransport.close();
                }
                System.out.println("Query OK, 1 rows affected");
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist");
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTuples(String tableName, ConditionNode conditionNodes) {
        try{
            List<String> regions = tableToRegion.containsKey(tableName)? tableToRegion.get(tableName) : masterServiceClient.getRegionServers(tableName);

            // 存在表
            if(regions.size() > 0){
                tableToRegion.put(tableName, regions);
                int counts = 0;

                for(String regionServer: regions){
                    TTransport regionTransport;
                    RegionService.Client regionServiceClient;
                    regionTransport = new TSocket(regionServer.split(":")[0], Integer.parseInt(regionServer.split(":")[1]));
                    regionTransport.open();

                    TProtocol protocol = new TBinaryProtocol(regionTransport);
                    regionServiceClient = new RegionService.Client(protocol);

                    counts = regionServiceClient.deleteRecords(tableName,conditionNodes);

                    regionTransport.close();
                }
                System.out.println("Query OK, " + counts + " rows affected");
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist");
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void selectTuples(String tableName, Vector<String> attriNames, ConditionNode conditionNodes, String orderAttr, boolean ins) {
        try{
            List<String> regions = tableToRegion.containsKey(tableName)? tableToRegion.get(tableName) : masterServiceClient.getRegionServers(tableName);
            Table resTable = null;
            // 存在表
            if(regions.size() > 0){
                tableToRegion.put(tableName, regions);

                for(String regionServer: regions){
                    TTransport regionTransport;
                    RegionService.Client regionServiceClient;
                    regionTransport = new TSocket(regionServer.split(":")[0], Integer.parseInt(regionServer.split(":")[1]));
                    regionTransport.open();

                    TProtocol protocol = new TBinaryProtocol(regionTransport);
                    regionServiceClient = new RegionService.Client(protocol);

                    resTable = regionServiceClient.select(tableName, attriNames, conditionNodes, orderAttr, ins);
                    regionTransport.close();

                    if(resTable.getTuplesSize() > 0)
                        break;
                }
                // 输出属性
                for (Attribute attr : resTable.getAttributes()) {
                    System.out.print("| " + attr.getName() + "\t");
                }
                System.out.println("|");
                // 输出结果
                for (List<String> selectTuple : resTable.getTuples()) {
                    for(String value : selectTuple){
                        System.out.print("| " + value + "\t");
                    }
                    System.out.println("|");
                }
                System.out.println("Query OK, " + resTable.getTuplesSize() + " rows in set");
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist");
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void showTables() {
        try{
            List<String> regions = masterServiceClient.showTables();
            for(String table:regions){
                System.out.println(table);
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
/* thrift demo
        try{
            Config.init();

            TTransport transport;

            transport = new TSocket(Config.Master.ip, Config.Master.port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            MasterService.Client masterServiceClient = new MasterService.Client(protocol);

            String tableName = "student";
            List<Attribute> attributes = new ArrayList<>();
            for(int i = 0; i < 2; i++) attributes.add(
                    new Attribute("attribute" + i, AttributeType.INT, 4, false));
            Table table = new Table(tableName, attributes, 1, null);

            for(String regionServer: masterServiceClient.createTable(table)) System.out.println(regionServer);
            for(String regionServer: masterServiceClient.getRegionServers(tableName)) System.out.println(regionServer);
            for(String str: masterServiceClient.showTables()) System.out.println(str);

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }*/
