package com.csdg1t3.ryverbankapi.trade;
import java.util.List;
import java.util.Optional;

/**
 * Data access object
 */
public interface TradeRepository {
    Long save(Trade trade);
    int update(Trade trade);
    int deleteById(Long id);
    List<Trade> findAll();

    // Using Optional - the return value of this method may contain a null value
    Optional<Trade> findById(Long id);
 
}

