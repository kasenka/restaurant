package org.example.config;

import org.example.model.Worker;
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

    @ModelAttribute("authorized")
    public boolean isAuthorized(Principal principal) {
        return principal != null ;
    }

    @ModelAttribute("unauthorized")
    public boolean isUnauthorized(Principal principal) {
        return principal == null ;
    }

    @ModelAttribute("name")
    public String workerName(Principal principal) {
        if (principal != null) {
            return principal.getName();
        }return "";
    }

    @ModelAttribute("photo")
    public String photo(Principal principal) {
        if (principal != null) {
            Worker w = workerRepository.findByUsername(principal.getName()).get();
            return (w.getPhoto() == null)? "/images/profile.jpg" : w.getPhoto();
        }return "";
    }
}
