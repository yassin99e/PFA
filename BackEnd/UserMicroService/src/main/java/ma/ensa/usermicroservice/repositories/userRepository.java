package ma.ensa.usermicroservice.repositories;


import ma.ensa.usermicroservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);

}
