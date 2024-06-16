package com.Social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Social.model.Comments;


public interface CommentRepository extends JpaRepository<Comments, Integer> {

}
