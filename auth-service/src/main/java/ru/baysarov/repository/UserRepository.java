package ru.baysarov.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.baysarov.model.UserCredential;

public interface UserRepository extends JpaRepository<UserCredential, Integer> {

  Optional<UserCredential> findByEmail(String email);
}
