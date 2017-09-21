package com.habage;

import com.habage.face.SharedService;
import com.habage.face.impl.HelloHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.zookeeper.CreateMode;

public class Zknode {

    private static SharedService.Processor<HelloHandler> processor;

    public static void main(String[] args) throws Exception {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        CuratorFramework zkClient = builder.connectString("172.19.3.162:2181").sessionTimeoutMs(3000).connectionTimeoutMs(30000)
                .canBeReadOnly(true).namespace("rpc/zkthrift").retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .defaultData(null).build();

        zkClient.start();

        HelloHandler helloHandler = new HelloHandler();
        processor = new SharedService.Processor<>(helloHandler);

        Runnable simple = new Runnable() {
            public void run() {
                serve(processor);
            }
        };

        new Thread(simple).start();

        zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/com.habage.Hello/1.0.0/localhost:9090");
    }

    private static void serve(SharedService.Processor processor) {
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
    }
}
