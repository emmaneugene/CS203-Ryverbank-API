package com.csdg1t3.ryverbankapi.transfer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.csdg1t3.ryverbankapi.account.Account;
import com.csdg1t3.ryverbankapi.account.AccountRepository;

/**
 * Concrete class that implements TransferService
 */
@Service
public class TransferServiceImpl implements TransferService{
    private TransferRepository transfers;
    private AccountRepository accounts;
    
    public TransferServiceImpl(TransferRepository transfers, AccountRepository accounts) {
        this.transfers = transfers;
        this.accounts = accounts;
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
        Account sender = transfer.getSender();
        Account receiver = transfer.getReceiver();
        double amount = transfer.getAmount();

        if (sender.getAvailableBalance() < amount) {
            throw new TransferNotValidException("Could not process due to insufficient funds");
        }

        sender.setAvailableBalance(sender.getAvailableBalance() - amount);
        sender.setBalance(sender.getBalance() - amount);
        receiver.setAvailableBalance(receiver.getAvailableBalance() + amount);
        receiver.setBalance(receiver.getBalance() + amount);
        accounts.save(sender);
        accounts.save(receiver);
        
        return transfers.save(transfer);
    }
}