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
import com.csdg1t3.ryverbankapi.security.*;
import com.csdg1t3.ryverbankapi.trade.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;

@ExtendWith(MockitoExtension.class)
public class PortfolioControllerTest {
    @Mock
    private AssetRepository assetRepo;

    @Mock
    private PortfolioRepository portfolioRepo;

    @Mock
    private StockService stockSvc;

    @Mock
    private UserAuthenticator uAuth;

    @InjectMocks
    PortfolioController portfolioController;

    private final String u1_FULL_NAME = "cspotatoes";
    private final String  u1_USERNAME = "potato";
    private final String u1_PASSWORD = "iamgoodpotato123";
    private final String u1_ROLE = "ROLE_USER";
    private final String NRIC = "S1234567G";
    private final String PHONE_NO = "93223235";

    @Test
    void getPortfolio_validUser_ReturnPortfolio() {
        Long id = Long.valueOf(1);
        User user = new User(id, u1_FULL_NAME, NRIC, PHONE_NO, "Test Address", u1_USERNAME, u1_PASSWORD, u1_ROLE, true);
        List<Asset> assets = new ArrayList<Asset>();
        Portfolio portfolio = new Portfolio(id, user.getId(), user, assets, 0.0, 0.0);
        
        when(uAuth.getAuthenticatedUser()).thenReturn(user);
        when(portfolioRepo.findByCustomerId(any(Long.class))).thenReturn(Optional.of(portfolio));

        Portfolio returnedPortfolio = portfolioController.getPortfolio();

        assertEquals(returnedPortfolio, portfolio);
        verify(uAuth).getAuthenticatedUser();
        verify(portfolioRepo).findByCustomerId(id);
    }
}
