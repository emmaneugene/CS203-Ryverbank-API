package com.csdg1t3.ryverbankapi.trade;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Allows us to store asset as persistent data through JPA.
 * Methods do not have to by explicitly declared, supports save(), findById() and delete()
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{
    Asset save(Asset asset);
    List<Asset> findAll();
    List<Asset> findByPortfolioCustomerId(Long id);
    void deleteById(Long id);
}