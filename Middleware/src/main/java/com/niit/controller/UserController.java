package com.niit.controller;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.dao.UserDao;
import com.niit.model.*;

@Controller
public class UserController {
		
		@Autowired
		private UserDao userDao;
		@RequestMapping(value = "/registeruser", method = RequestMethod.POST)
		public ResponseEntity<?> registerUser(@RequestBody User user) 
		{
			if(!userDao.isEmailvalid(user.getEmail()))
			{
				Errorclass error = new Errorclass(1,user.getEmail()+"..username already exists,, please enter different username");
				return new ResponseEntity<Errorclass>(error, HttpStatus.NOT_ACCEPTABLE);
			}
			boolean result = userDao.registerUser(user);
			
			if (result) {
				return new ResponseEntity<User>(user, HttpStatus.OK);
			} else {
				Errorclass error = new Errorclass(2,"unable to register user details");
				return new ResponseEntity<Errorclass>(error, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		@RequestMapping(value="/login",method=RequestMethod.POST)
		public ResponseEntity<?> login(@RequestBody User user ,HttpSession sess)
		{
			System.out.println(user);
			User validuser=userDao.login(user);
			sess.setAttribute("validuser", validuser);
			System.out.println(validuser);
			if(validuser==null)
			{
				Errorclass error=new Errorclass(5, "Login Failed...!make sure to insert correct Email-Id and Password");
				return new ResponseEntity<Errorclass>(error,HttpStatus.UNAUTHORIZED);
			}
			else	{	
				validuser.setOnline(true);
				userDao.update(validuser);
				sess.setAttribute("validuser",validuser);
			return new ResponseEntity<User>(validuser,HttpStatus.OK);
			}		
		}
		@RequestMapping(value="/logout",method=RequestMethod.PUT)
		public ResponseEntity<?> logout(HttpSession sess)
		{
			User user1=(User) sess.getAttribute("validuser");
			if(user1==null)
			{
				Errorclass error=new Errorclass(4, "Please Login.....!");
				return new ResponseEntity<Errorclass>(error,HttpStatus.UNAUTHORIZED);
			}
			else{
			User user=userDao.getUser(user1.getId());
			user.setOnline(false);
			userDao.update(user);
			sess.removeAttribute("validuser");
			sess.invalidate();
			return new ResponseEntity<User>(user,HttpStatus.OK);}		
	   }
		@RequestMapping(value="/getuser",method=RequestMethod.GET)
		public ResponseEntity<?> getuser(HttpSession sess){
			User user2=(User)sess.getAttribute("validuser");
			//Integer usid=new Integer(user2.getId());
			if(user2==null){
				Errorclass error=new Errorclass(6, "Unauthorized Access.....!");
						return new ResponseEntity<Errorclass>(error,HttpStatus.UNAUTHORIZED);
			}
			User user=userDao.getUser(user2.getId());
			return new ResponseEntity<User>(user,HttpStatus.OK);	
		}
		@RequestMapping(value="/updateuser",method=RequestMethod.PUT)
		public ResponseEntity<?> updateuser(@RequestBody User user,HttpSession sess){
			User user2=(User)sess.getAttribute("validuser");
			//int usid=user2.getId();
			if(user2==null){
				Errorclass error=new Errorclass(7, "Unauthorized Access.....!");
						return new ResponseEntity<Errorclass>(error,HttpStatus.UNAUTHORIZED);
			}
			try {
				userDao.updateuser(user);
				return new ResponseEntity<User>(user,HttpStatus.OK);
			} catch (Exception e)
			{
				Errorclass error=new Errorclass(8, "Unable to update user details......!" +e.getMessage());
	             return new ResponseEntity<Errorclass>(error,HttpStatus.INTERNAL_SERVER_ERROR);
			}	
		}
	}