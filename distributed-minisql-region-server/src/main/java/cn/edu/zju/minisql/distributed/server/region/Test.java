package cn.edu.zju.minisql.distributed.server.region;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import cn.edu.zju.minisql.distributed.service.RegionService;

import cn.edu.zju.minisql.distributed.server.region.FTPTransferor;

public class Test {
    public static void main(String[] args){
        sqlRequestTest();
    }

    public static void ftpTest(){
        System.setProperty("user.dir", "./");
        System.setProperty("env", "unix");

        String tableName = "author";
        String remoteIP = "182.92.71.45";
        String remotePath = "./";
        boolean res = FTPTransferor.fromLocalFTPto(tableName, remoteIP, remotePath);
        if(res){
            System.out.println("success");
        }else{
            System.out.println("failed");
        }
    }

    public static void sqlRequestTest(){
        String[] statements = {
                //"create table author (a_id int, a_name char(100), primary key(a_name) );",
                "show tables;",
                "insert into author values(8, 'naah');",

                "select * from author where a_name <'naat' and a_id<13;",
                //"quit;"
        };

        System.out.println("start");
        TTransport transport = null;
        try{
            transport = new TSocket(null,"localhost", 7999, 30000);
            TProtocol protocol = new TBinaryProtocol(transport);
            RegionService.Client client = new RegionService.Client(protocol);
            transport.open();
            for(String statement: statements){
                String result = client.sqlRequest(statement);
                System.out.println(result);

            }
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }
}
