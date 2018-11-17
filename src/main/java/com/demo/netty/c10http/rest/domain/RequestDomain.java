package com.demo.netty.c10http.rest.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString
public class RequestDomain implements Serializable {

	private Double height;
	private Double weight;
	private Boolean isBoy;
	private Integer age;

}
