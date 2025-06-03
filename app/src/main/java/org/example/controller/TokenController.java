package org.example.controller;

import io.jsonwebtoken.Jwt;
import org.example.entities.RefreshToken;
import org.example.request.AuthRequestDTO;
import org.example.request.JwtResponseDTO;
import org.example.response.RefreshTokenRequestDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){

        Authentication authentication = authenticationManager.authenticate (new UsernamePasswordAuthenticationToken (authRequestDTO.getUsername (),authRequestDTO.getPassword ()));
        if(authentication.isAuthenticated ()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken (authRequestDTO.getUsername ());
            return new ResponseEntity<> (JwtResponseDTO.builder ()
                    .accessToken (jwtService.GenerateToken (authRequestDTO.getUsername ()))
                    .token (refreshToken.getToken ())
                    .build (), HttpStatus.OK);
        }else {
            return new ResponseEntity <>("Exception in user Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken (refreshTokenRequestDTO.getToken())
                .map (refreshTokenService::verifyExpiration)
                .map (RefreshToken::getUserInfo)
                .map (userInfo -> {
                    String accessToken = jwtService.GenerateToken (userInfo.getUsername ());
                    return JwtResponseDTO.builder ()
                            .accessToken (accessToken)
                            .token (refreshTokenRequestDTO.getToken()).build ();
                })
                .map (ResponseEntity::ok)
                .orElseThrow (() ->new RuntimeException ("Refresh Token is not in DB..!!"));
    }
}
