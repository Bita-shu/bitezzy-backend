package com.example.Final.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Final.user.User;

@Repository
public interface Repo extends JpaRepository<User, Integer> {
	
	User findByEmail (String email);
	
	boolean existsByEmail(String email);
	
	User findByResetToken (String resetToken);
	
	boolean existsByResetToken(String resetToken);

}
