package com.habage;


import com.habage.face.SharedService;
import com.habage.face.SharedStruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.List;

public class Zkclient {
    public static void main(String[] args) throws Exception {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        CuratorFramework zkClient = builder.connectString("172.19.3.162:2181").sessionTimeoutMs(3000).connectionTimeoutMs(30000)
                .canBeReadOnly(true).namespace("rpc/zkthrift").retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .defaultData(null).build();

        zkClient.start();

        PathChildrenCache cachedPath = new PathChildrenCache(zkClient, "/com.habage.Hello/1.0.0", true);
        cachedPath.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type eventType = event.getType();
                switch (eventType) {
                    case CONNECTION_RECONNECTED:
                        System.out.println("Connection is reconection.");
                        break;
                    case CONNECTION_SUSPENDED:
                        System.out.println("Connection is suspended.");
                        break;
                    case CONNECTION_LOST:
                        System.out.println("Connection error,waiting...");
                        return;
                    case INITIALIZED: {
                        //	countDownLatch.countDown();
                        System.out.println("Connection init ...");
                    }
                    case CHILD_ADDED: {
                        cachedPath.rebuild();

                        List<ChildData> children = cachedPath.getCurrentData();
                        if (children == null || children.isEmpty()) {
                            System.out.println("child is empty ....");
                            return;
                        }

                        for (ChildData data : children) {
                            String path = data.getPath();
                            System.out.println("path:"+path);
                            String[] split = path.split("/");

                            String address = split[3];
                            String[] adds = address.split(":");

                            call_thrift(adds[0], Integer.valueOf(adds[1]));
                        }
                    }
                    default:
                        System.out.println("zkstatus:" + eventType);
                }
            }
        });
        cachedPath.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        System.in.read();
    }

    private static void call_thrift(String ip, Integer port) throws Exception {
        System.out.println(ip);
        System.out.println(port);

        TTransport transport = new TFramedTransport(new TSocket(ip, port));
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);

        SharedService.Client client = new SharedService.Client(protocol);

        SharedStruct struct = client.getStruct(234);
        System.out.println(struct.getKey());
        System.out.println(struct.getValue());

        transport.close();
    }
}
