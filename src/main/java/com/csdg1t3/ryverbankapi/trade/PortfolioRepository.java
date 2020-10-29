package com.csdg1t3.ryverbankapi.trade;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class that allows us to store asset as persistent data through JPA.
 * Methods do not have to by explicitly declared, supports save(), findById() and delete()
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long>{
    Portfolio save(Portfolio portfolio);
    Optional<Portfolio> findByCustomerId(Long id);
}