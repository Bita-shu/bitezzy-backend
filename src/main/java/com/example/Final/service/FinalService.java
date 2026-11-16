package com.example.Final.service;

import com.example.Final.controller.FinalController;
import com.example.demo.FinalApplication;
import com.example.demo.loginPage.LoginPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Final.user.User;
import com.example.Final.user.repository.Repo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class FinalService {
	@Autowired
	Repo userRepo;
	
	@Autowired
	JavaMailSender mailSender;
	
	@Value("${file.upload-dir}")
	private String uploadDirFile;
	
	public String ulpoadNewUserDetails(MultipartFile file, String email, String name, String password,
			                           String confirmPassword, int age, String phone) {
		
		String imageUpload;
		
		File fileHolder =new File(uploadDirFile);
		if (!fileHolder.exists()) {
			fileHolder.mkdir();
		}
		if ( file==null || file.isEmpty()) {
			Path sourcePath= Paths.get("D:/aswin/Documents/default.jpg");
			
		    String  defaultFileName= System.currentTimeMillis() + "_default.jpa";
		    
		    Path targetPath=Paths.get(uploadDirFile, File.separator , defaultFileName);
		    
		    try {
				Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				imageUpload= "http://localhost:8080/FileUpload/"+ defaultFileName;
			} catch (IOException e) {
				imageUpload="http://localhost:8080/FileUpload/default.jpa";
			}			
		} else {
           
			String fileName=System.currentTimeMillis() + "_" + file.getOriginalFilename();
			
			Path filePath=Paths.get(uploadDirFile, File.separator , fileName);
			
			try {
				Files.write(filePath, file.getBytes());
				imageUpload="http:localhost:8080/FileUpload/" +fileName;
			} catch (IOException e) {
				e.printStackTrace();
				imageUpload="http://localhost:8080/FileUpload/default.jpa";
			}	
		}
		User user= userRepo.findByEmail(email);
		
		if (userRepo.existsByEmail(email) && user.isVerification()) {
			return "Account already exists by this email. Please Sign in";
		}
		
		if (!userRepo.existsByEmail(email)) {
			user=new User(imageUpload, email,name,password,confirmPassword, age, phone);
		}
		if (!password.equals(confirmPassword)) {
			return "Mismatch confirm password";
		}
		
		String otp=String.format("%06d", new Random().nextInt(900000)+100000);
		
		user.setUserProfile(imageUpload);
		user.setEmail(email);
		user.setName(name);
		user.setPassword(password);
		user.setConfirmPassword(confirmPassword);
		user.setAge(age);
		user.setPhone(phone);
		user.setOtp(otp);
		user.setOtpGeneratDateTime(LocalDateTime.now());
		
		try {	
			
		SimpleMailMessage message= new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Email OTP verification");
		message.setText("The OTP code is: "+ otp + " . This code is expired within 5 mins");
		mailSender.send(message);
		userRepo.save(user);
		return "OTP Send Successfully ";
	
		} catch (Exception e) {
			throw new RuntimeException("Error Occured: "+ e.getMessage());
		}
	}

	public String OTPVerification(String email, String otp) {
		User user=userRepo.findByEmail(email);
		if (userRepo.existsByEmail(email) && user.isVerification()) {
			return "Account already Exist.";
		}
		if (!userRepo.existsByEmail(email)) {
			return "Account is not Registered yet for OTP verification";
		}
		if (!user.getOtp().equals(otp)) {
			return "Invalid OTP. Please Enter valid OTP";
		}
		if (Duration.between(user.getOtpGeneratDateTime(), LocalDateTime.now()).toMinutes()>5) {
			return "OTP expired. Please get a new one";
		}
		
		user.setOtp(null);
		user.setVerification(true);
		userRepo.save(user);
		
		return "OTP Verified";
	}

	public String resetLink(String email) throws MessagingException {
		User user=userRepo.findByEmail(email);
		if (!userRepo.existsByEmail(email)) {
			return "Account not Register. Please register your Account";
		}
		String token =UUID.randomUUID().toString();
		
		String resetTokenLink="http://localhost:3000/reset-token-link/?token="+ token;
		user.setResetToken(resetTokenLink);
		user.setResetLinkDateTime(LocalDateTime.now());
		try {
			MimeMessage mime=mailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(mime, true);
			  
			helper.setTo(email);
			helper.setSubject("Password reset link Verification email");
			String htmlContent= "<p>Hello,</p>"
					+"<p>You requested to reset your password. Click the link below to set a new password:</p>"
					+"<p><a href=\""+resetTokenLink+"\">Reset My Password</a></p>"
					+"<br><p>This link will expire in 15 mintues.</p>";
			helper.setText(htmlContent, true);
			mailSender.send(mime);
			userRepo.save(user);
			return "Password reset Link sent to your email successfully";
		} catch (Exception e) {
			throw new RuntimeException("Error Occured: "+e.getMessage());
		}
		
	}

	public String confirmNewPassword(String token, String newPassword, String confirmPassword) {
		User user=userRepo.findByResetToken(token);
		if (userRepo.existsByResetToken(token) && !user.isVerification()) {
			return "Register the UserEmail and verify Properly.";
		}
		if (!userRepo.existsByResetToken(token)) {
			return "Invalid password reset link.";
		}
		
	   if (Duration.between(user.getResetLinkDateTime(), LocalDateTime.now()).toMinutes()>15) {
		return "Password reset link is expired. please get a new one";
	}
	   if (!newPassword.equals(confirmPassword)) {
		return "Invalid confirm password";
	}
	   user.setResetToken(null);
	   user.setResetLinkDateTime(null);
	   user.setPassword(newPassword);
	   user.setNewPassword(newPassword);
	   user.setConfirmPassword(confirmPassword);
	   userRepo.save(user);
		return "New password updated";
	}

	public String userPageLogin(LoginPage userLogin) {
		String emailUser=userLogin.getEmail();
		String passwordUser=userLogin.getPassword();
		User user=userRepo.findByEmail(emailUser);
		
		if (!userRepo.existsByEmail(emailUser)) {
			return "Account not found. Please register your Account";
		}
		if (userRepo.existsByEmail(emailUser) && !user.isVerification()) {
			return "Account is not verified yet. Please register and verify by OTP verification";
		}
		
		if (!user.getPassword().equals(passwordUser)) {
			return "Invalid password";
		}
		 
		return "LoggedIn successfully";
	}

	public User getUserDetails(String email) {
		User emailUser=userRepo.findByEmail(email);
		return emailUser; 
	}

	public String updateUser(MultipartFile file, String email, String name, int age, String phone) {
		String imageUrl;
		File dir=new File(uploadDirFile);
		
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (file==null || file.isEmpty()) {
			Path defaultSourcePath=Paths.get("D:/aswin/Documents/default.jpg");
			
			String defaultFileName=System.currentTimeMillis() +"_default.jpa";
			
			Path targetPath=Paths.get(uploadDirFile, File.separator, defaultFileName);
			
			try {
				Files.copy(defaultSourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				imageUrl="http://localhost:8080/FileUpload" + defaultFileName;
			} catch (IOException e) {
				imageUrl="http://localhost:8080/FileUpload/default.jpa";
			}     
			
		} else {
          String fileName=System.currentTimeMillis()+ "_" + file.getOriginalFilename();
          
          Path sourcePath=Paths.get(uploadDirFile, File.separator, fileName);
          
          try {
			 Files.write(sourcePath, file.getBytes());
			 imageUrl="http://localhost:8080/FileUpload/" +fileName;
		} catch (IOException e) {
			imageUrl="http://localhost:8080/FileUpload/default.jpa";
		}
		}
		 
		User user=userRepo.findByEmail(email);
		if (!userRepo.existsByEmail(email)) {
			return "Account Not Exists";
		}
		user.setUserProfile(imageUrl);
		user.setName(name);
		user.setAge(age);
		user.setPhone(phone);
		
		userRepo.save(user);
		return "User updated successfully";
	}
  
	public String deleteUserAccount(String email) {
		User user=userRepo.findByEmail(email);
		if (!userRepo.existsByEmail(email)) {
			return "Account not found";
		}
		 userRepo.delete(user);
		 return "Account deleted successfully";
	}

	public List<User> getAllUserDetails() {
		
		return userRepo.findAll();
	}

}
