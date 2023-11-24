package com.focus.sv.ws.utils;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.focus.sv.ws.data.repository.RolRepository;
import com.focus.sv.ws.model.Rol;

@Component
public class DataSeeder implements CommandLineRunner{

	@Autowired RolRepository repo;
	@Transactional
	@Override
	public void run(String... args) throws Exception {
		List<Rol> rolList = repo.findAll();
		System.out.println(rolList.size() == 0);
		if(rolList.size() == 0) {
			Rol rol = new Rol();
			rol.setName("LIBRARIAN");
			repo.save(rol);
			
			Rol rol2 = new Rol();
			rol2.setName("STUDENT");
			repo.save(rol2);
		}
		
	}

}
