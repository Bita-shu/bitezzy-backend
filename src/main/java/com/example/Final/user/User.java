package com.example.Final.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_DETAILS")
@Data
@NoArgsConstructor
public class User {
	
	

	public User(String userProfile, String email, String name, String password,
			    String confirmPassword , int age, String phone) {
		
		this.userProfile = userProfile;
		this.email = email;
		this.name = name;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.age = age;
		this.phone = phone;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SNO")
	private int sno;
	
	@Column(name = "USER_PROFILE_PICTURE")
	private String userProfile;
	
	@Column(name="USER_NAME")
	private String name;
	
	@Column(name = "USER_EMAIL")
	private String email;
	
	@Column(name = "USER_AGE")
	private int age;
	
	@Column(name = "USER_PHONE_NUMBER")
	private String phone;
	
	@Column(name ="USER_PASSWORD" )
	private String password;
	
	@Column(name = "User_NEW_PASSWORD")
	private String newPassword;
	
	@Column(name = "USER_CONFIRM_PASSWORD")
	private String confirmPassword;
	
	@Column(name = "OTP")
	private String otp;
	
	@Column(name = "OTP_GENERATED_DATE_TIME")
	private LocalDateTime otpGeneratDateTime;
	
	@Column(name = "USER_VERIFICATION")
	private boolean verification =false;
	
	@Column(name ="USER_TOKEN" )
	private String token;
	
	@Column(name = "RESRT_TOKEN")
	private String resetToken;
	
	@Column(name ="RESETLINK_VERIFICATION_DATE_TIME" )
	private LocalDateTime resetLinkDateTime;

}
