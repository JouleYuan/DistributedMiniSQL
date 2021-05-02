package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.Attribute;
import cn.edu.zju.minisql.distributed.service.AttributeType;
import cn.edu.zju.minisql.distributed.service.MasterService;
import cn.edu.zju.minisql.distributed.service.Table;
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
            for(int i = 0; i < 2; i++) attributes.add(
                    new Attribute("attribute" + i, AttributeType.INT, 4, false));
            Table table = new Table(tableName, attributes, 1, null);

            for(String regionServer: masterServiceClient.createTable(table)) System.out.println(regionServer);
            System.out.println(masterServiceClient.dropTable(tableName));

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
