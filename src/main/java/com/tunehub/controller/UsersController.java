package com.tunehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.tunehub.entities.Song;
import com.tunehub.entities.Users;
import com.tunehub.services.SongService;
import com.tunehub.services.UsersService;

import jakarta.servlet.http.HttpSession;



@Controller
public class UsersController {
	
	@Autowired
	UsersService service;
	@Autowired
	SongService songService;
	@PostMapping("/register")
	public String addUsers(@ModelAttribute Users user) {
		boolean user_status = service.emailExists(user.getEmail());
		if(user_status == false) {
			service.addUser(user);
			System.out.println("User added");
		}
		else {
			System.out.println("User already exists");
		}
		return "home";
	}
	@PostMapping("/validate")
	public String validate(@ModelAttribute Users user,
			HttpSession session, Model model) {
		
		System.out.println("Call received");
		String email=user.getEmail();
		String password=user.getPassword();
		if(service.validateUser(email, password) == true) {
			String role = service.getRole(email);
			
			session.setAttribute("email", email);
			
			if(role.equals("Admin")) {
				return "adminHome";
			}
			else {
				Users users = service.getUser(email);
				boolean userStatus = users.isPremium();
				model.addAttribute("isPremium", userStatus);
				List<Song> songsList = songService.fetchAllSongs();
				model.addAttribute("songs", songsList);
				return "customerHome";
			}
		}
		else {
			return "login";
		}
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}
	
	
}
