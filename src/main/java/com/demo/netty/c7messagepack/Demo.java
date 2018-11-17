package com.demo.netty.c7messagepack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

public class Demo {

	public static void main(String[] args) throws IOException {
		List<String> src = new ArrayList<>();
		src.add("msgpack");
		src.add("kumofs");
		src.add("viver");
		MessagePack mspack = new MessagePack();
		byte[] raw = mspack.write(src);
		List<String> dst = mspack.read(raw, Templates.tList(Templates.TString));
		System.out.println(dst.get(0));
		System.out.println(dst.get(1));
		System.out.println(dst.get(2));
	}

}
