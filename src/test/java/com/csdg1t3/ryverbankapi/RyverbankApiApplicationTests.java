package com.csdg1t3.ryverbankapi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.*;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.content.*;
import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.security.*;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
class RyverbankApiApplicationTests {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    /**
     * Use TestRestTemplate for testing a real instance of your application as an external actor.
     * TestRestTemplate is just a convenient subclass of RestTemplate that is suitable for integration tests.
     * It is fault tolerant, and optionally can carry Basic authentication headers.
        */
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransferRepository transferRepo;

    @Autowired
    private ContentRepository contentRepo;

    @Autowired
    private StockRepository stockRepo;

    @Autowired 
    private TradeRepository tradeRepo;

    @Autowired 
    private AssetRepository assetRepo;

    @Autowired 
    private PortfolioRepository portfolioRepo;

    @Autowired
    private MarketMaker marketMaker;

    @Autowired
    private BCryptPasswordEncoder encoder;


    // Before each test, create manager, 2 users and 2 analyst accounts
    @BeforeEach
    void setUp(){
        User admin = new User(null, "admin", "S1234567G", "81756529", "Lalaland 10, Potato's Dream, 10200", "manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", true);
        User tstark = new User(null, "Tony Stark", "S8732269I", "81437586", "10880 Malibu Point, 90265", "iamironman", encoder.encode("i<3you3000"), "ROLE_USER", true);
        User tholland = new User(null, "Tom Holland", "S9847385E", "93580378", "20 Ingram Street", "spiderman", encoder.encode("mrstark,Idontfeels0good"), "ROLE_USER", true);
        User analyst1 = new User(null, "analyst 1", "S1234567D", "00000000", "no address", "analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST", true);
        User analyst2 = new User(null, "analyst 2", "S2331492D", "00000000", "no address", "analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST", true);

        userRepo.save(admin);
        userRepo.save(tstark);
        userRepo.save(tholland);
        userRepo.save(analyst1);
        userRepo.save(analyst2);

        portfolioRepo.save(new Portfolio(null, tstark.getId(),tstark, null, 0, 0));
        portfolioRepo.save(new Portfolio(null, tholland.getId(), tholland, null, 0, 0));

        contentRepo.save(new Content(null, "Title1", "Summary1", "Content1", "Link1", true));
        contentRepo.save(new Content(null, "Title2", "Summary2", "Content2", "Link2", false));
        
        marketMaker.initMarket();
    }

    @AfterEach
    void tearDown(){
        contentRepo.deleteAll();
        tradeRepo.deleteAll();
        stockRepo.deleteAll();
        portfolioRepo.deleteAll();
        userRepo.deleteAll();
    }

    // General: Unmapped URIs return 404 not found
    @Test
    void unmappedURIs_NotFound() throws Exception {
        URI uri = new URI(baseUrl + port + "/random/mapping");
        ResponseEntity<Object> result = restTemplate.getForEntity(uri, Object.class);

        assertEquals(404, result.getStatusCode().value());
    }

    // SecurityConfig: Unauthorized users cannot access any primary URIs, should receive 401 unauthorized
    @Test
    void noCredentials_ViewURIs_Unauthorized() throws Exception {
        URI customersUri = new URI(baseUrl + port + "/api/customers");
        ResponseEntity<Object> customersResult = restTemplate.getForEntity(customersUri, Object.class);

        URI accountsUri = new URI(baseUrl + port + "/api/accounts");
        ResponseEntity<Object> accountsResult = restTemplate.getForEntity(accountsUri, Object.class);

        URI contentsUri = new URI(baseUrl + port + "/api/contents");
        ResponseEntity<Object> contentsResult = restTemplate.getForEntity(contentsUri, Object.class);

        URI stocksUri = new URI(baseUrl + port + "/api/stocks");
        ResponseEntity<Object> stocksResult = restTemplate.getForEntity(stocksUri, Object.class);

        URI tradesUri = new URI(baseUrl + port + "/api/trades");
        ResponseEntity<Object> tradesResult = restTemplate.getForEntity(tradesUri, Object.class);

        URI portfolioUri = new URI(baseUrl + port + "/api/portfolio");
        ResponseEntity<Object> portfolioResult = restTemplate.getForEntity(portfolioUri, Object.class);

        assertEquals(401, customersResult.getStatusCode().value());
        assertEquals(401, accountsResult.getStatusCode().value());
        assertEquals(401, contentsResult.getStatusCode().value());
        assertEquals(401, stocksResult.getStatusCode().value());
        assertEquals(401, tradesResult.getStatusCode().value());
        assertEquals(401, portfolioResult.getStatusCode().value());
        
    }

/* 
-------------------------------------------CUSTOMERS------------------------------------------------
*/

