package com.focus.sv.ws.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Component
public class XmlQueriesAnnotationProcessor implements BeanPostProcessor{
	public XmlQueriesAnnotationProcessor(List<XmlMarshalledObjectQuery> applicationQueries) {
		XmlQueriesUtil.applicationQueries = applicationQueries;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean.getClass().isAnnotationPresent(Service.class) || bean.getClass().isAnnotationPresent(Component.class)) {
			ReflectionUtils.doWithLocalFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
				public void doWith(Field field) { 
					if(!StringUtils.isAllUpperCase(field.getName().replaceAll("[^a-zA-Z0-9]", ""))) return;
					ReflectionUtils.makeAccessible(field); 
					try { 
						String queryByName = XmlQueriesUtil.getQueryByName(field.getName());
						if(StringUtils.isBlank(queryByName)) return;
						field.set(bean, XmlQueriesUtil.getQueryByName(field.getName()));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			});
		}
		return bean; 
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
