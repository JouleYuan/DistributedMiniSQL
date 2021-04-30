package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.thrift.Attribute;
import cn.edu.zju.minisql.distributed.service.thrift.MasterService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        try{
            TTransport transport;

            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            MasterService.Client masterServiceClient = new MasterService.Client(protocol);

            String tableName = "student";
            List<Attribute> attributes = new ArrayList<>();
            for(int i = 0; i < 2; i++){
                attributes.add(new Attribute("attribute" + i, i));
            }
            int primaryKey = 0;

            System.out.println(masterServiceClient.createTable(tableName, attributes, primaryKey));
            System.out.println(masterServiceClient.dropTable(tableName));

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
