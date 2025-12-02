package com.broker.arbitrage.Service;

import com.broker.arbitrage.DTO.UserDTO;
import com.broker.arbitrage.Entity.Type.Roles;
import com.broker.arbitrage.Entity.User;
import com.broker.arbitrage.Repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    private final Map<String, UserDetails> cache = new ConcurrentHashMap<>();


    public void addUser(UserDTO user) {
        try {
            User userDTO = new User();
            userDTO.setEmail(user.getEmail());
            userDTO.setPassword(passwordEncoder.encode(user.getPassword()));
            userDTO.setName(user.getName());
            System.out.println("Adding user: " + userDTO.toString());
            if(user.getName().equals("admin")) {
                userDTO.setRoles(Set.of(Roles.Admin, Roles.User));
            } else{
            userDTO.setRoles(Set.of(Roles.User));
            }
            userRepo.save(userDTO);
            System.out.println("User saved successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Optional<User> isUserExist(String email) {
        return userRepo.findByEmail(email);
    }

    public boolean validaeLogin(String email, String password) {
        Optional<User> userExists = isUserExist(email);
        System.out.println("userExists : " + userExists);
        if(userExists.isPresent()) {
            return passwordEncoder.matches(password, userExists.get().getPassword());
        }
        return false;
    }


    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return cache.computeIfAbsent(username, e -> {
            User user = userRepo.findByEmail(e)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList()
            );
        });
    }

    @Transactional
    public void deleteUser(String email) {
        userRepo.deleteByEmail(email);
    }
}
