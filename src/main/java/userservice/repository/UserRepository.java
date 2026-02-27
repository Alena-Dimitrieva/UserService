package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import userservice.entity.AppUser;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findByAge(Integer age);
}
