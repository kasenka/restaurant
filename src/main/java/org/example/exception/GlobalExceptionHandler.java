package org.example.exception;

import org.example.config.GlobalModelAttributes;
import org.example.model.Worker;
import org.example.repository.WorkerRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.Principal;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final WorkerRepository workerRepository;

    public GlobalExceptionHandler(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @ExceptionHandler(BadRegisterParam.class)
    public String badRegisterParam(BadRegisterParam ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }

    @ExceptionHandler(BadUpdateParam.class)
    public String badUpdateParam(BadUpdateParam ex, Principal principal, Model model) {
        model.addAttribute("error", ex.getMessage());
        Worker worker = workerRepository.findByUsername(principal.getName()).get();
        model.addAttribute("worker", worker);
        model.addAttribute("photo", (worker.getPhoto() == null)? "/images/profile.jpg" : worker.getPhoto());
        model.addAttribute("authorized", true);
        model.addAttribute("name", principal.getName());
        return "profile";
    }
}
