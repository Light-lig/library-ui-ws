package com.focus.sv.ws.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.focus.sv.ws.dso.UserService;
import com.focus.sv.ws.dto.UserDto;
import com.focus.sv.ws.model.User;

@Controller
@RequestMapping("register")
public class RegisterController {
	@Autowired
	private UserService userService;
	@PostMapping("/save")
	public ResponseEntity<Object> register(@RequestBody UserDto userDto
			) {
			Map<String,Object> response = new HashMap<String, Object>();
		try {		
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
}
