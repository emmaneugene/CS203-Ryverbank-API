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
import com.csdg1t3.ryverbankapi.account.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private TradeService tradesvc;

    private final String u1_FULL_NAME = "cspotatoes";
    private final String  u1_USERNAME = "potato";
    private final String u1_PASSWORD = "iamgoodpotato123";
    private final String u1_ROLE = "ROLE_USER";
    private final String NRIC = "S1234567G";
    private final String PHONE_NO = "93223235";
    private final String u1_PASSWORD_ENCODED = "$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO";

    private final String u2_FULL_NAME = "Tan Li Ling";
    private final String  u2_USERNAME = "manager_1";
    private final String u2_PASSWORD = "01_manager_01";
    private final String u2_ROLE = "ROLE_MANAGER";  
}
