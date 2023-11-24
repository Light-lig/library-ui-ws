package com.focus.sv.ws.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "queries")
public class XmlMarshalledObject {
	public List<XmlMarshalledObjectQuery> query = new ArrayList<XmlMarshalledObjectQuery>();

	@XmlTransient
	public List<XmlMarshalledObjectQuery> getQuery() {
		return query;
	}

	public void setQuery(List<XmlMarshalledObjectQuery> query) {
		this.query = query;
	}
}
