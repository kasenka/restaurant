package org.example.controller;

import org.example.config.CustomUserDetailsService;
import org.example.exception.BadUpdateParam;
import org.example.model.Worker;
import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @GetMapping("")
    public String profile(Model model, Principal principal) {
        if (principal != null) {
            Worker worker = workerRepository.findByUsername(principal.getName()).get();
            System.out.println(worker.getRole());
            model.addAttribute("worker", worker);
            return "profile";
        }return "welcome";
    }

    @PostMapping("")
    public String update(Model model, Principal principal,
                         @RequestParam String username,
                         @RequestParam(required = false) String fullName,
                         @RequestParam(required = false) LocalDate birthDate,
                         @RequestParam(required = false) String photo) {

        String regex = "^[А-Я]{1}[а-я]+\\s[А-Я]{1}[а-я]+\\s[А-Я]{1}[а-я]+";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);

        if (principal != null) {
            if (!principal.getName().equals(username) &&
                    workerRepository.findByUsername(username).isPresent()){
                throw new BadUpdateParam("Юзернейм уже занят");
            }if (fullName != null && !fullName.trim().isEmpty() && !pattern.matcher(fullName).matches()){
                throw new BadUpdateParam("Неверный формат фио");
            }if (birthDate != null &&
                    (LocalDate.now().getYear() - birthDate.getYear() >= 70
                    || LocalDate.now().getYear() - birthDate.getYear() <= 18)){
                throw new BadUpdateParam("Невалидная дата рождения");
            }
            Worker worker = workerRepository.findByUsername(principal.getName()).get();

            worker.setUsername(username);
            worker.setFullName((fullName.trim().isEmpty()) ? null : fullName);
            worker.setBirthDate(birthDate);
            worker.setPhoto((photo.trim().isEmpty()) ? null : photo);

            workerRepository.save(worker);

            userDetailsService.reAuthenticateUser(worker.getUsername());

            return "redirect:/profile";
        }return "welcome";
    }

}
