package com.demo.netty.rpc.service;

import com.demo.netty.rpc.api.HelloService;
import com.demo.netty.rpc.domain.Person;

//第二步：编写服务接口的实现类
@RpcService(HelloService.class) // 指定远程接口
public class HelloServiceImpl implements HelloService {

	@Override
    public String hello(String name) {
		try {
			//Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
    	try {
			//Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }

}