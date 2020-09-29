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
    public List<Transfer> listTransfers(Long accountId) {
        List<Transfer> allTransfersForAccountId = transfers.findByReceiverId(accountId);
        allTransfersForAccountId.addAll(transfers.findBySenderId(accountId));
        return allTransfersForAccountId;
    }

    @Override
    public Transfer getTransfer(Long transferId, Long accountId) {
        Optional<Transfer> result = transfers.findByIdAndSenderId(transferId, accountId);
        if (result.isPresent()) {
            return result.get();
        }

        result = transfers.findByIdAndReceiverId(transferId, accountId);
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