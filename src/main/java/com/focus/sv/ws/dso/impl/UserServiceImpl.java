package com.focus.sv.ws.dso.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.focus.sv.ws.data.repository.RolRepository;
import com.focus.sv.ws.data.repository.UserRepository;
import com.focus.sv.ws.dso.UserService;
import com.focus.sv.ws.dto.UserDto;
import com.focus.sv.ws.model.Rol;
import com.focus.sv.ws.model.User;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepo;
	@Autowired
	RolRepository rolRepo;
	@Autowired
	private ModelMapper mapper;

	@Override
	public User findByUserName(String username) {
		return userRepo.findByEmail(username);
	}
	@Override
	public void save(UserDto userDto) {
		User us = mapper.map(userDto, User.class);
		Rol rol = rolRepo.findByName(userDto.getRol());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		us.setPassword(encoder.encode(userDto.getPassword()));
		us.setRole(rol);
		userRepo.save(us);
	}
	@Override
	public List<UserDto> getAllUsers() {
		List<User> usersList = userRepo.findAll();
		List<UserDto> usersDtoList = usersList.stream().map(u -> mapper.map(u, UserDto.class))
				.collect(Collectors.toList());
		return usersDtoList;
	}

}
