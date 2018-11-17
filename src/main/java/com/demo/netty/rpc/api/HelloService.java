package com.demo.netty.rpc.api;

import com.demo.netty.rpc.domain.Person;

public interface HelloService {

	String hello(String name);

    String hello(Person person);

}
