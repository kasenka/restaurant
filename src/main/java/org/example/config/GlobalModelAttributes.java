package org.example.config;

import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    WorkerRepository workerRepository;

    @ModelAttribute("admin")
    public boolean isAdmin(Principal principal) {
        return principal != null && SecurityUtils.hasRole(principal,"ADMIN");
    }
}
