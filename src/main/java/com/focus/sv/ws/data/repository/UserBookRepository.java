package com.focus.sv.ws.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.focus.sv.ws.model.UserBook;

public interface UserBookRepository extends JpaRepository<UserBook, Long>{
	@Query("SELECT ub FROM UserBook ub where ub.user.id = :userId and ub.state = 'REQUESTED'")
	List<UserBook> findByUser(@Param("userId") Long userId);
	
	List<UserBook> findByState(String State);
}
