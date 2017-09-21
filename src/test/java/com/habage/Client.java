package com.habage;


import com.habage.face.SharedService;
import com.habage.face.SharedStruct;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("hello client");

        TTransport transport = new TFramedTransport(new TSocket("localhost", 9090));
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);

        SharedService.Client client = new SharedService.Client(protocol);

        SharedStruct struct = client.getStruct(234);
        System.out.println(struct.getKey());
        System.out.println(struct.getValue());

        transport.close();
    }
}
