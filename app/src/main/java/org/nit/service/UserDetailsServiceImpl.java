package org.nit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nit.entities.UserInfo;
import org.nit.eventproducer.UserInfoEvent;
import org.nit.eventproducer.UserInfoProducer;
import org.nit.model.UserInfoDto;
import org.nit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user = userRepository.findByUsername (username);
        if(user == null){
            throw new UsernameNotFoundException ("could not found user... !!");
        }
        return new CustomUserDetails (user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername (userInfoDto.getUsername());
    }

    @org.springframework.transaction.annotation.Transactional
    public Boolean signupUser(UserInfoDto userInfoDto){
        //Define a function to check if userEmail, password is correct
        userInfoDto.setPassword(passwordEncoder.encode (userInfoDto.getPassword()));
        if(Objects.nonNull (checkIfUserAlreadyExist (userInfoDto))){
            return false;
        }
        String userId = UUID.randomUUID ().toString ();
        UserInfo newUser = new UserInfo();
        newUser.setUserId(userId);
        newUser.setUsername(userInfoDto.getUsername());
        newUser.setPassword(userInfoDto.getPassword());
        newUser.setFirstName(userInfoDto.getFirstName());
        newUser.setLastName(userInfoDto.getLastName());
        newUser.setEmail(userInfoDto.getEmail());
        newUser.setPhoneNumber(userInfoDto.getPhoneNumber());
        newUser.setRoles(new HashSet<>());
        userRepository.save(newUser);
        userInfoProducer.sendEventToKafka (userinfoEventPublish (userInfoDto,userId));
        return true;
    }
    private UserInfoEvent userinfoEventPublish(UserInfoDto userInfoDto, String userId){
        return UserInfoEvent.builder ().userId (userId)
                .firstName (userInfoDto.getFirstName ())
                .lastName (userInfoDto.getLastName ())
                .email (userInfoDto.getEmail ())
                .phoneNumber (userInfoDto.getPhoneNumber ())
                .build ();
    }

}
