package com.focus.sv.ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.focus.sv.ws.data.repository.RolRepository;
import com.focus.sv.ws.model.Rol;

@Controller
@RequestMapping("test")
public class TestController {

	@Autowired
	RolRepository rolrepo;
	@RequestMapping("add-rols")
	public String addRols() {
		Rol rol = new Rol();
		rol.setName("LIBRARIAN");
		rolrepo.save(rol);
		
		Rol rol2 = new Rol();
		rol2.setName("STUDENT");
		rolrepo.save(rol2);
		return "sucess";
	}
}
