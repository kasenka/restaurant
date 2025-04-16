package org.example.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.exception.BadRegisterParam;
import org.example.model.Subordination;
import org.example.model.Worker;
import org.example.repository.SubordinationRepository;
import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WelcomeController {

    private final WorkerRepository workerRepository;
    private final SubordinationRepository subordinationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WelcomeController(WorkerRepository workerRepository,
                             SubordinationRepository subordinationRepository,
                             PasswordEncoder passwordEncoder) {
        this.workerRepository = workerRepository;
        this.subordinationRepository = subordinationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String welcome(Model model){
        model.addAttribute("page", "home");
        return "welcome";
    }

    @GetMapping("/register")
    public String showRegister(){
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @RequestParam String username,
                           @RequestParam String password ,
                           @RequestParam String confirmPassword,
                           @RequestParam String key) {

        if (workerRepository.findByUsername(username).isPresent()){
            throw new BadRegisterParam("Юзернейм уже занят");
        }if (password.length() < 3  || password.length() > 10){
            throw new BadRegisterParam("Пароль должен быть от 3 до 10 символов");
        }if (!password.equals(confirmPassword)){
            throw new BadRegisterParam("Пароли не совпадают");
        }if(!key.equals("worker-key")){
            throw new BadRegisterParam("Неверный ключ доступа");
        }

        Worker worker = new Worker();
        worker.setUsername(username);
        worker.setEncryptedPassword(passwordEncoder.encode(password));
        worker.setRole("MANAGER");
        workerRepository.save(worker);

        Subordination subordination = new Subordination();
        subordination.setSubordinateId(worker);
        subordination.setSupervisorId(workerRepository.findByUsername("admin2").get());
        subordinationRepository.save(subordination);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLogin(){
        return "login";
    }
}
