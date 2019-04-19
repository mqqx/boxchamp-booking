package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
