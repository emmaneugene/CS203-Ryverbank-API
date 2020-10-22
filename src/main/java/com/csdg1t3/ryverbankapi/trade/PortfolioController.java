package com.csdg1t3.ryverbankapi.trade;

import java.util.*;

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

import com.csdg1t3.ryverbankapi.user.*;

import javax.validation.Valid;

/**
 * Controller that manages HTTP GET/POST/PUT/DELETE requests
 */
@RestController
public class PortfolioController {
    private AssetRepository assetRepo;
    private PortfolioRepository portfolioRepo;
    private UserRepository userRepo;
    private StockRepository stockRepo;

    public PortfolioController (PortfolioRepository portfolioRepo, AssetRepository assetRepo, 
    UserRepository userRepo, StockRepository stockRepo) {
        this.portfolioRepo = portfolioRepo;
        this.assetRepo = assetRepo;
        this.userRepo = userRepo;
        this.stockRepo = stockRepo;
    }

    /**
     * Returns the user's portfolio. Each time this method is called, we must update the current
     * price of all assets, and modify unrealised gain/loss as necessary
     * 
     * Only ROLE_USER can has the authority to perform this, as outlines in SecurityConfig
     * 
     * @return the user's portfolio
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/portfolio")
    public Portfolio getPortfolio() {
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        
        User user = userRepo.findByUsername(uDetails.getUsername()).get();
        Portfolio portfolio = portfolioRepo.findByCustomerId(user.getId()).get();

        List<Asset> assets = assetRepo.findByPortfolioCustomerId(portfolio.getCustomer_id());
        
        for (Asset asset : assets) {
            Stock stock = stockRepo.findBySymbol(asset.getCode()).get();
            asset.setCurrent_price(stock.getBid());
            assetRepo.save(asset);
        }
        portfolio.setAssets(assets);
        portfolio.setUnrealized_gain_loss();

        return portfolio;
    }
}