package com.example.loanManage.config;


import com.example.loanManage.entity.User;
import com.example.loanManage.entity.UserRole;
import com.example.loanManage.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component //Tells Spring to automatically find and manage this class as a 'Bean'
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

// This constructor injects (connects) the actual database tool into this class
    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    // This method contains the logic that executes at startup
    public void run(String... args) {
        // Checks if the database is empty (so we don't create duplicate admins)
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@lms.com");
            admin.setPhone("01700000000");
            admin.setPassword("admin123"); // later: encode
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
// Sends the completed admin object to the database to be saved permanently
            userRepository.save(admin);
            System.out.println("Default admin created: admin@lms.com / admin123");
        }
    }
}
