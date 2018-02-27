package com.niit.controller;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.dao.JobDao;
import com.niit.dao.UserDao;
import com.niit.model.Errorclass;
import com.niit.model.Job;
import com.niit.model.User;

@Controller
public class JobController {
@Autowired
private UserDao userDao;
@Autowired
private JobDao jobDao;
@RequestMapping(value="/addjob",method=RequestMethod.POST)
public ResponseEntity<?> addJob(@RequestBody Job  job,HttpSession sess){
	
	User user2=(User)sess.getAttribute("validuser");
	if(user2==null){
	Errorclass error=new Errorclass(4,"unauthorized access");
	return new ResponseEntity<Errorclass>(error,HttpStatus.UNAUTHORIZED);
	}
	User user=userDao.getUser(user2.getId());

if(!user.getRole().equals("ADMIN")){
	Errorclass error=new Errorclass(5,"Invalid email and password");
	return new ResponseEntity<Errorclass>(error,HttpStatus.UNAUTHORIZED);
	}

try{
	jobDao.addJob(job);
	return new ResponseEntity<Job>(job,HttpStatus.OK);
}catch(Exception e){
	Errorclass error=new Errorclass(6,"unable to post job details"+e.getMessage());
	return new ResponseEntity<Errorclass>(error,HttpStatus.INTERNAL_SERVER_ERROR);

}
}

}