package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;
import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.account.*;

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
    private final User manager = new User((long) 2,"Tan Li Ling","S1234567G", "93223235", "Potato island", "manager_1","$2a$10$HQKNcTWJ5Teo4dCwUeDc7uzijJoMJWgKvljwzQ/3aAS6w5Gf.Bblu","ROLE_MANAGER", true );

    private final String NEWCUSTOMER_USERNAME = "iamironman";
    private final String NEWCUSTOMER_PASSWORD = "i<3carmen";
    private final String NEWCUSTOMER_PASSWORD_ENCODED = "$2a$10$57cVEHOoCgQ5oXRRw8OD6OgufxqaW84orLtw9moW.cHEgxIFvy/F.";
    
    private final long CUST_ID = (long) 1;
    private final String CUSTOMER_FULL_NAME = "cspotatoes";
    private final String  CUSTOMER_USERNAME = "potato";
    private final String CUSTOMER_PASSWORD = "iamgoodpotato123";
    private final String CUSTOMER_ROLE = "ROLE_USER";
    private final String NRIC = "S1234567G";
    private final String PHONE = "93223235";
    private final String CUSTOMER_PASSWORD_ENCODED = "$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO";

    private static final List<String> VALID_STATUSES = Arrays.asList("open", "partial-filled");

    private Account account = new Account((long) 1, customer, customer.getId(), 1000.00, 1000.00);
    private Account tonyAccount = new Account(Long.valueOf(2), newCustomer, newCustomer.getId(), 100000.0, 100000.0);
    private Stock stock = new Stock("A17U", 3.23, 500, 3.33, 500, 3.35);
    private Trade buy = new Trade(Long.valueOf(1), "buy", stock.getSymbol(), 200, 0.0, stock.getAsk(), 3.33, 200, System.currentTimeMillis(), account, customer, "open", false, 0.0);
    private Trade sell = new Trade(Long.valueOf(2), "sell", stock.getSymbol(), 200, 3.34, stock.getAsk(), 0.0, 0, System.currentTimeMillis(), tonyAccount, newCustomer, "open", false, 0.0);
    private List<Asset> assets = new ArrayList<>();
    private Portfolio portfolio = new Portfolio(Long.valueOf(1), CUST_ID, customer, assets, 0.0, 0.0);
    private Asset asset = new Asset(Long.valueOf(1), sell.getSymbol(), portfolio, 500, 500, 3.23, 3.3);
        
    
    @Test
    void processUnprocessedTrades_noUnprocessedTrades_usesAllMocks() {
        List<Trade> unprocessed = new ArrayList<Trade>();
        List<Trade> processed = new ArrayList<Trade>();

        when(tradeRepo.findByProcessed(false)).thenReturn(unprocessed);
        tradeSvc.processUnprocessedTrades();

        verify(tradeRepo).findByProcessed(false);
    }
    

    @Test 
    void processUnprocessedTrades_listOfUnprocessedTrades_usesAllMocks() {
        List<Trade> trades = new ArrayList<>();
        trades.add(buy);
        trades.add(sell);
        when(tradeRepo.findByProcessed(false)).thenReturn(trades);

        tradeSvc.processUnprocessedTrades();

        verify(tradeRepo).findByProcessed(false);       
    }
    

    @Test
    void expireTrades_noExpiredTrades_usesAllMocks() {
        List<Trade> expired = new ArrayList<Trade>();

        when(tradeRepo.findByStatusIn(VALID_STATUSES)).thenReturn(expired);

        tradeSvc.expireTrades();

        verify(tradeRepo).findByStatusIn(VALID_STATUSES);
    }

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

    @Test
    void processExpiredTrade_SellTrade_usesAllMocks() { 
        assets.add(asset);
        when(assetRepo.findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol())).thenReturn(Optional.of(asset));
        when(assetRepo.save(asset)).thenReturn(asset);

        tradeSvc.processExpiredTrade(sell);

        verify(assetRepo).findByPortfolioCustomerIdAndCode(sell.getCustomer_id(), sell.getSymbol());
        verify(assetRepo).save(asset);
    } 

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

    // @Test
    // void makeTrade_withinTradingTimeAndBuy_returnProcessedTrade() {
    //     Date date = new Date();
    //     date.set(Calendar.HOUR_OF_DAY, 13);
    //     date.set(Calendar.DAY_OF_WEEK, 3);
    //     String action = "buy";
    //     String symbol = buy.getSymbol();
    //     List<Trade> found = new ArrayList<>();
    //     found.add(buy);
        
    //     when(calendar.getInstance()).thenReturn(calendar.setTime(date));
    //     when(tradeRepo.findByActionAndSymbolAndStatusIn(action, symbol, VALID_STATUSES)).thenReturn(found);  
    // }


    @Test
    void makeTrade_withinTradingTimeAndMarketBuy_returnProcessedTrade() {
        
    }

    @Test
    void makeTrade_withinTradingTimeAndSell_returnProcessedTrade() {
        
    }

    @Test
    void makeTrade_withinTradingTimeAndMarketSell_returnProcessedTrade() {
        
    }

    @Test
    void makeTrade_outsideTradingTime_returnUnprocessedTrade() {
        
    }

    @Test
    void processBuy_validTrade_usesAllMocks() {
    }
    
    @Test
    void processBuy_invalidTrade_usesAllMocks() {

    }

    @Test
    void processSell_validTrade_usesAllMocks() {

    }

    @Test
    void processSell_invalidTrade_usesAllMocks() {

    }

    @Test
    void processMarketBuy_validTrade_usesAllMocks() {

    }

    @Test
    void processMarketBuy_invalidTrade_usesAllMocks() {

    }

    @Test
    void processMarketSell_validTrade_usesAllMocks() {

    }

    @Test
    void processMarketSell_invalidTrade_usesAllMocks() {

    }

    @Test
    void fillTrades_validTradesPriceAndQuantity_usesAllMocks() {

    }

    @Test
    void fillTrades_sameBuyerAndSeller_usesAllMocks() {

    }

    // not sure if we need this
    // @Test
    // void fillTrades_invalidBuyOrSell_usesAllMocks() {

    // }

    // @Test
    // void fillTrades_invalidPrice_usesAllMocks() {

    // }

    // @Test
    // void fillTrades_invalidQuantity_usesAllMocks() {

    // }

    @Test
    void createTradeTransfer_validDetails_returnTransfer() {

    }

    @Test
    void createTradeTransfer_invalidSender_returnTransfer() {

    }

    @Test
    void createTradeTransfer_invalidReceiver_returnTransfer() {

    }

    // @Test
    // void updatePortfolioAsset_emptyTransfer_returnEmptyTransfer() {

    // }
}