package com.Social.service;

import java.util.List;

import com.Social.exception.UserException;
import com.Social.model.Reels;
import com.Social.model.User;

public interface ReelsService {
	
	public Reels createReel(Reels reel,User user);
	public List<Reels> findAllReels();
	public List<Reels> findUsersReel(Integer userId) throws UserException;

}
