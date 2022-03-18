package teplykh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teplykh.entity.Expression;

@Repository
public interface ExpressionRepository extends JpaRepository<Expression, Integer> {
}
