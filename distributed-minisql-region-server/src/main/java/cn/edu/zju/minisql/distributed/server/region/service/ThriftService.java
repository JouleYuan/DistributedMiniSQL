package cn.edu.zju.minisql.distributed.server.region.service;

import cn.edu.zju.minisql.distributed.server.region.service.Impl.ThriftServiceImpl;
import cn.edu.zju.minisql.distributed.service.RegionService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class ThriftService {
    public static ThriftServiceImpl handler;
    public static RegionService.Processor processor;

    public static void exposeInterface() {
        try{
            handler = new ThriftServiceImpl();
            processor = new RegionService.Processor(handler);

            Thread thriftServiceThread = new Thread(() -> {
                try {
                    TServerTransport serverTransport = new TServerSocket(9091);
                    TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

                    server.serve();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thriftServiceThread.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
