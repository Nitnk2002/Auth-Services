package org.nit.repository;

import org.nit.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserInfo(org.nit.entities.UserInfo userInfo);

}
