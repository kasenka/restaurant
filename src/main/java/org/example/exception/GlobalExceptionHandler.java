package org.example.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRegisterParam.class)
    public String badRegisterParam(BadRegisterParam ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }
}
