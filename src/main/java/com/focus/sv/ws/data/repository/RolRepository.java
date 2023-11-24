package com.focus.sv.ws.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.focus.sv.ws.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{
	Rol findByName(String name);
}
