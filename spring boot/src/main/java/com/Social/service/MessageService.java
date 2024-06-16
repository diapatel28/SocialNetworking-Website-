package com.Social.service;

import java.util.List;

import com.Social.exception.ChatException;
import com.Social.exception.MessageException;
import com.Social.exception.UserException;
import com.Social.model.Message;
import com.Social.request.SendMessageRequest;

public interface MessageService  {
	
	public Message sendMessage(SendMessageRequest req) throws UserException, ChatException;
	
	public List<Message> getChatsMessages(Integer chatId) throws ChatException;
	
	public Message findMessageById(Integer messageId) throws MessageException;
	
	public String deleteMessage(Integer messageId) throws MessageException;

}
