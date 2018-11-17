package com.demo.netty.rpc.service;

import com.demo.netty.rpc.api.HelloService;
import com.demo.netty.rpc.domain.Person;

@RpcService(value = HelloService.class, version = "sample.hello2")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return "你好! " + name;
    }

    @Override
    public String hello(Person person) {
        return "你好! " + person.getFirstName() + " " + person.getLastName();
    }

}