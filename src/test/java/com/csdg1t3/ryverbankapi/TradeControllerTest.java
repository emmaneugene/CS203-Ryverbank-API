package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.security.UserAuthenticator;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;

@ExtendWith(MockitoExtension.class)
public class TradeControllerTest {
    @Mock 
    private TradeRepository tradeRepo;

    @Mock
    private TradeService tradeSvc;

    @Mock
    private StockRepository stockRepo;

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private AssetRepository assetRepo;

    @Mock
    private UserAuthenticator uAuth;

    @InjectMocks
    private TradeController tradeController;

    private User user = new User((long) 1, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
    private Account account = new Account((long) 1, user, user.getId(), 1000.00, 1000.00);
    private Stock stock = new Stock("A17U", 3.23, 500, 3.33, 500, 3.35);
    private Trade buy = new Trade(Long.valueOf(1), "buy", stock.getSymbol(), 200, 3.34, stock.getAsk(), 0.0, 0, System.currentTimeMillis(), account, user, "open", false, 0.0);
    private Trade sell = new Trade(Long.valueOf(2), "sell", stock.getSymbol(), 0, 0.0, stock.getAsk(), 3.33, 200, System.currentTimeMillis(), account, user, "open", false, 0.0);

    // assert that a list of a user's trades will be returned 
    @Test
    void getTrades_validUser_returnTrades() {
        Long id = Long.valueOf(1);

        List<Trade> found = new ArrayList<Trade>();
        found.add(buy);

        when(tradeRepo.findByCustomerId(id)).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user);

        List<Trade> returnedList = tradeController.getTrades();

        assertEquals(found, returnedList);
        verify(tradeRepo).findByCustomerId(id);
        verify(uAuth).getAuthenticatedUser();
    }

    // assert that a RoleNotAuthorisedException will be thrown
    // when the trade id given is not owned by the person looking for the specific trade
   @Test
   void getTrade_notOwner_ThrowTradeNotFoundException(){  
        Long tradeId = buy.getId();    
        when(tradeRepo.existsById(tradeId)).thenReturn(true);
        when(tradeRepo.findById(tradeId)).thenReturn(Optional.of(buy));
        when(uAuth.idMatchesAuthenticatedUser(tradeId)).thenReturn(false);

        assertThrows(RoleNotAuthorisedException.class, () -> tradeController.getTrade(tradeId), "You are not allowed to view another user's trades");
        verify(tradeRepo).existsById(tradeId);
        verify(tradeRepo).findById(tradeId);
        verify(uAuth).idMatchesAuthenticatedUser(tradeId);
   }
   
    // assert that a TradeNotFoundException will be throw when the trade id given is invalid
    @Test
    void getTrade_invalidTrade_ThrowTradeNotFoundException(){
        Long id = Long.valueOf(100);
        
        when(tradeRepo.existsById(id)).thenReturn(false);

        assertThrows(TradeNotFoundException.class, () -> tradeController.getTrade(id), "Could not find trade " + id);
        verify(tradeRepo).existsById(id);
    }   

   //assert that trade is created if customer id of trade matches current user
   @Test
    void createTrade_validBuyTrade_returnTrades(){
        Long accId = buy.getAccount_id();

        when(uAuth.idMatchesAuthenticatedUser(buy.getCustomer_id())).thenReturn(true);
        when(accountRepo.existsById(accId)).thenReturn(true);
        when(accountRepo.findById(accId)).thenReturn(Optional.of(account));
        when(uAuth.getAuthenticatedUser()).thenReturn(user);
        when(stockRepo.existsBySymbol(buy.getSymbol())).thenReturn(true);
        when(tradeRepo.save(any(Trade.class))).thenReturn(buy);
        when(tradeSvc.makeTrade(any(Trade.class))).thenReturn(buy);

        Trade returned = tradeController.createTrade(buy);
        assertEquals(returned, buy);
        verify(uAuth).idMatchesAuthenticatedUser(buy.getCustomer_id());
        verify(accountRepo).existsById(accId);
        verify(accountRepo).findById(accId);
        verify(uAuth).getAuthenticatedUser();
        verify(stockRepo).existsBySymbol(buy.getSymbol());
    }
   
