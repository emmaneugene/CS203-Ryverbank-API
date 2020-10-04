package com.csdg1t3.ryverbankapi.transfer;

import java.util.List;

import com.csdg1t3.ryverbankapi.account.Account;
import com.csdg1t3.ryverbankapi.account.AccountService;
import com.csdg1t3.ryverbankapi.account.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
public class TransferController {
    private TransferService transferService;
    private AccountService accountService;

    public TransferController (TransferService ts, 
                                AccountService as) {
        this.transferService = ts;
        this.accountService = as;
    }

    /**
     * List all transfer in the system
     * @return list of all transfer
     */
    @GetMapping("/accounts/{account_id}/transactions")
    public List<Transfer> getTransfers(@PathVariable (value = "account_id") Long accountId) {
        Account account = accountService.getAccount(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return transferService.listTransfers(accountId);
    }

    /**
     * Search for transfer with the given id
     * If there isn't one with the given id, throw TransferNotFoundException
     * @param id
     * @return transfer with the given id
     */
    @PreAuthorize("#id == authentication.principal.id")
    @GetMapping("/accounts/{account_id}/transactions/{transfer_id}")
    public Transfer getTransfer(@PathVariable (value = "account_id") Long accountId, 
                                @PathVariable (value = "transfer_id") Long transferId) {
        Transfer transfer = transferService.getTransfer(transferId, accountId);
        
        // Handle "transfer not found" error using appropriate http codes
        if (transfer == null) throw new TransferNotFoundException(transferId);
        return transfer;
    }

    /**
     * Add new transfer with POST request to "/transfer"
     * Note the use of @RequestBody
     * @param transfer
     * @return list of all transfer
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/transactions")
    public Transfer addTransfer(@RequestBody Transfer transfer) {
        Account senderAccount = accountService.getAccount(transfer.getFrom());
        if (senderAccount == null) {
            throw new AccountNotFoundException(transfer.getFrom());
        }
        transfer.setSender(senderAccount);

        Account receiverAccount = accountService.getAccount(transfer.getTo());
        if (receiverAccount == null) {
            throw new AccountNotFoundException(transfer.getTo());
        }
        transfer.setReceiver(receiverAccount);

        return transferService.addTransfer(transfer);
    }
}