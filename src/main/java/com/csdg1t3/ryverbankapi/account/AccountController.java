package com.csdg1t3.ryverbankapi.account;


import java.util.Optional;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

import com.csdg1t3.ryverbankapi.user.*;

/**
 * Controller that manages HTTP GET/POST requests 
 */
@RestController
public class AccountController {
    private AccountRepository accountRepo;
    private UserRepository userRepo;
    private TransferRepository transferRepo;

    public AccountController(AccountRepository accountRepo, UserRepository userRepo,
                             TransferRepository transferRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
        this.transferRepo = transferRepo;
    }
    
    /**
     * Only ROLE_USER can view accounts, as validated in security config
     * Thus method returns a list of accounts associated with user's ID 
     * @return 
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        User user = userRepo.findByUsername(uDetails.getUsername()).get();
                
        return accountRepo.findByCustomerId(user.getId());
    }

    /**
     * Only ROLE_USER can view accounts, as validated in security config
     * This method calls getAccount() and further validates by customer's ID
     * @param id
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) {
        List<Account> userAccounts = getAccounts();
        
        for (Account acc : userAccounts) {
            if (acc.getId() == id)
                return acc;
        }

        throw new AccountNotFoundException(id);
    }

    /**
     * Only ROLE_MANAGER can create new accounts, as validated in security config. 
     * This method needs to validate customer ID and account details
     * @param account
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public Account addAccount(@Valid @RequestBody Account account) {
        Optional<User> result = userRepo.findById(account.getCustomerId());
        if (!result.isPresent())
            throw new UserNotFoundException(account.getCustomerId());
        User user = result.get();

        if (account.getBalance() < 5000.0 )
            throw new AccountNotValidException("Initial account balance must be more than 5000 ");
        if (Math.round(account.getBalance() * 100) != Math.round(account.getAvailableBalance() * 100)) 
            throw new AccountNotValidException("Initial balance must match available balance");
        
        account.setCustomer(user);
        return accountRepo.save(account);
    }

    /**
     * Only ROLE_USER can view transactions, as validated in security config
     * This method needs to validate account ID, and make sure that the user is the owner of the
     * account
     * @param accountId
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accounts/{account_id}/transactions")
    public List<Transfer> getTransfers(@PathVariable (value = "account_id") Long accountId) {
        Optional<Account> result = accountRepo.findById(accountId);
        if (!result.isPresent()) 
            throw new AccountNotFoundException(accountId);
        Account account = result.get();

        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        User user = userRepo.findByUsername(uDetails.getUsername()).get();
        if (account.getCustomerId() != user.getId())
            throw new RoleNotAuthorisedException("You cannot view another customer's accounts");

        List<Transfer> transfers = transferRepo.findBySenderIdOrReceiverId(accountId, accountId);

        return transfers;
    }

    /**
     * Only ROLE_USER can create a new transfer, as validated in security config
     * This method needs to validate sender and receiver accounts and ensure that the sender has 
     * sufficient funds for transfer
     * @param accountId
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts/{account_id}/transactions")
    public Transfer createTransfer(@PathVariable (value = "account_id") Long senderId, 
    @RequestBody Transfer transfer) {
        if (transfer.getTo() == transfer.getFrom())
            throw new TransferNotValidException("Sender and receiver fields cannot be identical");
            
        if (senderId != transfer.getFrom())
            throw new AccountNotValidException("'From' field must match account ID in URL");

        Optional<Account> sender = accountRepo.findById(transfer.getFrom());
        if (!sender.isPresent()) 
            throw new AccountNotFoundException(transfer.getFrom());
        Account senderAcc = sender.get();

        if (senderAcc.getAvailableBalance() < transfer.getAmount())
            throw new TransferNotValidException("Insufficient funds in account for transfer");
        transfer.setSender(senderAcc);

        Optional<Account> receiver = accountRepo.findById(transfer.getTo());
        if (!receiver.isPresent()) {
            throw new AccountNotFoundException(transfer.getTo());
        }
        Account receiverAcc = receiver.get();
        transfer.setReceiver(receiverAcc);
        
        senderAcc.setAvailableBalance(senderAcc.getAvailableBalance() - transfer.getAmount());
        senderAcc.setBalance(senderAcc.getBalance() - transfer.getAmount());
        accountRepo.save(senderAcc);
        receiverAcc.setAvailableBalance(receiverAcc.getAvailableBalance() + transfer.getAmount());
        receiverAcc.setBalance(receiverAcc.getBalance() + transfer.getAmount());
        accountRepo.save(receiverAcc);

        return transferRepo.save(transfer);
    }
    
}