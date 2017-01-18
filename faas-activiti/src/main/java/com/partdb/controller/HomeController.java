package com.partdb.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.partdb.entity.cloud.Person;
import com.partdb.service.PersonService;

@Controller
public class HomeController {
	@Autowired
	PersonService personService;
/*	
	@RequestMapping("/hello")
    public String helloWorld(Model model) {			
		model.addAttribute("name", "hello world!!!");
		return "hello";
    }
	
	@RequestMapping(value="/login")
    public String login() {			
		return "login";
    }
	
	
	@RequestMapping(value="/home")
    public String home(Model model) {			
		return "home";
    }*/
	/*
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(Person person, ModelAndView model, RedirectAttributes redirectAttributes) {
		person = personService.login(person);
		if(person != null){			
			redirectAttributes.addFlashAttribute("person", person);
			return "redirect:home";
		} else {
			model.setViewName("error");
			return "error";
		}
	}*/
	
	@RequestMapping(value="/signin")
    public String signin() {			
		return "signin";
    }
	
	@RequestMapping(value="/signin", method = RequestMethod.POST)
    public String signin(Person person) {	
		personService.signIn(person);
		return "redirect:home";
    }
	
	@RequestMapping(value="/home")
    public ModelAndView home(Authentication auth) {
		ModelAndView model = new ModelAndView();
		model.addObject("name", auth.getName());
		
		model.setViewName("home");
		return model;
    }
	
	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage(Authentication auth) {
		ModelAndView model = new ModelAndView();
			
		if(auth.isAuthenticated()) {
			model.addObject("name", auth.getName());
			model.setViewName("hello");
		} else {
			model.setViewName("hello");
		}
		
		return model;

	}
	
	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage(Authentication auth) {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security + Hibernate Example");
		model.addObject("message", "This page is for ROLE_ADMIN only!");
		model.addObject("name", auth.getName());
		model.setViewName("admin");

		return model;

	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout, HttpServletRequest request, final RedirectAttributes redirectAttributes) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		
		model.setViewName("login");

		return model;

	}
	
	// customize the error message
	private String getErrorMessage(HttpServletRequest request, String key) {

		Exception exception = (Exception) request.getSession().getAttribute(key);

		String error = "";
		if (exception instanceof BadCredentialsException) {
			error = "Invalid username and password!";
		} else if (exception instanceof LockedException) {
			error = exception.getMessage();
		} else {
			error = "Invalid username and password!";
		}

		return error;
	}
	
	// for 403 access denied page
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied() {

		ModelAndView model = new ModelAndView();

		// check if user is login
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			System.out.println(userDetail);

			model.addObject("username", userDetail.getUsername());

		}

		model.setViewName("403");
		return model;

	}

	
}
 