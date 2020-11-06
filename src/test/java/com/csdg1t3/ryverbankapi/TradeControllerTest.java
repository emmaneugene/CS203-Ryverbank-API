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

    @Test
    void getTrades_validUser_returnTrades() {
        Long id = Long.valueOf(1);

        List<Trade> found = new ArrayList<Trade>();

        when(tradeRepo.findByCustomerId(id)).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user);

        List<Trade> returnedList = tradeController.getTrades();

        assertEquals(found, returnedList);
        verify(tradeRepo).findByCustomerId(id);
        verify(uAuth).getAuthenticatedUser();
    }
}
