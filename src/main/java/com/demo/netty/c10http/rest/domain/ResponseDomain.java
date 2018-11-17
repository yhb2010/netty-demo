package com.demo.netty.c10http.rest.domain;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString
public class ResponseDomain implements Serializable {

	private String bmi;
	private String bmr;
	private String error;

}
