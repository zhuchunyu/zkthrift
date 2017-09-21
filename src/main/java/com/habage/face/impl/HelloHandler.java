package com.habage.face.impl;


import com.habage.face.SharedService;
import com.habage.face.SharedStruct;
import org.apache.thrift.TException;

public class HelloHandler implements SharedService.Iface {
    @Override
    public SharedStruct getStruct(int key) throws TException {
        System.out.println("receive:"+key);
        SharedStruct sharedStruct = new SharedStruct();
        sharedStruct.key = key;
        sharedStruct.value = "value:"+key;
        return sharedStruct;
    }
}