    // Users: Manager can view all users
    // We do not deserialize User from ResponseEntity, due to a mismatch in JSON output 
    // Currently, "authorities" is an array of strings when returned from the API due to overridden
    // methods from UserDetails
	@Test
    void manager_ViewUsers_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/customers");

        ResponseEntity<Object[]> result = restTemplate.withBasicAuth("manager_1", "01_manager_01")
                                        .getForEntity(uri, Object[].class);
        

        assertEquals(200, result.getStatusCode().value());
    }

    // Users: User cannot view all users, should receive 403 foridden
	@Test
    void user_ViewUsers_Forbidden() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/customers");

        ResponseEntity<Object> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                        .getForEntity(uri, Object.class);
        
        assertEquals(403, result.getStatusCode().value());
    }

    // Users: User can view their own profile
    @Test
    void user_ViewOwnProfile_OK() throws Exception {
        Long id = userRepo.findByUsername("iamironman").get().getId();
        URI uri = new URI(baseUrl + port + "/api/customers/" + id);

        ResponseEntity<Object> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                        .getForEntity(uri, Object.class);
        

        assertEquals(200, result.getStatusCode().value());
    }

    // Users: User cannot create a new user
    @Test
    void user_CreateUser_Forbidden() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/customers");
        User user = new User(null, "John Tan Xiao Ming", "S1234567D", "93410569", "99 Bedok North Ave 4 #13-233 S460099", "johntxm", "johnpwsucks!1!", "ROLE_USER", true);

        ResponseEntity<Object> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                        .postForEntity(uri, user, Object.class);

        assertEquals(403, result.getStatusCode().value());
    }

    // Users: Manager can update user details (number, address, password)
    @Test
    void manager_UpdateUser_OK() throws Exception {
        Long id = userRepo.findByUsername("iamironman").get().getId();
        URI uri = new URI(baseUrl + port + "/api/customers/" + id);
        User updatedUser = new User(null, "Tony Stark", "S8732269I", "90405678", "Avengers Tower, New York", "iamironman", encoder.encode("justchangedpassw0rd"), "ROLE_USER", true);

        assertDoesNotThrow(() -> {
            restTemplate.withBasicAuth("manager_1", "01_manager_01").put(uri, updatedUser);
        });
    }

    // Users: User can update their own details
    @Test
    void user_UpdateOwnProfile_OK() throws Exception {
        Long id = userRepo.findByUsername("iamironman").get().getId();
        URI uri = new URI(baseUrl + port + "/api/customers/" + id);
        User updatedUser = new User(null, "Tony Stark", "S8732269I", "90405678", "Avengers Tower, New York", "iamironman", encoder.encode("justchangedpassw0rd"), "ROLE_USER", true);

        assertDoesNotThrow(() -> {
            restTemplate.withBasicAuth("iamironman", "i<3you3000").put(uri, updatedUser);
        });
    }

