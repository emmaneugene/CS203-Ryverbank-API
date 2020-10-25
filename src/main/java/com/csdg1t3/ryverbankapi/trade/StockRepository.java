package com.csdg1t3.ryverbankapi.trade;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access object
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, String>{
    Stock save(Stock stock);
    void deleteBySymbol(String symbol);
    List<Stock> findAll();
    Optional<Stock> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);
}
