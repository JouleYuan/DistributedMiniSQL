package cn.edu.zju.minisql.distributed.server.master.service;

import cn.edu.zju.minisql.distributed.server.master.service.impl.ThriftServiceImpl;
import cn.edu.zju.minisql.distributed.service.MasterService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.layered.TFramedTransport;

public class ThriftService {
    public static ThriftServiceImpl handler;
    public static MasterService.Processor<MasterService.Iface> processor;

    public static void exposeInterface(int port) {
        try{
            handler = new ThriftServiceImpl();
            processor = new MasterService.Processor<>(handler);

            Thread thriftServiceThread = new Thread(() -> {
                try {
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
