package com.focus.sv.ws.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.focus.sv.ws.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	public abstract User findByEmail(String userName);
}
