package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/contact")
@SessionAttributes("page")
public class ContactController {

    @GetMapping("")
    public String contact(Model model) {
        model.addAttribute("page", "contact");
        return "contact";
    }
}
