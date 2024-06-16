package com.Social.service;

import java.util.List;

import com.Social.exception.StoryException;
import com.Social.exception.UserException;
import com.Social.model.Story;

public interface StoryService {

	public Story createStory(Story story,Integer userId) throws UserException;
	
	public List<Story> findStoryByUserId(Integer userId) throws UserException, StoryException;
	
	
}
