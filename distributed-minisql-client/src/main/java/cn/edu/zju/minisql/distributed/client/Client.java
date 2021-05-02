package cn.edu.zju.minisql.distributed.client;

import cn.edu.zju.minisql.distributed.service.Attribute;
import cn.edu.zju.minisql.distributed.service.AttributeType;
import cn.edu.zju.minisql.distributed.service.MasterService;
import cn.edu.zju.minisql.distributed.service.Table;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Client {
    public static void main(String[] args) {
        try{
            Properties properties = new Properties();
            InputStream inputStream = Object.class.getResourceAsStream("/config.properties");
            properties.load(inputStream);

            TTransport transport;

            transport = new TSocket(properties.getProperty("master.ip"),
                    Integer.parseInt(properties.getProperty("master.port")));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
