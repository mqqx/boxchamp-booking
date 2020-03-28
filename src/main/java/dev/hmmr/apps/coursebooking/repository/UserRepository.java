package dev.hmmr.apps.coursebooking.repository;

import dev.hmmr.apps.coursebooking.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
