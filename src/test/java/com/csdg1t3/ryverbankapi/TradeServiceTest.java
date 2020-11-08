package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;
import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.account.*;

import java.security.Timestamp;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {
    @Mock
    private TradeRepository tradeRepo;

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private TransferRepository transferRepo;

    @Mock
    private AssetRepository assetRepo;

    @Mock
    private PortfolioRepository portfolioRepo;

    @Mock
    private StockRepository stockRepo;

    @Mock
    private Calendar calendar;

    @InjectMocks
    private TradeService tradeSvc;

    private final User customer = new User((long) 1,"cspotatoes","S1234567G", "93223235", "Potato island", "potato","$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO","ROLE_USER", true );
    private final User newCustomer = new User((long) 3,"tony starks","S1234567G", "93223235", "starship", "iamironman","i<3carmen","ROLE_USER", true );
    
    private final long CUST_ID = (long) 1;

    private static final List<String> VALID_STATUSES = Arrays.asList("open", "partial-filled");

    private Account account = new Account((long) 1, customer, customer.getId(), 1000.00, 1000.00);
    private Account tonyAccount = new Account(Long.valueOf(2), newCustomer, newCustomer.getId(), 100000.0, 100000.0);
    private Stock stock = new Stock("A17U", 3.23, 500, 3.33, 500, 3.35);
    private Trade buy = new Trade(Long.valueOf(1), "buy", stock.getSymbol(), 200, 3.34, stock.getAsk(), 3.33, 200, System.currentTimeMillis(), account, customer, "open", false, 0.0);
    private Trade sell = new Trade(Long.valueOf(2), "sell", stock.getSymbol(), 0, 0.0, stock.getAsk(), 3.33, 200, System.currentTimeMillis(), tonyAccount, newCustomer, "open", false, 0.0);
    private List<Asset> assets = new ArrayList<>();
    private Portfolio portfolio = new Portfolio(Long.valueOf(1), CUST_ID, customer, assets, 0.0, 0.0);
    private Asset asset = new Asset(Long.valueOf(1), sell.getSymbol(), portfolio, 200, 200, 3.33, 666);
        
    //assert that all mocks are used when given zero unprocessed trades
    @Test
    void processUnprocessedTrades_noUnprocessedTrades_usesAllMocks() {
        List<Trade> unprocessed = new ArrayList<Trade>();
        List<Trade> processed = new ArrayList<Trade>();

        when(tradeRepo.findByProcessed(false)).thenReturn(unprocessed);
        tradeSvc.processUnprocessedTrades();

        verify(tradeRepo).findByProcessed(false);
    }
    
    //assert that all mocks are used when given a list of unprocessed trades
    @Test 
    void processUnprocessedTrades_listOfUnprocessedTrades_usesAllMocks() {
        List<Trade> trades = new ArrayList<>();
        trades.add(buy);
        trades.add(sell);
        when(tradeRepo.findByProcessed(false)).thenReturn(trades);

        tradeSvc.processUnprocessedTrades();

        verify(tradeRepo).findByProcessed(false);       
    }
    
    //assert that all mocks are used when there are no expired trades 
    @Test
    void expireTrades_noExpiredTrades_usesAllMocks() {
        List<Trade> expired = new ArrayList<Trade>();

        when(tradeRepo.findByStatusIn(VALID_STATUSES)).thenReturn(expired);

        tradeSvc.expireTrades();

        verify(tradeRepo).findByStatusIn(VALID_STATUSES);
    }

    // assert that all mocks are used when there are expired trades
    @Test
    void expireTrades_listOfExpiredTrades_usesAllMocks() {
        List<Trade> trades = new ArrayList<>();
        trades.add(buy);
        trades.add(sell);

        when(tradeRepo.findByStatusIn(VALID_STATUSES)).thenReturn(trades);
        when(accountRepo.findById(buy.getId())).thenReturn(Optional.of(account));
        when(accountRepo.save(account)).thenReturn(account);
        when(assetRepo.findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol())).thenReturn(Optional.of(asset));
        when(assetRepo.save(asset)).thenReturn(asset);

        tradeSvc.expireTrades();

        verify(tradeRepo).findByStatusIn(VALID_STATUSES);
        verify(accountRepo).findById(buy.getId());
        verify(accountRepo).save(account);
        verify(assetRepo).findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol());
        verify(assetRepo).save(asset);
    }
    
    //assert that for sell trade, all mocks are used 
    @Test
    void processExpiredTrade_SellTrade_usesAllMocks() { 
        assets.add(asset);
        when(assetRepo.findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol())).thenReturn(Optional.of(asset));
        when(assetRepo.save(asset)).thenReturn(asset);

        tradeSvc.processExpiredTrade(sell);

        verify(assetRepo).findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol());
        verify(assetRepo).save(asset);
    } 

    //assert that for buy trade, all mocks are used 
    @Test
    void processExpiredTrade_BuyTrade_usesAllMocks() { 
        when(accountRepo.findById(buy.getAccount_id())).thenReturn(Optional.of(account));
        when(accountRepo.save(account)).thenReturn(account);

        tradeSvc.processExpiredTrade(buy);
        
        verify(accountRepo).findById(buy.getAccount_id());
        verify(accountRepo).save(account);
    } 

    @Test
    void listValidBuyTradesForStock_noTrades_returnEmptyList() {
        List<Trade> found = new ArrayList<Trade>();
        String action = "buy";
        String symbol = "invalidStock";

        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);
        
        List<Trade> returnedList = tradeSvc.listValidBuyTradesForStock(symbol);

        assertEquals(found, returnedList);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void listValidBuyTradesForStock_tradesAvailable_returnListOfBuyTrades() {
        List<Trade> found = new ArrayList<Trade>();
        found.add(buy);
        String action = "buy";
        String symbol = buy.getSymbol();

        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);
        
        List<Trade> returnedList = tradeSvc.listValidBuyTradesForStock(symbol);

        assertEquals(found, returnedList);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void listValidSellTradesForStock_noTrades_usesAllMocks() {
        List<Trade> found = new ArrayList<Trade>();
        String action = "sell";
        String symbol = "invalidStock";

        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);
        
        List<Trade> returnedList = tradeSvc.listValidSellTradesForStock(symbol);

        assertEquals(found, returnedList);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    } 

    @Test
    void listValidSellTradesForStock_tradesAvailable_returnListOfSellTrades() {
        List<Trade> found = new ArrayList<Trade>();
        String action = "sell";
        found.add(sell);
        String symbol = sell.getSymbol();

        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);
        
        List<Trade> returnedList = tradeSvc.listValidSellTradesForStock(symbol);

        assertEquals(found, returnedList);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void getLowestAskTradeForStock_validStock_returnLowestAskTrade() {
        List<Trade> validTrades = new ArrayList<Trade>();
        validTrades.add(sell);
        String action = "sell";
        String symbol = sell.getSymbol();
        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(validTrades);

        Trade returned = tradeSvc.getLowestAskTradeForStock(symbol);

        assertEquals(returned, sell);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void getLowestAskTradeForStock_invalidStock_returnNull() {
        List<Trade> invalidTrades = new ArrayList<Trade>();
        String action = "sell";
        String symbol = "invalidStock";
        
        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(invalidTrades);
        
        Trade returned = tradeSvc.getLowestAskTradeForStock(symbol);

        assertNull(returned);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void getLowestAskTradeForStock_invalidAsk_returnNull() {
        List<Trade> found = new ArrayList<Trade>();
        sell.setAsk(0.0);
        found.add(sell);
        String action = "sell";
        String symbol = sell.getSymbol();
        
        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);

        Trade returned = tradeSvc.getLowestAskTradeForStock(symbol);

        assertNull(returned);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void getHighestBidTradeForStock_validStock_returnHighestBidTrade() {
        List<Trade> validTrades = new ArrayList<Trade>();
        buy.setBid(10.0);
        validTrades.add(buy);
        String action = "buy";
        String symbol = buy.getSymbol();
        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(validTrades);
        Trade returned = tradeSvc.getHighestBidTradeForStock(symbol);
        
        assertEquals(returned, buy);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);

    } 

    @Test
    void getHighestBidTradeForStock_invalidStock_returnNull() {
        List<Trade> invalidTrades = new ArrayList<Trade>();
        String action = "buy";
        String symbol = "invalidStock";
        
        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(invalidTrades);
        
        Trade returned = tradeSvc.getHighestBidTradeForStock(symbol);

        assertNull(returned);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void getHighestBidTradeForStock_invalidBid_returnNull() {
        List<Trade> found = new ArrayList<Trade>();
        buy.setBid(0.0);
        found.add(buy);
        String action = "buy";
        String symbol = buy.getSymbol();
        
        when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);

        Trade returned = tradeSvc.getHighestBidTradeForStock(symbol);

        assertNull(returned);
        verify(tradeRepo).findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES);
    }

    @Test
    void fillTrades_validTradesPriceAndQuantity_tradesStatusNotOpen() {
        Portfolio sellerPortfolio = new Portfolio(Long.valueOf(2), newCustomer.getId(), customer, assets, 0.0, 0.0);
        Optional<Portfolio> sellerFound = Optional.of(sellerPortfolio);
        Optional<Portfolio> buyerFound = Optional.of(portfolio);
        when(portfolioRepo.findByCustomerId(sell.getCustomer_id())).thenReturn(sellerFound);
        when(portfolioRepo.findByCustomerId(buy.getCustomer_id())).thenReturn(buyerFound);
        when(stockRepo.findBySymbol(any(String.class))).thenReturn(Optional.of(stock));

        tradeSvc.fillTrades(buy, sell, sell.getAsk(), sell.getQuantity());

        assertNotEquals(sell.getStatus(), "open");
        assertNotEquals(buy.getStatus(), "open");
        verify(portfolioRepo).findByCustomerId(sell.getCustomer_id());
        verify(portfolioRepo).findByCustomerId(buy.getCustomer_id());
        verify(stockRepo).findBySymbol(sell.getSymbol());
    }

    @Test
    void fillTrades_sameBuyerAndSeller_tradesStatusOpen() {
        sell.setAccount(account);
        sell.setCustomer(customer);
        sell.setCustomer_id(customer.getId());

        tradeSvc.fillTrades(buy, sell, sell.getAsk(), sell.getQuantity());

        assertEquals(buy.getStatus(), "open");
        assertEquals(sell.getStatus(), "open");
    }

    @Test 
    void processCancelTrade_sellingTrade_statusChanged() {
        when(assetRepo.findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol())).thenReturn(Optional.of(asset));

        tradeSvc.processCancelTrade(sell);

        assertEquals("cancelled", sell.getStatus());
        verify(assetRepo).findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol());
    }

    @Test 
    void processCancelTrade_buyingTrade_statusChanged() {
        Optional<Account> found = Optional.of(account);
        when(accountRepo.findById(buy.getCustomer_id())).thenReturn(found);

        tradeSvc.processCancelTrade(buy);

        assertEquals("cancelled", buy.getStatus());
        verify(accountRepo).findById(buy.getCustomer_id());
    }

}