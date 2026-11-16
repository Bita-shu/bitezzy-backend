package com.example.Final.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Final.service.FinalService;
import com.example.Final.user.User;
import com.example.demo.loginPage.LoginPage;

import jakarta.mail.MessagingException;

@RestController
@CrossOrigin(origins = "*")
public class FinalController {

	@Autowired
	FinalService userService;
	
	@PostMapping("/register/User-Detail/")
	public ResponseEntity<String> RegisterUserDetails(@RequestParam(value = "file", required = false) MultipartFile file ,
			                                          @RequestParam("email") String email,
	                                                  @RequestParam("name") String name,
	                                                  @RequestParam("password") String password,
	                                                  @RequestParam("confirmPassword")String confirmPassword,
	                                                  @RequestParam("age") int age ,
	                                                  @RequestParam("phone")String phone){
		
		String str= userService.ulpoadNewUserDetails(file,email,name,password,confirmPassword,age,phone);
		 
		if ("OTP Send Successfully ".equals(str)) {
			return ResponseEntity.ok(str + email);
		} else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		}
	}
	
	@PostMapping("/OTP-verification/")
	public ResponseEntity<String> verfiyByOTP(@RequestParam("email") String email, @RequestParam("otp") String otp){
		String str=userService.OTPVerification(email,otp);
		if ("OTP Verified".equalsIgnoreCase(str.trim())) {
			return ResponseEntity.ok(str);
		} else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		}
	}
	
	@PostMapping("/Forgot-password/reset-link/")
	public ResponseEntity<String> resetLink (@RequestParam("email") String email) throws MessagingException{
		String str=userService.resetLink(email);
		if ("Password reset Link sent to your email successfully".equalsIgnoreCase(str.trim())) {
			return ResponseEntity.ok(str);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		}
	} 
	
	@PostMapping("/reset/forgot-password/confirm-new-password/")
	public ResponseEntity<String> confirmNewPassword(@RequestParam("token") String token, 
			                                         @RequestParam("newPassword") String newPassword, 
			                                         @RequestParam("confirmPassword") String confirmPassword){
		String str=userService.confirmNewPassword(token,newPassword,confirmPassword);
		if ("New password updated".equalsIgnoreCase(str.trim())) {
			return ResponseEntity.ok(str);
		} else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		} 
	}
	
	@PostMapping("/Login-page/")
	public ResponseEntity<String> mainLoginPage(@RequestBody LoginPage userLogin){
		String str=userService.userPageLogin(userLogin);
		if ("LoggedIn successfully".equalsIgnoreCase(str.trim())) {
			return ResponseEntity.ok(str);
		} else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		}
	}
	
	@GetMapping("/user-detail/{email}")
	public ResponseEntity<Object> getUserByEmail(@PathVariable("email") String email) {
		User user= userService.getUserDetails(email);
		if (user!= null) {
			return ResponseEntity.ok(user);
		} else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found");
		}
	}
	
	@PutMapping("/Edit/user-profile/")
	public ResponseEntity<String> updateUserProfile(@RequestParam(value = "file", required = false) MultipartFile file,
			                                        @RequestParam("email") String email,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("age") int age ,
                                                    @RequestParam("phone")String phone){
		String str=userService.updateUser(file,email,name,age,phone);
		if ("User updated successfully".equalsIgnoreCase(str.trim())) {
			return ResponseEntity.ok(str);
		} else { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		}
	}
	
	@DeleteMapping("/delete/user-account/{email}")
	public ResponseEntity<String> removeUserAccount(@PathVariable("email") String email){
		String str=userService.deleteUserAccount(email);
		if ("Account deleted successfully".equalsIgnoreCase(str.trim())) {
			return ResponseEntity.ok(str);
		} else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(str);
		}
	}
	@GetMapping("/user-details")
	public List<User> AllUser(){
		return userService.getAllUserDetails();
	}
}
