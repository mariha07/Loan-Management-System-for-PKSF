package com.example.loanManage.service;


import com.example.loanManage.dto.CreateOfficerRequest;
import com.example.loanManage.dto.LoginRequest;
import com.example.loanManage.dto.LoginResponse;
import com.example.loanManage.entity.User;
import com.example.loanManage.entity.UserRole;
import com.example.loanManage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    // if you use password encoder: private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1) Create officer (Admin only)
    @Transactional
    public User createOfficer(CreateOfficerRequest request) {
        // check role
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Use LOAN_OFFICER or COLLECTION_OFFICER");
        }

        if (role != UserRole.LOAN_OFFICER && role != UserRole.COLLECTION_OFFICER) {
            throw new RuntimeException("Role must be LOAN_OFFICER or COLLECTION_OFFICER");
        }

        // check unique email/phone
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        // user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(request.getPassword()); // simple version, not secure
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    // 2) Login
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        var optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Invalid email or password");
            return response;
        }

        User user = optionalUser.get();

        // if using encoder: if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
        if (!user.getPassword().equals(request.getPassword())) {
            response.setSuccess(false);
            response.setMessage("Invalid email or password");
            return response;
        }

        if (!user.isActive()) {
            response.setSuccess(false);
            response.setMessage("User is not active");
            return response;
        }

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setSuccess(true);
        response.setMessage("Login successful");

        return response;
    }
}
