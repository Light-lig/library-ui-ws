package com.focus.sv.ws.security.oauth2.config;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.focus.sv.ws.data.repository.UserRepository;

@Component
public class CustomTokenEnhancer implements TokenEnhancer{

	@Autowired
	UserRepository userRepo;
	@Transactional
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		// TODO Auto-generated method stub
		User user = (User) authentication.getPrincipal();
	     Map<String, Object> additionalInfo = new HashMap<>();
	        com.focus.sv.ws.model.User us = userRepo.findByEmail(user.getUsername());
	        ObjectMapper mapper = new ObjectMapper();
	        Map<String,Object> map = mapper.convertValue(us,  new TypeReference<Map<String, Object>>() {});
	        additionalInfo.put("userinfo", map);
	        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
	        return accessToken;
	}

}
