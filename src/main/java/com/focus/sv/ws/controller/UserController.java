package com.focus.sv.ws.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.focus.sv.ws.dso.UserService;
import com.focus.sv.ws.dto.UserDto;
import com.focus.sv.ws.model.User;

@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;
	public static String LIBRARIAN_ROLE = "LIBRARIAN";
	@PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody UserDto userDto
			) {
			Map<String,Object> response = new HashMap<String, Object>();
		try {		
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			 String role = authentication.getAuthorities().stream()
                     .findFirst()
                     .map(GrantedAuthority::getAuthority)
                     .orElse(null);
			 if(!LIBRARIAN_ROLE.equals(role)) {
					response.put("success", false);
					response.put("message", "Unauthorized");
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			 }
			User us = userService.findByUserName(userDto.getEmail());
			if(us != null) {
				response.put("success", false);
				response.put("message", "The user with email, alreary exists");
				return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
			}
			userService.save(userDto);
			response.put("success", true);
			response.put("message", "Your user was created successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "There was an error. ".concat(e.getMessage()));
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/find-all")
	public ResponseEntity<Object> findAll() {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			 String role = authentication.getAuthorities().stream()
                     .findFirst()
                     .map(GrantedAuthority::getAuthority)
                     .orElse(null);
			 if(!LIBRARIAN_ROLE.equals(role)) {
					response.put("success", false);
					response.put("message", "Unauthorized");
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			 }
			List<UserDto> usersList = userService.getAllUsers();
			response.put("success", true);
			response.put("users_list", usersList);
				return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "There was an error");
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
