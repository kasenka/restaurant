package org.example.config;

import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SecurityUtils {

    private static WorkerRepository workerRepository;

    @Autowired
    public SecurityUtils(WorkerRepository workerRepository) {
        SecurityUtils.workerRepository = workerRepository;
    }

    public static boolean hasRole(Principal principal, String role) {
//        return workerRepository.findByUsername(principal.getName()).get().getRole().equals(role);
        if (principal == null) return false;

        return workerRepository.findByUsername(principal.getName())
                .map(worker -> role.equals(worker.getRole()))
                .orElse(false);
    }
}
