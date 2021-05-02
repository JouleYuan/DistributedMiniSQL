package cn.edu.zju.minisql.distributed.server.master.service;

import cn.edu.zju.minisql.distributed.server.master.service.impl.ThriftServiceImpl;
import cn.edu.zju.minisql.distributed.service.MasterService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class ThriftService {
    public static ThriftServiceImpl handler;
    public static MasterService.Processor processor;

    public static void exposeInterface(int port) {
        try{
            handler = new ThriftServiceImpl();
            processor = new MasterService.Processor(handler);

            Thread thriftServiceThread = new Thread(() -> {
                try {
                    TServerTransport serverTransport = new TServerSocket(port);
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
