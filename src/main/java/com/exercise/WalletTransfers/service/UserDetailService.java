package com.exercise.WalletTransfers.service;

import com.exercise.WalletTransfers.config.UsersDetails;
import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UsersDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("App Not Found with name: " + username));
        return UsersDetails.build(user);
    }
}
