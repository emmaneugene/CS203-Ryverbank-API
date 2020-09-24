package com.csdg1t3.ryverbankapi.transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {
    private TransferRepository tranfers;
    // private AccountRepository accounts;

    public TransferController(TransferRepository transfers /*, AccountRepository accounts*/) {
        this.transfers = transfers;
        // this.accounts = accounts;
    }

    @PostMapping("/accountURL/{account_id}/transactions")
    public Transfer addTransfer(@Valid @PathVariable (value = "account_id") Long accountId, @Valid @RequestBody Transfer trasnfer) {
        
    }
}