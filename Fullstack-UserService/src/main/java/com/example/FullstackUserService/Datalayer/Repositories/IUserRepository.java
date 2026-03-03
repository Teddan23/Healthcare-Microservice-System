package com.example.FullstackUserService.Datalayer.Repositories;

import com.example.FullstackUserService.Model.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository  extends JpaRepository<User, Long> {
    User findByPersonnummer(String personnummer);
}
