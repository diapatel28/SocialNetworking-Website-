package com.Social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Social.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

	PasswordResetToken findByToken(String token);

}
