package com.traceback.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByNormalizedEmail(String normalizedEmail);
    boolean existsByNormalizedEmail(String normalizedEmail);
}
