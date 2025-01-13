package com.cafe.management.JWT;

import com.cafe.management.DAO.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    @Lazy
    private UserDao userDao;

    private com.cafe.management.POJO.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from DB using the provided email (username)
        userDetail = userDao.findByEmailId(username);

        if (userDetail == null) {
            log.error("User with username '{}' not found.", username);
            throw new UsernameNotFoundException("User Not Found.");
        }

        // You can also fetch authorities/roles if needed in the future
        log.info("User '{}' loaded successfully.", username);
        return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
    }

    public com.cafe.management.POJO.User getUserDetail() {
        return userDetail;
    }
}
