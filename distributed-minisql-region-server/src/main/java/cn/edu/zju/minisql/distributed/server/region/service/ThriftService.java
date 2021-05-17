package cn.edu.zju.minisql.distributed.server.region.service;

import cn.edu.zju.minisql.distributed.server.region.service.Impl.ThriftServiceImpl;
import cn.edu.zju.minisql.distributed.service.RegionService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.layered.TFramedTransport;

public class ThriftService {
    public static ThriftServiceImpl handler;
    public static RegionService.Processor<RegionService.Iface> processor;

    public static void exposeInterface(int port) {
        try{
            handler = new ThriftServiceImpl();
            processor = new RegionService.Processor<>(handler);

            Thread thriftServiceThread = new Thread(() -> {
                try {
                    /*
                    TServerTransport serverTransport = new TServerSocket(port);
                    TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

                    server.serve();
                    */

                    TNonblockingServerSocket serverSocket = new TNonblockingServerSocket(port);
                    TThreadedSelectorServer.Args ttssArgs = new TThreadedSelectorServer.Args(serverSocket);
                    ttssArgs.processor(processor);
                    ttssArgs.protocolFactory(new TBinaryProtocol.Factory());
                    ttssArgs.transportFactory(new TFramedTransport.Factory());
                    TThreadedSelectorServer server = new TThreadedSelectorServer(ttssArgs);

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
