package com.movielist.controller;

import com.movielist.dto.UserResponse;
import com.movielist.entity.User;
import com.movielist.security.CustomUserDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CustomUserDetailsService userDetailsService;

    public UserController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return UserResponse.from(user);
    }
}
