package com.Social.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Social.config.JwtProvider;
import com.Social.exception.UserException;
import com.Social.model.User;
import com.Social.model.VerifyOtpRequest;
import com.Social.repository.UserRepository;
import com.Social.request.LoginRequest;
import com.Social.response.AuthResponse;
import com.Social.service.CustomeUserServiceImplementation;
import com.Social.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private JwtProvider jwtProvider;
	private CustomeUserServiceImplementation customUserDetails;

	private Map<String, String> otpMap = new HashMap<>();
	@Autowired
	private EmailService emailService;
	
	
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
			CustomeUserServiceImplementation customUserDetails) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider;
		this.customUserDetails = customUserDetails;

	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody User user , @RequestParam String otp) throws UserException {

		String email = user.getEmail();
		String password = user.getPassword();
		String fullName = user.getFirstName();
		String lastName=user.getLastName();
		String gender=user.getGender();
		
		if (!otpMap.containsKey(email) || !otpMap.get(email).equals(otp)) {
            throw new UserException("Invalid OTP");
        }
		
		
		
		System.out.println("email "+email+" - "+fullName);

		Optional<User> isEmailExist = userRepository.findByEmail(email);

		if (isEmailExist.isPresent()) {

			throw new UserException("Email Is Already Used With Another Account");
		}

	
		User createdUser = new User();
		createdUser.setEmail(email);
		createdUser.setFirstName(fullName);
		createdUser.setLastName(lastName);
		createdUser.setGender(gender);
		createdUser.setPassword(passwordEncoder.encode(password));
		
	

		User savedUser = userRepository.save(createdUser);

		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtProvider.generateToken(authentication);

		AuthResponse authResponse=new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("Registration Successfull");

		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

	}
	
	@PostMapping("/generateOTP")
	public ResponseEntity<?> generateOTP(@Valid @RequestBody Map<String, String> request) {
	    String email = request.get("email");
	    // Generate OTP
	    String otp = generateOTP();
	    // Send OTP to email (You can use email sending service here)
	    emailService.sendOTP(email, otp);
	    System.out.println("OTP for " + email + " is: " + otp);
	    // Store OTP in a map temporarily
	    otpMap.put(email, otp);
	    return ResponseEntity.ok().build();
	}


	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) throws UserException {

		String username = loginRequest.getEmail();
		String password = loginRequest.getPassword();
		
//		Optional<User> user = userRepository.findByEmail(username);
		User user=new User();
//		if(user.isEmpty()) {
//			throw new UserException("user not found with username  "+ username);
//		}

		System.out.println(username + " ----- " + password);

		Authentication authentication = authenticate(username, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
//
		String token = jwtProvider.generateToken(authentication);
		AuthResponse authResponse = new AuthResponse();

		authResponse.setMessage("Login Success");
		authResponse.setJwt(token);

		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
	}

	private Authentication authenticate(String username, String password) {
		UserDetails userDetails = customUserDetails.loadUserByUsername(username);

		System.out.println("sign in userDetails - " + userDetails);

		if (userDetails == null) {
			System.out.println("sign in userDetails - null " + userDetails);
			throw new BadCredentialsException("Invalid username or password");
		}
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			System.out.println("sign in userDetails - password not match " + userDetails);
			throw new BadCredentialsException("Invalid username or password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
	
	private String generateOTP() {
        // Generate a 6-digit OTP
        return String.format("%06d", new Random().nextInt(999999));
    }
	
	
}
