package com.focus.sv.ws.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XmlQueriesUtil {
	final static Logger logger = LogManager.getLogger(XmlQueriesUtil.class);
	public static final String DEFAULT_WEB_ENCODING = "ISO-8859-1";

	public static List<XmlMarshalledObjectQuery> applicationQueries;

	private static XmlMarshalledObject unmarshallXml(String xmlString) {
		XmlMarshalledObject output = new XmlMarshalledObject();
		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(XmlMarshalledObject.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			output = (XmlMarshalledObject) unmarshaller.unmarshal(new StringReader(xmlString));
		}
		catch (JAXBException e) {
			logger.error("Error convirtiendo objeto a XML: " + e.getMessage(), e);
		}
		return output;
	}
	public static List<XmlMarshalledObjectQuery> loadQueries(String location) {
		XmlMarshalledObject object = null;
		List<String> xmlQueryFiles = getXmlFiles(location);
		String xmlString = StringUtils.EMPTY;
		List<XmlMarshalledObjectQuery> gestorQueries = new ArrayList<XmlMarshalledObjectQuery>();
		for (String queryFile : xmlQueryFiles) {
			xmlString = getXmlContent(location + "/" + queryFile);
			object = unmarshallXml(xmlString);
			gestorQueries.addAll(object.getQuery());
		}
		Collections.sort(gestorQueries);
		return gestorQueries;
	}
	
	private static String getXmlContent(String xmlFileName) {
		String xmlString = StringUtils.EMPTY;
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
			xmlString = IOUtils.toString(inputStream, DEFAULT_WEB_ENCODING);
		}
		catch (Exception e) {
			logger.error("Error leyendo archivo XML: " + e.getMessage(), e);
		}
		return xmlString;
	}
	
	private static List<String> getXmlFiles(String xmlPathLocation) {
		List<String> files = new ArrayList<String>();
		URL url = null;
		try {
			url = Thread.currentThread().getContextClassLoader().getResource(xmlPathLocation);
			File folder = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
			File[] listOfFiles = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
			});
			for (File file : listOfFiles)
				if (file.isFile())
					files.add(file.getName());
		} catch (Exception e) {
			try {
				url = Thread.currentThread().getContextClassLoader().getResource(StringUtils.EMPTY);
				ZipInputStream zipInputStream = new ZipInputStream(url.openStream());
				ZipEntry zipEntry = null;
				String entryName = null;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					entryName = zipEntry.getName();
					if ((StringUtils.contains(entryName, "WEB-INF/classes/" + xmlPathLocation) || StringUtils.contains(entryName, "BOOT-INF/classes/" + xmlPathLocation))
							&& StringUtils.endsWith(entryName, ".xml")) {
						files.add(StringUtils.substringAfterLast(entryName, "/"));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return files;
	}	
	public static String getQueryByName(String queryName) {
		String query = StringUtils.EMPTY;
		XmlMarshalledObjectQuery objectToFind = new XmlMarshalledObjectQuery();
		objectToFind.setName(queryName);
		int index = Collections.binarySearch(applicationQueries, objectToFind);
		if (index >= 0) query = applicationQueries.get(index).getValue();
		return query;
	}
}
