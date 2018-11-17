package com.demo.netty.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cdel.util.helper.SpringContextUtil;
import com.demo.netty.rpc.registry.ServiceRegistry;
import com.demo.netty.rpc.registry.zookeeper.ZooKeeperServiceRegistry;
import com.demo.netty.rpc.service.RpcServer;

@SpringBootApplication
public class RpcBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcBootstrap.class);

    @Value("${registry.address}")
	private String registry_address;
    @Value("${server.address}")
    private String server_address;

    public static void main(String[] args) throws InterruptedException {
    	LOGGER.warn("start server");
		SpringContextUtil.setApplicationContext(SpringApplication.run(RpcBootstrap.class, args));
	}

    @Bean
    public ServiceRegistry serviceRegistry(){
    	return new ZooKeeperServiceRegistry(registry_address);
    }

    @Bean
    public RpcServer rpcServer(){
    	return new RpcServer(server_address, serviceRegistry());
    }

}