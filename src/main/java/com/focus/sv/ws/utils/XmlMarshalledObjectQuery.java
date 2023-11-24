package com.focus.sv.ws.utils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

public class XmlMarshalledObjectQuery implements Comparable<XmlMarshalledObjectQuery> {

	@XmlAttribute(name = "name")
	private String name;
	@XmlValue
	private String value;

	@XmlTransient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int compareTo(XmlMarshalledObjectQuery o) {
		return name.compareTo(o.getName());
	}
}
