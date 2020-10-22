package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import com.csdg1t3.ryverbankapi.account.Account;
import com.csdg1t3.ryverbankapi.account.AccountRepository;
import com.csdg1t3.ryverbankapi.security.UserAuthenticator;

import java.util.*;


/**
 * Controller that manages HTTP requests to "/trades"
 */
@RestController
public class TradeController {
    private TradeRepository tradeRepo;
    private TradeService tradeService;
    private AccountRepository accountRepo;
    private StockRepository stockRepo;
    private UserAuthenticator uAuth;

    public TradeController (TradeRepository tradeRepo, TradeService tradeService, 
    AccountRepository accountRepo, StockRepository stockRepo, UserAuthenticator uAuth) {
        this.tradeRepo = tradeRepo;
        this.tradeService = tradeService;
        this.accountRepo = accountRepo;
        this.stockRepo = stockRepo;
        this.uAuth = uAuth;
    }

    /**
     * Retrieves all trades
     * 
     * This method is only authorised for ROLE_USER as configured in SecurityConfig
     * 
     * @param id
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/trades")
    public List<Trade> getTrades() {
        tradeService.updateTradeExpiry();
        return tradeRepo.findAll();
    }

    /**
     * Retrieves a trade according to its ID. If the trade does not exist, throws a
     * TradeNotFoundException.
     * 
     * This method is only authorised for ROLE_USER, as configured in SecurityConfig
     * @param Id
     * @return trade that matches the ID specified
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Long id) {
        tradeService.updateTradeExpiry();
        Optional<Trade> result = tradeRepo.findById(id);
        
        if (!result.isPresent())
            throw new TradeNotFoundException(id);
        
        return result.get();
    }

    /**
     * Creates a new trade. This method performs the following validation steps:
     * 
     * 1. Verify that the customer_id of the trade matches the id of the current authenticated user
     * 2. Verify that the account_id of the trade is valid, and is owned by the authenticated user
     * 3. Verify that action is "buy" or "sell", and check bid or ask accordingly
     * 4. Verify that stock symbol is valid
     * 5. Verify that quantity is a multiple of 100
     * 6. Populate the 'avg_price', 'filled_quantity', 'date' and 'status fields' 
     * 
     * The method then saves the trade and passes it to to tradeService for further processing,
     * if the trade can be filled/partially filled, the 
     * 
     * This method is only authorised for ROLE_USER, as configured in SecurityConfig
     * @param trade
     * @return successfully created trade
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trades")
    public Trade createTrade(@RequestBody Trade trade) {
        tradeService.updateTradeExpiry();

        if (!uAuth.idMatchesAuthenticatedUser(trade.getCustomer_id()))
            throw new TradeNotValidException("you cannot post a trade for another user");
        
        if (!accountRepo.existsById(trade.getAccount_id()))
            throw new TradeNotValidException("account_id is invalid");
        
        Account acc = accountRepo.findById(trade.getAccount_id()).get();
        if (acc.getCustomerId() != trade.getCustomer_id())
            throw new TradeNotValidException("trade must be made with your own account");
        trade.setAccount(acc);
        
        switch (trade.getAction()) {
            case "buy":
                if (trade.getBid() == null) 
                    throw new TradeNotValidException("buy trade should include a bid value");
                if (trade.getBid() < 0)
                    throw new TradeNotValidException("buy trade bid should be non-negative");
                trade.setAsk(null);
                break;
            case "sell":
                if (trade.getAsk() == null) 
                    throw new TradeNotValidException("sell trade should include an ask value");
                if (trade.getAsk() < 0)
                    throw new TradeNotValidException("sell trade ask should be non-negative");
                trade.setBid(null);
                break;
            default:
                throw new TradeNotValidException("trade action can only be 'buy' or 'sell'");
        }

        if (!stockRepo.existsBySymbol(trade.getSymbol()))
            throw new TradeNotValidException("stock symbol is invalid");

        if (trade.getQuantity() % 100 != 0)
            throw new TradeNotValidException("quantity must be a multiple of 100");
        
        trade.setAvg_price(0);
        trade.setFilled_quantity(0);
        trade.setDate(System.currentTimeMillis()/1000);
        trade.setStatus("open");

        Trade savedTrade = tradeRepo.save(trade);
        tradeService.processTrade(tradeRepo.save(trade));
        return savedTrade;
    } 

    /**
     * Cancels an existing trade. Only open trades can be cancelled.
     * 
     * The method does the following:
     * 1. Retrieve the trade at the specified id. Otherwise, throw a TradeNotFoundException
     * 2. Verify that the current authenticated user is the owner of the trade. Otherwise, throw a
     * TradeNotValidException 
     * 3. Verify that the trade's current status is "open". Otherwise, throw a
     * TradeNotValidException
     * 4. Verify that the new status in tradeDetails is "cancelled". Otherwise, throw a 
     * TradeNotValidException
     * 
     * This method is only authorised for ROLE_USER, as configured in SecurityConfig
     * 
     * @param trade
     * @return cancelled trade
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/trades/{id}")
    public void cancelTrade(@PathVariable Long id, @RequestBody Trade tradeDetails) {
        tradeService.updateTradeExpiry();
        Trade trade = getTrade(id);
        
        if (!uAuth.idMatchesAuthenticatedUser(trade.getCustomer_id()))
            throw new TradeNotValidException("You cannot modify another user's trade");
        
        if (!trade.getStatus().equals("open"))
            throw new TradeNotValidException("You can only modify an open trade");
        
        if (tradeDetails.getStatus() == null || !tradeDetails.getStatus().equals("cancelled"))
            throw new TradeNotValidException("Only trade cancellation is supported");
        
        tradeService.cancelTrade(id);
    }
}
