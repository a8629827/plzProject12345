package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    //select * from tbl_user where username="abcd";
}
