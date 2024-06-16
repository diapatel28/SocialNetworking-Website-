package com.Social.service;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Social.config.JwtProvider;
import com.Social.dto.UserDto;
import com.Social.exception.UserException;
import com.Social.model.PasswordResetToken;
import com.Social.model.User;
import com.Social.repository.PasswordResetTokenRepository;
import com.Social.repository.UserRepository;


import jakarta.mail.MessagingException;

@Service
public class UserServiceImplementation implements UserService {


	private UserRepository userRepository;
	private JwtProvider jwtProvider;
	private PasswordEncoder passwordEncoder;
	private PasswordResetTokenRepository passwordResetTokenRepository;
	private JavaMailSender javaMailSender;
	
	public UserServiceImplementation(
			UserRepository userRepository,
			JwtProvider jwtProvider,
			PasswordEncoder passwordEncoder,
			 PasswordResetTokenRepository passwordResetTokenRepository,
			 JavaMailSender javaMailSender) {
		
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
		this.passwordEncoder = passwordEncoder;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.javaMailSender = javaMailSender;
		
	}
	
	@Override
	public User registerUser(User user) throws UserException {
		
		System.out.println("registered user ------ ");
		
		Optional<User> isEmailExist = userRepository.findByEmail(user.getEmail());
		
		if (isEmailExist.isPresent()) {
			throw new UserException("Email Already Exist");
		}
		
//		Optional<User> isUsernameTaken=userRepository.findByUsername(user.getUsername());
		
//		if(isUsernameTaken.isPresent()) {
//			throw new UserException("Username Already Taken");
//		}
		
//		if(user.getEmail()== null || 
//				user.getPassword()== null || user.getUsername()==null || user.getName()==null) {
//			throw new UserException("email,password and username are required");
//			
//		}
		
		String encodedPassword=passwordEncoder.encode(user.getPassword());
		
		User newUser=new User();
		
		newUser.setEmail(user.getEmail());
		newUser.setPassword(encodedPassword);
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		
		return userRepository.save(newUser);
		
	}

	
	@Override
	public User findUserById(Integer userId) throws UserException {
		
		Optional<User> opt = userRepository.findById(userId);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		
		throw new UserException("user not found with userid : "+userId);
	}
	
	


	@Override
	public String followUser(Integer reqUserId, Integer followUserId) throws UserException {
	    User followUser = userRepository.findById(followUserId)
	                                    .orElseThrow(() -> new UserException("User not found with ID: " + followUserId));
	    User reqUser = userRepository.findById(reqUserId)
	                                 .orElseThrow(() -> new UserException("User not found with ID: " + reqUserId));

	    // Remove followUser from reqUser's following list if it exists
	    reqUser.getFollowing().removeIf(user -> user.getId().equals(followUserId));

	    // Add followUser to reqUser's following list if it's not already there
	    if (!followUser.getFollower().contains(reqUser)) {
	        followUser.getFollower().add(reqUser);
	    }

	    userRepository.save(followUser);
	    userRepository.save(reqUser);

	    return "success";
	}


	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
	    String email = jwtProvider.getEmailFromJwtToken(jwt);

	    System.out.println("email" + email);

	    Optional<User> user = userRepository.findByEmail(email);

	    return user.orElseThrow(() -> new UserException("User not found with email: " + email));
	}

	

	@Override
	public User findUserByEmail(String username) throws UserException {
		
		Optional<User> opt=userRepository.findByEmail(username);
		
		if(opt.isPresent()) {
			User user=opt.get();
			return user;
		}
		
		throw new UserException("user not exist with username "+username);
	}


	@Override
	public List<User> findUsersByUserIds(List<Integer> userIds) {
//		List<User> users= userRepository.findAllUserByUserIds(userIds);
		
		return null;
	}


	@Override
	public Set<User> searchUser(String query) throws UserException {
		Set<User> users=userRepository.findByQuery(query);
		if(users.size()==0) {
			throw new UserException("user not exist");
		}
		return users;
	}


	@Override
	public User updateUserDetails(User updatedUser, User existingUser) throws UserException {
		
		if(updatedUser.getEmail()!= null) {
			existingUser.setEmail(updatedUser.getEmail());	
		}
		if(updatedUser.getBio()!=null) {
			existingUser.setBio(updatedUser.getBio());
		}
		if(updatedUser.getFirstName()!=null) {
			existingUser.setFirstName(updatedUser.getFirstName());
		}
		if(updatedUser.getLastName()!=null) {
			existingUser.setLastName(updatedUser.getLastName());
		}
		if(updatedUser.getMobile()!=null) {
			existingUser.setMobile(updatedUser.getMobile());
		}
		if(updatedUser.getGender()!=null) {
			existingUser.setGender(updatedUser.getGender());
		}
		if(updatedUser.getWebsite()!=null) {
			existingUser.setWebsite(updatedUser.getWebsite());
		}
		if(updatedUser.getImage()!=null) {
			existingUser.setImage(updatedUser.getImage());
		}
		
		
		if(updatedUser.getId()==existingUser.getId()) {
			System.out.println(" u "+updatedUser.getId()+" e "+existingUser.getId());
			throw new UserException("you can't update another user"); 
		}
		
		
		return userRepository.save(existingUser);
		
	}

	@Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

	@Override
	public void sendPasswordResetEmail(User user) {
		
		// Generate a random token (you might want to use a library for this)
        String resetToken = generateRandomToken();
        
        // Calculate expiry date
        Date expiryDate = calculateExpiryDate();

        // Save the token in the database
        PasswordResetToken passwordResetToken = new PasswordResetToken(resetToken,user,expiryDate);
        passwordResetTokenRepository.save(passwordResetToken);

        // Send an email containing the reset link
        sendEmail(user.getEmail(), "Password Reset", "Click the following link to reset your password: http://localhost:3000/reset-password?token=" + resetToken);
	}
	private void sendEmail(String to, String subject, String message) {
	    SimpleMailMessage mailMessage = new SimpleMailMessage();

	    mailMessage.setTo(to);
	    mailMessage.setSubject(subject);
	    mailMessage.setText(message);

	    javaMailSender.send(mailMessage);
	}
	private String generateRandomToken() {
	    return UUID.randomUUID().toString();
	}
	private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, 10);
        return cal.getTime();
    }
	
	
	
}
