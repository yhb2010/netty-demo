package com.demo.netty.c10http.httpjson;

import java.util.List;
import lombok.Data;

@Data
public class Customer {

	private long customerNumber;
    private String firstName;
    private String lastName;
    private List<String> middleNames;

}
