package teplykh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teplykh.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByLogin(String login);
}
