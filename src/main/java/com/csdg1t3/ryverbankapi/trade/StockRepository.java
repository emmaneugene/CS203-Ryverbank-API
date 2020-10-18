package com.csdg1t3.ryverbankapi.trade;

import java.util.List;
import java.util.Optional;

/**
 * Data access object
 */
@Repository
public interface StockRepository extends JPARepository<Stock, Long>{
    Long save(Stock stock);
    int update(Stock stock);
    int deleteById(Long id);
    List<Stock> findAll();

    Optional<Stock> findByID(Long id);
}
