package com.broker.arbitrage.Controller;

import com.broker.arbitrage.DTO.UserDTO;
import com.broker.arbitrage.Entity.User;
import com.broker.arbitrage.Service.UserService;
import com.broker.arbitrage.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {
    UserService userService;

    AuthenticationManager authenticationManager;

    JWTUtil jwtUtil;


@PostMapping("/create-admin")
public ResponseEntity<String> createAdmin(@Valid @RequestBody UserDTO userDTO) {
    Optional<User> userExists  = userService.isUserExist(userDTO.getEmail());
    if(userExists.isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }
    userService.addUser(userDTO);
    return new ResponseEntity<>("Admin User created", HttpStatus.OK);
}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userDTO) {
        Optional<User> userExists  = userService.isUserExist(userDTO.getEmail());
            if(userExists.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }

            userService.addUser(userDTO);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody  User user, HttpServletRequest request) {
        try {
            if (user.getEmail() == null || user.getPassword() == null || user.getPassword().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Username or password is empty"), HttpStatus.BAD_REQUEST);
            }
            System.out.println("email : " + user.getEmail());
            System.out.println("password : " + user.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            System.out.println("authentication : " + SecurityContextHolder.getContext());
            System.out.println("your session bro : " + SecurityContextHolder.getContext().getAuthentication());
          //  SecurityContextHolder.getContext().setAuthentication(authentication);
           // HttpSession session = request.getSession(true); //generate session
            //session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
          //  System.out.println("Session ID: " + session.getId());


            System.out.println("username : " + authentication.getName());

            boolean isValidUser = userService.validaeLogin(user.getEmail(), user.getPassword());
            System.out.println("isvaliduser : " + isValidUser);
            if (!isValidUser) {
                return new ResponseEntity<>(Map.of("message", "Wrong Credentials"), HttpStatus.UNAUTHORIZED);
            }
           String token = jwtUtil.generateToken(user.getEmail());
            return new ResponseEntity<>(Map.of("message", "success","token", token), HttpStatus.OK);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(Map.of("message", "Wrong Credentials"), HttpStatus.UNAUTHORIZED);
        }

    }

    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @DeleteMapping("/delete-users")
    public ResponseEntity<?> deleteUsers(String email) {
        try {
            userService.deleteUser(email);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }

}
