package com.csdg1t3.ryverbankapi.transfer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {
    private TransferService transferService;

    public TransferController (TransferService ts) {
        this.transferService = ts;
    }

    /**
     * List all transfer in the system
     * @return list of all transfer
     */
    @GetMapping("/transfers")
    public List<Transfer> getTransfers() {
        return transferService.listTransfers();
    }

    /**
     * Search for transfer with the given id
     * If there isn't one with the given id, throw TransferNotFoundException
     * @param id
     * @return transfer with the given id
     */
    @GetMapping("/transfers/{id}")
    public Transfer getTransfer(@PathVariable Long id) {
        Transfer transfer = transferService.getTransfer(id);
        
        // Handle "transfer not found" error using appropriate http codes
        if (transfer == null) throw new TransferNotFoundException(id);
        return transferService.getTransfer(id);
    }

    /**
     * Add new transfer with POST request to "/transfer"
     * Note the use of @RequestBody
     * @param transfer
     * @return list of all transfer
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/transfers")
    public Transfer addTransfer(@RequestBody Transfer transfer) {
        return transferService.addTransfer(transfer);
    }
}