/* 
---------------------------------------------CONTENT------------------------------------------------
*/
    // Contents: User can view only approved content
    @Test
    void user_ViewContents_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/");
        ResponseEntity<Content[]> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                           .getForEntity(uri, Content[].class);
        Content[] contents = result.getBody();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, contents.length);
    }

    // Contents: Manager can view all content
    @Test
    void manager_ViewContents_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/");
        ResponseEntity<Content[]> result = restTemplate.withBasicAuth("manager_1", "01_manager_01")
                                           .getForEntity(uri, Content[].class);
        Content[] contents = result.getBody();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, contents.length);
    }

    // Contents: Analyst can create content, but approved status is always set to false
    @Test
    void analyst_CreateContent_Created() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/");
        Content newContent = new Content(null, "How to get rich QUICK", "Click the link for easy profit!!!", 
        "Just click the link!!", "Don't have la", true);
        ResponseEntity<Content> result = restTemplate.withBasicAuth("analyst_1", "01_analyst_01")
                                           .postForEntity(uri, newContent, Content.class);
        Content createdContent = result.getBody();

        assertEquals(201, result.getStatusCode().value());
        assertEquals(false, createdContent.getApproved());
    }

    // Contents: Manager can create content, approved status can be set to true
    @Test
    void manager_CreateContent_Created() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/");
        Content newContent = new Content(null, "KPMG Quarterly outlook Q2 2020", 
        "Financial analysis of market performance in Q2 2020 by KPMG", 
        "KPMG reports on major accounting and financial reporting developments that could affect companies in the current period and near term.", 
        "https://frv.kpmg.us/reference-library/2020/q2-2020-quarterly-outlook.html", true);
        ResponseEntity<Content> result = restTemplate.withBasicAuth("manager_1", "01_manager_01")
                                           .postForEntity(uri, newContent, Content.class);
        Content createdContent = result.getBody();

        assertEquals(201, result.getStatusCode().value());
        assertEquals(true, createdContent.getApproved());
    }
    // Contents: Analyst can delete content
    @Test
    void analyst_DeleteContent_Deleted() throws Exception {
        Long id = contentRepo.findByTitle("Title2").get().getId();
        URI uri = new URI(baseUrl + port + "/api/contents/" + id);
        
        assertDoesNotThrow(() -> {
            restTemplate.withBasicAuth("analyst_1", "01_analyst_01").delete(uri);
        });
    }

/* 
---------------------------------------------ACCOUNT------------------------------------------------
*/

    // Accounts: User can view their own accounts
    @Test
    void user_ViewOwnAccounts_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/accounts");

        ResponseEntity<Account[]> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                           .getForEntity(uri, Account[].class);

        assertEquals(200, result.getStatusCode().value());
    }

    // Accounts: Manager cannot view any accounts
    @Test
    void manager_ViewAccounts_Forbidden() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/accounts");

        ResponseEntity<Object> result = restTemplate.withBasicAuth("manager_1", "01_manager_01")
                                           .getForEntity(uri, Object.class);

        assertEquals(403, result.getStatusCode().value());
    }

    // Accounts: Manager can create accounts
    @Test
    void manager_CreateAccount_Created() throws Exception {
        Long id = userRepo.findByUsername("iamironman").get().getId();
        URI uri = new URI(baseUrl + port + "/api/accounts");
        Account newAccount = new Account(null, null, id, Double.valueOf(1000000), null);
        ResponseEntity<Account> result = restTemplate.withBasicAuth("manager_1", "01_manager_01")
                                           .postForEntity(uri, newAccount, Account.class);

        assertEquals(201, result.getStatusCode().value());
    }

    // Accounts: User can transfer funds from their account
    @Test
    void user_TransferFromOwnAccount_Created() throws Exception {
        User tstark = userRepo.findByUsername("iamironman").get();
        Account sender = accountRepo.save(new Account(null, tstark, tstark.getId(), 
        Double.valueOf(1000000), Double.valueOf(1000000)));

        User tholland = userRepo.findByUsername("spiderman").get();
        Account receiver = accountRepo.save(new Account(null, tholland, tholland.getId(), 
        Double.valueOf(10000), Double.valueOf(10000)));

        Transfer newTransfer = new Transfer(null, null, null, sender.getId(), receiver.getId(), 
        Double.valueOf(5000));

        URI uri = new URI(baseUrl + port + "/api/accounts/" + sender.getId() + "/transactions");
        ResponseEntity<Transfer> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                          .postForEntity(uri, newTransfer, Transfer.class);

        assertEquals(201, result.getStatusCode().value());
    }

    // Accounts: User cannot transfer funds from other user's accounts
    @Test
    void user_TransferFromOtherAccount_Forbidden() throws Exception {
        User tstark = userRepo.findByUsername("iamironman").get();
        Account receiver = accountRepo.save(new Account(null, tstark, tstark.getId(), 
        Double.valueOf(1000000), Double.valueOf(1000000)));

        User tholland = userRepo.findByUsername("spiderman").get();
        Account sender = accountRepo.save(new Account(null, tholland, tholland.getId(), 
        Double.valueOf(10000), Double.valueOf(10000)));

        Transfer newTransfer = new Transfer(null, null, null, sender.getId(), receiver.getId(), 
        Double.valueOf(5000));

        URI uri = new URI(baseUrl + port + "/api/accounts/" + sender.getId() + "/transactions");
        ResponseEntity<Transfer> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                          .postForEntity(uri, newTransfer, Transfer.class);

        assertEquals(403, result.getStatusCode().value());
    }

    // Accounts: User cannot transfer funds larger than account balance
    @Test
    void user_TransferLargerThanBalance_BadRequest() throws Exception {
        User tstark = userRepo.findByUsername("iamironman").get();
        Account receiver = accountRepo.save(new Account(null, tstark, tstark.getId(), 
        Double.valueOf(1000000), Double.valueOf(1000000)));

        User tholland = userRepo.findByUsername("spiderman").get();
        Account sender = accountRepo.save(new Account(null, tholland, tholland.getId(), 
        Double.valueOf(10000), Double.valueOf(10000)));

        Transfer newTransfer = new Transfer(null, null, null, sender.getId(), receiver.getId(), 
        Double.valueOf(20000));

        URI uri = new URI(baseUrl + port + "/api/accounts/" + sender.getId() + "/transactions");
        ResponseEntity<Object> result = restTemplate.withBasicAuth("spiderman", "mrstark,Idontfeels0good")
                                          .postForEntity(uri, newTransfer, Object.class);

        assertEquals(400, result.getStatusCode().value());
    }

