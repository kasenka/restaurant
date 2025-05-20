package org.example.config;

import org.example.model.Worker;
import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Worker worker = workerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("Работник не найден");
                });

        return org.springframework.security.core.userdetails.User.builder()
                .username(worker.getUsername())
                .password(worker.getEncryptedPassword())
                .roles(worker.getRole())
                .build();
    }

    public void reAuthenticateUser(String newUsername) {
        UserDetails userDetails = loadUserByUsername(newUsername);

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

}