   //assert that exception is thrown if customer id don't match user id
   @Test
   void createTrade_notOwner_ThrowTradeNotValidException(){
       when(uAuth.idMatchesAuthenticatedUser(any(Long.class))).thenReturn(false);
       assertThrows(TradeNotValidException.class, () -> tradeController.createTrade(buy), "You cannot post a trade for another user");
       verify(uAuth).idMatchesAuthenticatedUser(buy.getId());
   }

    //assert that trade is cancelled
    @Test    
    void cancelTrade_validTrade_returnCancelledTrade(){ 
        Long tradeId = buy.getId();
        Long customerId = buy.getCustomer_id();

        Trade tradeDetails = new Trade(Long.valueOf(2), "buy", buy.getSymbol(), buy.getQuantity(), buy.getBid(), 0.0, 0.0, 0, System.currentTimeMillis(), account, user, "cancelled", false, 0.0);

        when(tradeRepo.existsById(any(Long.class))).thenReturn(true);
        when(tradeRepo.findById(any(Long.class))).thenReturn(Optional.of(buy));
        when(uAuth.idMatchesAuthenticatedUser(any(Long.class))).thenReturn(true);

        Trade cancelledTrade = tradeController.cancelTrade(tradeId, tradeDetails); 
        assertEquals(buy, cancelledTrade);
        verify(uAuth).idMatchesAuthenticatedUser(customerId);
        verify(tradeRepo).existsById(tradeId);
        verify(tradeRepo).findById(tradeId);
   }
   
    // assert that exception is thrown if trade is not valid
    // i.e. trade is not marked as "open"
    @Test 
    void cancelTrade_invalidTrade_throwTradeNotValidException(){
        buy.setStatus("filled");
        Long tradeId = buy.getId();
        Trade tradeDetails = new Trade(Long.valueOf(2), "buy", buy.getSymbol(), buy.getQuantity(), buy.getBid(), 0.0, 0.0, 0, System.currentTimeMillis(), account, user, "cancelled", false, 0.0);

        when(tradeRepo.existsById(any(Long.class))).thenReturn(true);
        when(tradeRepo.findById(any(Long.class))).thenReturn(Optional.of(buy));
        when(uAuth.idMatchesAuthenticatedUser(any(Long.class))).thenReturn(true);
        
        assertThrows(TradeNotValidException.class, () -> tradeController.cancelTrade(tradeId, tradeDetails), "You can only modify an open trade");
        verify(tradeRepo).existsById(tradeId);
        verify(tradeRepo).findById(tradeId);
        verify(uAuth).idMatchesAuthenticatedUser(tradeId);
    }
    
    //assert that exception is thrown if the current user does not match the trade owner
    @Test 
    void cancelTrade_invalidUser_ThrowTradeNotValidException(){
        when(tradeRepo.existsById(any(Long.class))).thenReturn(true);
        when(tradeRepo.findById(any(Long.class))).thenReturn(Optional.of(buy));
        when(uAuth.idMatchesAuthenticatedUser(any(Long.class))).thenReturn(false);
        
        assertThrows(TradeNotValidException.class, () -> tradeController.cancelTrade(buy.getId(), buy), "You cannot modify another user's trade");
        verify(tradeRepo).existsById(buy.getId());
        verify(tradeRepo).findById(buy.getId());
        verify(uAuth).idMatchesAuthenticatedUser(buy.getId());
    }

    //assert that exception is thrown if tradeDetails is not "cancelled"
    @Test
    void cancelTrade_invalidTradeDeatils_ThrowTradeNotValidException(){
        buy.setStatus("filled");
        Long tradeId = buy.getId();
        Trade tradeDetails = new Trade(Long.valueOf(2), "buy", buy.getSymbol(), buy.getQuantity(), buy.getBid(), 0.0, 0.0, 0, System.currentTimeMillis(), account, user, null, false, 0.0);

        when(tradeRepo.existsById(any(Long.class))).thenReturn(true);
        when(tradeRepo.findById(any(Long.class))).thenReturn(Optional.of(buy));
        when(uAuth.idMatchesAuthenticatedUser(any(Long.class))).thenReturn(true);
        
        assertThrows(TradeNotValidException.class, () -> tradeController.cancelTrade(tradeId, tradeDetails), "Only trade cancellation is supported");
        verify(tradeRepo).existsById(tradeId);
        verify(tradeRepo).findById(tradeId);
        verify(uAuth).idMatchesAuthenticatedUser(tradeId);
    }


    


   

   


}
