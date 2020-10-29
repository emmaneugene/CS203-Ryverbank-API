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

import javax.validation.Valid;

import com.csdg1t3.ryverbankapi.user.*;

/**
 * Controller that manages HTTP requests to "/accounts"
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
     * Retrieve all the accounts owned by the logged in customer.
     * 
     * Only ROLE_USER can view their own accounts, as validated in security config
     * This method calls getAccount() and returns a list of accounts associated with user's ID
     * If the user ID or role is wrong, a 403 error would be shown.
     * 
     * @return a List containing all of the accounts associated with the user's ID.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        User user = userRepo.findByUsername(uDetails.getUsername()).get();
                
        return accountRepo.findByCustId(user.getId());
    }

    /**
     * Search for the account with the given account ID.
     * 
     * Only ROLE_USER can view their own accounts, as validated in security config
     * This method calls getAccount() and further validates by customer's ID
     * If the user ID, account ID or role is wrong, a 403 error would be shown.
     * 
     * @param id The ID of the account that the customer is accessing.
     * @return The account that is tied to the customer and account ID.
     * @throws AccountNotFoundException If the account is not found.
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
     * Creates a new account.
     * 
     * Only ROLE_MANAGER can create new accounts, as validated in security config. 
     * This method needs to validate customer ID and account details.
     * 
     * @param account The account to be created.
     * @return The account created in database.
     * @throws UserNotFoundException If customer ID is not found
     * @throws AccountNotValidExcception If balance is below 5000 or initial and available balance does not match
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public Account addAccount(@RequestBody Account account) {
        Optional<User> result = userRepo.findById(account.getCustomer_id());
        if (!result.isPresent())
            throw new AccountNotValidException("invalid customer_id");
        User user = result.get();

        account.setAvailable_balance(account.getBalance());
        account.setCustomer(user);
        return accountRepo.save(account);
    }

    /**
     * Retrieve transaction details of the account with the given account ID.
     * 
     * Only ROLE_USER can view transactions, as validated in security config
     * This method needs to validate account ID, and make sure that the user is the owner of the
     * account
     * 
     * @param accountId The account ID of the transactions to be retrieved.
     * @return The transaction details of the account with the given account ID.
     * @throws AccountNotFoundException If account ID is not found
     * @throws RoleNotAuthorizedException If the user ID of the person accessing the account 
     *                                    does not match the user ID tied to the account.
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
        if (account.getCustomer_id() != user.getId())
            throw new RoleNotAuthorisedException("You cannot view another customer's accounts");

        List<Transfer> transfers = transferRepo.findBySenderIdOrReceiverId(accountId, accountId);

        return transfers;
    }

    /**
     * Create a new transaction.
     * 
     * Only ROLE_USER can create a new transfer, as validated in security config
     * This method needs to 
     * 1. validate sender and receiver accounts
     * 2. ensure that user sending the request owns the account
     * 3. ensure that the sender has sufficient funds for transfer
     * 
     * @param accountId The account ID that the sender wishes to transfer money out from.
     * @return The transaction created in the database.
     * @throws TransferNotValidException If sender and receiver fields are the same or there is insufficient balance.
     * @throws AccountNotValidException If account ID in sender field is different from the logged in customer's account.
     * @throws AccountNotFoundException If sender or receiver account is not found.
     * @throws RoleNotAuthorisedException If account is not owned by the sender customer.
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

        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        User user = userRepo.findByUsername(uDetails.getUsername()).get();
        if (senderAcc.getCustomer_id() != user.getId())
            throw new RoleNotAuthorisedException("You cannot transfer funds from another person's account");

        if (senderAcc.getAvailable_balance() < transfer.getAmount())
            throw new TransferNotValidException("Insufficient funds in account for transfer");
        transfer.setSender(senderAcc);

        Optional<Account> receiver = accountRepo.findById(transfer.getTo());
        if (!receiver.isPresent()) {
            throw new AccountNotFoundException(transfer.getTo());
        }
        Account receiverAcc = receiver.get();
        transfer.setReceiver(receiverAcc);
        
        senderAcc.setAvailable_balance(senderAcc.getAvailable_balance() - transfer.getAmount());
        senderAcc.setBalance(senderAcc.getBalance() - transfer.getAmount());
        accountRepo.save(senderAcc);
        receiverAcc.setAvailable_balance(receiverAcc.getAvailable_balance() + transfer.getAmount());
        receiverAcc.setBalance(receiverAcc.getBalance() + transfer.getAmount());
        accountRepo.save(receiverAcc);

        return transferRepo.save(transfer);
    }

    /**
     * Creates a trade transfer between two accounts. The method will also update the balances of
     * both sender and receiver accounts as necessary
     * 
     * If the sender or receiver account is null, that account is associated with a market maker
     * trade, and hence no account operations will occur
     * 
     * @param transfer
     * @param sender
     * @param receiver
     * @return created transfer
     */
    public Transfer createTradeTransfer(Transfer transfer, Account sender, Account receiver) {
        if (sender != null) {
            if (Math.round(sender.getAvailable_balance() * 100) == Math.round(sender.getBalance() * 100))
                sender.setAvailable_balance(sender.getAvailable_balance() - transfer.getAmount());
        
            sender.setBalance(sender.getBalance() - transfer.getAmount());
            accountRepo.save(sender);
        }
        
        if (receiver != null) {
            receiver.setAvailable_balance(receiver.getAvailable_balance() + transfer.getAmount());
            receiver.setBalance(receiver.getBalance() + transfer.getAmount());
            accountRepo.save(receiver);
        }

        return transferRepo.save(transfer);
    }
    
}