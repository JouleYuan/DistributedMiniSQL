package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class API {
    private static TTransport masterTransport;
    private static MasterService.Client masterServiceClient;
    private static final HashMap<String,List<String>> tableToRegion = new HashMap<>(); // regionServer缓存

    public static void init(){
        try{
            Config.init();

            masterTransport = new TFramedTransport(new TSocket(Config.Master.ip, Config.Master.port));
            TProtocol protocol = new TBinaryProtocol(masterTransport);
            masterServiceClient = new MasterService.Client(protocol);
            masterTransport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        masterTransport.close();
    }

    public static void createTable(String tableName, Table newTable) {
        try{
            masterTransport.open();
            if(masterServiceClient.getRegionServers(tableName).isEmpty()){
                List<String> regions =  masterServiceClient.createTable(newTable);
                if(regions.size() > 0){
                    tableToRegion.put(tableName, regions);
                    System.out.println("Query OK, 0 rows affected\n");
                }
                else{
                    System.out.println("ERROR: Fail to create table " + tableName + "\n");
                }
            }
            else{
                System.out.println("ERROR: Table " + tableName + " already exists.\n");
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
            if(!masterServiceClient.getRegionServers(tableName).isEmpty()){
                if(masterServiceClient.dropTable(tableName)){
                    tableToRegion.remove(tableName);
                    System.out.println("Query OK, 0 rows affected\n");
                }
                else{
                    System.out.println("ERROR: Fail to drop table " + tableName + "\n");
                }
            }
            else{
                System.out.println("ERROR: Unknown table " + tableName + "\n");
            }
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            masterTransport.close();
        }
    }

    public static void regionSQL(String tableName, String sql, boolean isWrite){
        List<String> regions;
        try{

            if(!isWrite && tableToRegion.containsKey(tableName)){
                regions = tableToRegion.get(tableName);
            }else{
                masterTransport.open();
                regions = masterServiceClient.getRegionServers(tableName);
                masterTransport.close();
            }

            // 存在表
            if(!regions.isEmpty()){
                String response = "ERROR: Region servers failed.\n";
                List<String> regionsCopy = new ArrayList<>(regions);
                for(String regionServer: regionsCopy) {
                    String regionIP = regionServer.split(":")[0];
                    int regionPort = Integer.parseInt(regionServer.split(":")[1]);

                    try (TTransport transport = new TFramedTransport(new TSocket(regionIP, regionPort))) {
                        TProtocol protocol = new TBinaryProtocol(transport);
                        RegionService.Client client = new RegionService.Client(protocol);
                        transport.open();
                        response = client.sqlRequest(sql);
                        if(response.startsWith("The table ")) {
                            regions.remove(regionServer);
                            continue;
                        }
                        if(!isWrite) break;
                    } catch (TException e) {
                        regions.remove(regionServer);
                    }
                }
                if (regions.isEmpty()) {
                    masterTransport.open();
                    regions = masterServiceClient.getRegionServers(tableName);
                    masterTransport.close();
                    if(!regions.isEmpty()) {
                        regionsCopy = new ArrayList<>(regions);
                        for(String regionServer: regionsCopy) {
                            String regionIP = regionServer.split(":")[0];
                            int regionPort = Integer.parseInt(regionServer.split(":")[1]);

                            try (TTransport transport = new TFramedTransport(new TSocket(regionIP, regionPort))) {
                                TProtocol protocol = new TBinaryProtocol(transport);
                                RegionService.Client client = new RegionService.Client(protocol);
                                transport.open();
                                response = client.sqlRequest(sql);
                                if(response.startsWith("The table ")) {
                                    regions.remove(regionServer);
                                    continue;
                                }
                                if(!isWrite) break;
                            } catch (TException e) {
                                regions.remove(regionServer);
                            }
                        }
                        if (regions.isEmpty()) tableToRegion.remove(tableName);
                        else tableToRegion.put(tableName, regions);
                    } else {
                        tableToRegion.remove(tableName);
                        response = "ERROR: Table " + tableName + " doesn't exist\n";
                    }
                } else {
                    tableToRegion.put(tableName, regions);
                }
                System.out.println(response);
            }
            else{
                System.out.println("ERROR: Table " + tableName + " doesn't exist\n");
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
                System.out.print(table + " ");
            }
            System.out.println();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            masterTransport.close();
        }
    }
}
