package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.security.UserAuthenticator;

import java.util.*;


/**
 * Controller that manages HTTP requests to "/trades"
 */
@RestController
public class TradeController {
    private TradeRepository tradeRepo;
    private TradeService tradeService;
    private StockRepository stockRepo;
    private AccountRepository accountRepo;
    private AssetRepository assetRepo;
    private UserAuthenticator uAuth;

    public TradeController (TradeRepository tradeRepo, TradeService tradeService, 
    AccountRepository accountRepo, StockRepository stockRepo, AssetRepository assetRepo, UserAuthenticator uAuth) {
        this.tradeRepo = tradeRepo;
        this.tradeService = tradeService;
        this.stockRepo = stockRepo;
        this.accountRepo = accountRepo;
        this.assetRepo = assetRepo;
        this.uAuth = uAuth;
    }

     /**
     * Retrieves all trades that the authenticated user has made
     * 
     * This method is only authorised for ROLE_USER as configured in SecurityConfig
     * 
     * @return a list containing all of the trades associated with the authenticated user
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/trades")
    public List<Trade> getTrades() {
        tradeService.updateTradeExpiry();

        return tradeRepo.findByCustomerId(uAuth.getAuthenticatedUser().getId());
    }

    /**
     * Retrieves a trade according to its ID. If the trade does not exist, throws a
     * TradeNotFoundException.
     * 
     * This method is only authorised for ROLE_USER, as configured in SecurityConfig
     * 
     * @param id The ID of the trade to be retrieved
     * @return Trade that matches the ID specified
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Long id) {
        tradeService.updateTradeExpiry();

        if (!tradeRepo.existsById(id))
            throw new TradeNotFoundException(id);
        
        Trade trade = tradeRepo.findById(id).get();
        if (!uAuth.idMatchesAuthenticatedUser(trade.getCustomer_id()))
            throw new RoleNotAuthorisedException("You are not allowed to view another user's trades");
        
        return trade;
    }

    /**
     * Creates a new trade. This method performs the following validation steps:
     * 
     * 1. Verify that the customer_id of the trade matches the id of the current authenticated user
     * 2. Verify that the account_id of the trade is valid, and is owned by the authenticated user
     * 3. Verify that action is "buy" or "sell", and check bid or ask accordingly
     * 4. Verify that stock symbol is valid
     * 5. Verify that quantity is a multiple of 100
     * 6. Verify sufficient funds in account (buy) or sufficient assets (sell)
     * 7. Populate the 'avg_price', 'filled_quantity', 'date' and 'status fields' 
     * 
     * The method then saves the trade and passes it to to tradeService for further processing.
     * 
     * This method is only authorised for ROLE_USER, as configured in SecurityConfig
     * 
     * @param trade The trade to be created.
     * @return The trade created in database.
     * @throws TradeNotValidException If trade conditions are not met.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trades")
    public Trade createTrade(@RequestBody Trade trade) {
        tradeService.updateTradeExpiry();

        if (!uAuth.idMatchesAuthenticatedUser(trade.getCustomer_id()))
            throw new TradeNotValidException("You cannot post a trade for another user");
        
        if (!accountRepo.existsById(trade.getAccount_id()))
            throw new TradeNotValidException("Account_id is invalid");
        
        Account acc = accountRepo.findById(trade.getAccount_id()).get();
        User cust = uAuth.getAuthenticatedUser();
        if (acc.getCustomer_id() != trade.getCustomer_id())
            throw new TradeNotValidException("Trade must be made with your own account");
        trade.setAccount(acc);
        trade.setCustomer(cust);
        
        switch (trade.getAction()) {
            case "buy":
                if (trade.getBid() == null) 
                    throw new TradeNotValidException("Buy trade should include a bid value");
                if (trade.getBid() < 0)
                    throw new TradeNotValidException("Buy trade bid should be non-negative");
                trade.setAsk(null);
                break;
            case "sell":
                if (trade.getAsk() == null) 
                    throw new TradeNotValidException("Sell trade should include an ask value");
                if (trade.getAsk() < 0)
                    throw new TradeNotValidException("Sell trade ask should be non-negative");
                trade.setBid(null);
                break;
            default:
                throw new TradeNotValidException("Trade action can only be 'buy' or 'sell'");
        }

        if (!stockRepo.existsBySymbol(trade.getSymbol()))
            throw new TradeNotValidException("Stock symbol is invalid");

        if (trade.getQuantity() % 100 != 0)
            throw new TradeNotValidException("Quantity must be a multiple of 100");

        if (trade.getAction().equals("buy") && trade.getBid() > 0) {
            Double avail = acc.getAvailable_balance();
            Double needed = trade.getBid() * trade.getQuantity();
            if (avail < needed)
                throw new TradeNotValidException("Insufficient funds for trade");
            
            acc.setAvailable_balance(avail - needed);
            accountRepo.save(acc);
        } else if (trade.getAction().equals("sell")) {
            Optional<Asset> result = assetRepo.findByPortfolioCustomerIdAndCode(
                cust.getId(), trade.getSymbol());
            if (!result.isPresent()) 
                throw new TradeNotValidException("No assets of the required stock found");
            
            Asset asset = result.get();
            int avail = asset.getAvailable_quantity();
            int needed = trade.getQuantity();
            if (avail < needed)
                throw new TradeNotValidException("Insufficient quantity of assets for trade");
            asset.setAvailable_quantity(avail - needed);
            assetRepo.save(asset);
        }

        trade.setAvg_price(0);
        trade.setFilled_quantity(0);
        trade.setDate(System.currentTimeMillis());
        trade.setStatus("open");

        return tradeService.makeTrade(tradeRepo.save(trade));
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
     * @param trade The trade to be cancelled.
     * @return The cancelled trade.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/trades/{id}")
    public Trade cancelTrade(@PathVariable Long id, @RequestBody Trade tradeDetails) {
        Trade trade = getTrade(id);
        
        if (!uAuth.idMatchesAuthenticatedUser(trade.getCustomer_id()))
            throw new TradeNotValidException("You cannot modify another user's trade");
        
        if (!trade.getStatus().equals("open"))
            throw new TradeNotValidException("You can only modify an open trade");
        
        if (tradeDetails.getStatus() == null || !tradeDetails.getStatus().equals("cancelled"))
            throw new TradeNotValidException("Only trade cancellation is supported");
        
        tradeService.processCancelTrade(trade);
        return trade;
    }
}
