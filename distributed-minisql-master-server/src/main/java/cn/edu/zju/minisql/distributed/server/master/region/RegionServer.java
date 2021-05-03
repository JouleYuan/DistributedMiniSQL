package cn.edu.zju.minisql.distributed.server.master.region;

import cn.edu.zju.minisql.distributed.service.RegionService;
import lombok.Data;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegionServer {
    private String path;
    private List<String> tables;
    TTransport transport;
    private RegionService.Client serviceClient;

    public RegionServer(String address, String path) {
        this.path = path;
        this.tables = new ArrayList<>();

        String[] str = address.split(":", 2);
        try {
            this.transport = new TSocket(str[0], Integer.parseInt(str[1]));
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            this.serviceClient = new RegionService.Client(protocol);
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
