package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;

import java.util.HashMap;
import java.util.List;

public class API {
    private static TTransport masterTransport;
    private static MasterService.Client masterServiceClient;
    private static final HashMap<String,List<String>> tableToRegion = new HashMap<>(); // regionServer缓存

    public static void init(){
        try{
            Config.init();
            // connect to master server
            masterTransport = new TFramedTransport(new TSocket(Config.Master.ip, Config.Master.port));
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
            System.out.println("call master");
            masterTransport.open();
            if(masterServiceClient.getRegionServers(tableName).isEmpty()){
                System.out.println("call master");
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
        } finally {
            masterTransport.close();
        }
    }

    public static void dropTable(String tableName) {
        try{
            // 存在表
            masterTransport.open();
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
        } finally {
            masterTransport.close();
        }
    }

    public static void regionSQL(String tableName, String sql, boolean isWrite){
        List<String> regions;
        //System.out.println(sql);
        try{
            masterTransport.open();
            if(isWrite){
                regions = masterServiceClient.getRegionServers(tableName);
            }else{
                regions = tableToRegion.containsKey(tableName)? tableToRegion.get(tableName) : masterServiceClient.getRegionServers(tableName);
            }
            masterTransport.close();

            // 存在表
            if(regions.size() > 0){
                String response = "ERROR: Region servers failed.";
                tableToRegion.put(tableName, regions);

                for(String regionServer: regions){

                    System.out.println(regionServer);
                    String regionIP = regionServer.split(":")[0];
                    int regionPort = Integer.parseInt(regionServer.split(":")[1]);

//                    TTransport regionTransport;
//                    RegionService.Client regionServiceClient;
//                    regionTransport = new TSocket(regionServer.split(":")[0], Integer.parseInt(regionServer.split(":")[1]));
//                    regionTransport.open();
//
//                    TProtocol protocol = new TBinaryProtocol(regionTransport);
//                    regionServiceClient = new RegionService.Client(protocol);

                    try (TTransport transport = new TFramedTransport(new TSocket(regionIP, regionPort))) {
                        TProtocol protocol = new TBinaryProtocol(transport);
                        RegionService.Client client = new RegionService.Client(protocol);
                        transport.open();

                        String result = client.sqlRequest(sql);
                        System.out.println(result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*
                    TTransport transport = new TSocket(regionIP, regionPort);
                    try{
                        transport.open();
                        TProtocol protocol = new TBinaryProtocol(transport);
                        RegionService.Client client = new RegionService.Client(protocol);

                        String result = client.sqlRequest(sql);
                        System.out.println(result);

                    } catch (TException e) {
                        e.printStackTrace();
                    } finally {
                        transport.close();
                    }*/

//                    response = regionServiceClient.sqlRequest(sql);
//                    regionTransport.close();

                    if(!isWrite) break;
                }

                System.out.println(response);
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist");
            }
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            masterTransport.close();
        }
    }

    public static void showTables() {
        try{
            masterTransport.open();
            List<String> regions = masterServiceClient.showTables();
            for(String table:regions){
                System.out.println(table);
            }
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            masterTransport.close();
        }
    }
}
