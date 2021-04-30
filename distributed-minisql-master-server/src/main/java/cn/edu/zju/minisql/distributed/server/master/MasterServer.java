package cn.edu.zju.minisql.distributed.server.master;

import cn.edu.zju.minisql.distributed.server.master.handler.ThriftServiceHandler;
import cn.edu.zju.minisql.distributed.service.thrift.MasterService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class MasterServer {
    public static ThriftServiceHandler handler;
    public static MasterService.Processor processor;

    public static void main(String[] args) {
        try{
            handler = new ThriftServiceHandler();
            processor = new MasterService.Processor(handler);

            Thread thriftServiceThread = new Thread(() -> {
                try {
                    TServerTransport serverTransport = new TServerSocket(9090);
                    TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

                    System.out.println("Starting the master server...");
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
