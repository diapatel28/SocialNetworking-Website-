package com.Social.service;

import com.Social.exception.CommentException;
import com.Social.exception.PostException;
import com.Social.exception.UserException;
import com.Social.model.Comments;

public interface CommentService {
	
	public Comments createComment(Comments comment,Integer postId,Integer userId) throws PostException, UserException;

	public Comments findCommentById(Integer commentId) throws CommentException;
	public Comments likeComment(Integer CommentId,Integer userId) 
			throws UserException, CommentException;
}
