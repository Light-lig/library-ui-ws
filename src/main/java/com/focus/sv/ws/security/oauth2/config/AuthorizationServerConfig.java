package com.focus.sv.ws.security.oauth2.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;

import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;



@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter 
{
		@Autowired 
		private Environment env;
	 	@Autowired private AuthenticationManager authenticationManager; 
		@Autowired
		private PasswordEncoder passwordEncoder;
		@Autowired
		private CustomTokenEnhancer customTokenEnhancer;
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients
				.inMemory()
					.withClient(env.getProperty("config.security.oauth.client.id"))
					.secret(passwordEncoder.encode(env.getProperty("config.security.oauth.client.secret")))
					.scopes("read", "write")
					.authorizedGrantTypes("password", "refresh_token")

					.accessTokenValiditySeconds(3600)
					.refreshTokenValiditySeconds(3600)
			;
		}
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer,accessTokenConverter()));
			endpoints.authenticationManager(authenticationManager)
				.tokenStore(tokenStore())
				.accessTokenConverter(accessTokenConverter())
				.tokenEnhancer(tokenEnhancerChain)
				.exceptionTranslator(loggingExceptionTranslator());	
		}
		
	    @Bean
	    WebResponseExceptionTranslator<OAuth2Exception> loggingExceptionTranslator() {
	        return new DefaultWebResponseExceptionTranslator() {
	            @Override
	            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

	                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
	                HttpHeaders headers = new HttpHeaders();
	                headers.setAll(responseEntity.getHeaders().toSingleValueMap());
	                OAuth2Exception excBody = responseEntity.getBody();
	                return new ResponseEntity<>(excBody, headers, responseEntity.getStatusCode());
	            }
	        };
	    }
		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()");
		}
		
		  @Bean
		    JwtTokenStore tokenStore() {
		        return new JwtTokenStore(accessTokenConverter());
		    }
		  
			@Bean
			JwtAccessTokenConverter accessTokenConverter() {
				JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter(); 
				tokenConverter.setSigningKey(env.getProperty("config.security.oauth.jwt.key"));
				return tokenConverter;
			}
}
