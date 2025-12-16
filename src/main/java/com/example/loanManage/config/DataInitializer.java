package com.example.loanManage.config;


import com.example.loanManage.entity.User;
import com.example.loanManage.entity.UserRole;
import com.example.loanManage.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@lms.com");
            admin.setPhone("01700000000");
            admin.setPassword("admin123"); // later: encode
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);
            System.out.println("Default admin created: admin@lms.com / admin123");
        }
    }
}
