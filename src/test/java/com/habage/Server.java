package com.habage;


import com.habage.face.SharedService;
import com.habage.face.impl.HelloHandler;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Server {

    private static SharedService.Processor<HelloHandler> processor;

    public static void main(String[] args) {

        /*HelloHandler helloHandler = new HelloHandler();
        processor = new SharedService.Processor<>(helloHandler);

        Runnable simple = new Runnable() {
            public void run() {
                simple(processor);
            }
        };

        new Thread(simple).start();*/
    }

    /*private static void simple(SharedService.Processor processor) {
        try {
            //TServerTransport serverTransport = new TServerSocket(9090);
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(9090);

            //TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();

            //TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server... 9090");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
