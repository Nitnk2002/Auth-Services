package org.nit.repository;

import org.nit.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserInfo, Long> {
    public UserInfo findByUsername(String username);

}
