package com.csdg1t3.ryverbankapi.transfer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Concrete class that implements TransferService
 */
@Service
public class TransferServiceImpl implements TransferService{
    private TransferRepository transfers;
    
    public TransferServiceImpl(TransferRepository transfers) {
        this.transfers = transfers;
    }

    @Override
    public List<Transfer> listTransfers() {
        return transfers.findAll();
    }

    @Override
    public Transfer getTransfer(Long id) {
        Optional<Transfer> result = transfers.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    @Override
    public Transfer addTransfer(Transfer transfer) {
        return transfers.save(transfer);
    }
}