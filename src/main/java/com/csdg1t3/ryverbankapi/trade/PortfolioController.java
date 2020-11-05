package com.csdg1t3.ryverbankapi.trade;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;

/**
 * Controller that manages HTTP requests to "/api/portfolio"
 */
@RestController
public class PortfolioController {
    private AssetRepository assetRepo;
    private PortfolioRepository portfolioRepo;
    private StockService stockSvc;
    private UserAuthenticator uAuth;

    public PortfolioController (PortfolioRepository portfolioRepo, AssetRepository assetRepo, 
    StockService stockSvc, UserAuthenticator uAuth) {
        this.portfolioRepo = portfolioRepo;
        this.assetRepo = assetRepo;
        this.stockSvc = stockSvc;
        this.uAuth = uAuth;
    }

    /**
     * Returns the user's portfolio. Each time this method is called, we must update the current
     * price of all assets, and modify unrealised gain/loss as necessary
     * 
     * Only ROLE_USER has the authority to perform this, as outlined in SecurityConfig
     * 
     * @return the user's portfolio
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/portfolio")
    public Portfolio getPortfolio() {
        User user = uAuth.getAuthenticatedUser();
        Portfolio portfolio = portfolioRepo.findByCustomerId(user.getId()).get();

        List<Asset> assets = portfolio.getAssets();
        
        for (Asset asset : assets) {
            Stock stock = stockSvc.getUpdatedStock(asset.getCode()).get();
            asset.setCurrent_price(stock.getBid());
            assetRepo.save(asset);
        }
        portfolio.setAssets(assets);
        portfolio.setUnrealized_gain_loss();

        return portfolio;
    }
}