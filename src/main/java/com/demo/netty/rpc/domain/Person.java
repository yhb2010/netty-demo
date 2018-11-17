package com.demo.netty.rpc.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@ToString
@Getter
@Setter
public class Person implements Serializable {

	private String firstName;
    private String lastName;

}
