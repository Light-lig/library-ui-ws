package com.focus.sv.ws.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.focus.sv.ws.model.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
	@EqualsAndHashCode.Include
    private Long id;
    
    private String firstName;

    private String lastName;

    @JsonIgnore
    private String password;

    private String email;

    private String rol;
    

    private List<Book> usersBooks = new ArrayList<Book>();
	public UserDto( String firstName, String lastName, String email, String rol,String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.rol = rol;
		this.password = password;
	}
}
