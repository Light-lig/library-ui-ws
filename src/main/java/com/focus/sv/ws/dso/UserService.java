package com.focus.sv.ws.dso;

import java.util.List;

import com.focus.sv.ws.dto.UserDto;
import com.focus.sv.ws.model.User;

public interface UserService {

	User findByUserName(String username);
	void save(UserDto userDto);
	List<UserDto> getAllUsers();
}
