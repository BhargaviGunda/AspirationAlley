package AspirationAlley.service;

import AspirationAlley.model.User;
import AspirationAlley.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String roles = "ROLE_USER,ROLE_ADMIN";  // Example roles as a String
        Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))  // Convert to stream
            .map(role -> new SimpleGrantedAuthority(role))  // Map each role to a GrantedAuthority
            .collect(Collectors.toList());  // Collect to a list

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
