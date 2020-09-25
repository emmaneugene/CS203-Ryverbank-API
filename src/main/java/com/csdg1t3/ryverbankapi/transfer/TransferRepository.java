package com.csdg1t3.ryverbankapi.transfer;

import java.util.List;
import java.util.Optional;

/**
 * Data access object
 */
public interface TransferRepository {
    Long save(Transfer transfer);
    List<Transfer> findAll();

    Optional<Transfer> findByID(Long id);
}