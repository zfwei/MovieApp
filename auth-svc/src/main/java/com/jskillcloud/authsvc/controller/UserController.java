package com.jskillcloud.authsvc.controller;

import com.jskillcloud.authsvc.dto.UserIdentityAvailability;
import com.jskillcloud.authsvc.dto.UserInfo;
import com.jskillcloud.authsvc.mapper.UserMapper;
import com.jskillcloud.authsvc.security.CurrentUser;
import com.jskillcloud.authsvc.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/me")
    public UserInfo getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        List<String> roles = currentUser.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        UserInfo userInfo = UserInfo.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .roles(roles)
                .build();
        return userInfo;
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userMapper.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/testuser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return ">>> User Contents!";
    }

    @GetMapping("/testadmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return ">>> Admin Contents";
    }

}
