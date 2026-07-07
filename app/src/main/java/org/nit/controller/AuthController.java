package org.nit.controller;

import lombok.AllArgsConstructor;
import org.nit.entities.RefreshToken;
import org.nit.model.UserInfoDto;
import org.nit.request.JwtResponseDTO;
import org.nit.service.JwtService;
import org.nit.service.RefreshTokenService;
import org.nit.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/auth/v1/signup")
    public ResponseEntity signup (@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean isSignUped = userDetailsService.signupUser (userInfoDto);
            if (Boolean.FALSE.equals (isSignUped)) {
                return new ResponseEntity <>("Already Exist", HttpStatus.BAD_REQUEST) ;
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken (userInfoDto.getUsername ());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername ());
            return new ResponseEntity <>(JwtResponseDTO.builder ().accessToken (jwtToken)
                    .token(refreshToken.getToken ()).build (),HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity <>("Exception in User service",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/ping")
    public ResponseEntity ping(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            return new ResponseEntity <>("Pong",HttpStatus.OK);
        }else {
            return new ResponseEntity<> ("Unauthenticated", HttpStatus.UNAUTHORIZED);
        }
    }
}