/* 
---------------------------------------------TRADING------------------------------------------------
*/

    // Stocks: User can view stocks
    @Test
    void user_ViewStocks_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/stocks");

        ResponseEntity<Stock[]> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                        .getForEntity(uri, Stock[].class);
        Stock[] stocks = result.getBody();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(30, stocks.length);
    }
    // Stocks: Manager cannot view stocks
    @Test
    void manager_ViewStocks_Forbidden() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/stocks");

        ResponseEntity<Object> result = restTemplate.withBasicAuth("manager_1", "01_manager_01")
                                        .getForEntity(uri, Object.class);

        assertEquals(403, result.getStatusCode().value());
    }

    // Trades: User can view their trades
    @Test
    void user_ViewOwnTrades_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/trades");

        ResponseEntity<Trade[]> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                         .getForEntity(uri, Trade[].class);

        assertEquals(200, result.getStatusCode().value());
    }

    // Trades: User can post a trade
    @Test
    void user_CreateTrade_Created() throws Exception {
        User tstark = userRepo.findByUsername("iamironman").get();
        Account acc = accountRepo.save(new Account(null, tstark, tstark.getId(), 
        Double.valueOf(1000000), Double.valueOf(1000000)));
        Trade newTrade = new Trade();
        newTrade.setAction("buy");
        newTrade.setSymbol("A17U");
        newTrade.setQuantity(500);
        newTrade.setBid(3.23);
        newTrade.setAccount_id(acc.getId());
        newTrade.setCustomer_id(tstark.getId());
        URI uri = new URI(baseUrl + port + "/api/trades");

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                       .postForEntity(uri, newTrade, Trade.class);

        assertEquals(201, result.getStatusCode().value());
    }
    // Trades: User can cancel an open trade
    @Test
    void user_CancelOpenTrade_OK() throws Exception {
        User tstark = userRepo.findByUsername("iamironman").get();
        Account acc = accountRepo.save(new Account(null, tstark, tstark.getId(), 
        Double.valueOf(1000000), Double.valueOf(1000000)));
        Trade newTrade = new Trade();
        newTrade.setAction("buy");
        newTrade.setSymbol("A17U");
        newTrade.setQuantity(500);
        newTrade.setBid(3.23);
        newTrade.setAccount_id(acc.getId());
        newTrade.setCustomer_id(tstark.getId());
        URI uri = new URI(baseUrl + port + "/api/trades");

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                       .postForEntity(uri, newTrade, Trade.class);

        Trade savedTrade = response.getBody();
        savedTrade.setStatus("processed");

        assertDoesNotThrow(() -> {
            restTemplate.withBasicAuth("iamironman", "i<3you3000").put(uri, savedTrade);
        });
    }

    // Portfolio: User can view portfolio
    @Test
    void user_ViewOwnPortfolio_OK() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/portfolio");

        ResponseEntity<Object> result = restTemplate.withBasicAuth("iamironman", "i<3you3000")
                                        .getForEntity(uri, Object.class);

        assertEquals(200, result.getStatusCode().value());
    }
    
}
