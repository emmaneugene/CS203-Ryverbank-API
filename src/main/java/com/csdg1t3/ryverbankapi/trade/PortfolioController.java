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

    public PortfolioController (PortfolioRepository portfolioRepo, AssetRepository assetRepo, UserRepository userRepo) {
        this.portfolioRepo = portfolioRepo;
        this.assetRepo = assetRepo;
        this.userRepo = userRepo;
    }

    /**
     * List the user's portfolio. Only ROLE_USER can access.
     * @return the user's portfolio
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/portfolio")
    public Portfolio getContent() {
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        
        User user = userRepo.findByUsername(uDetails.getUsername()).get();
        Optional<Portfolio> result = portfolioRepo.findByCustomerId(user.getId());
        if (!result.isPresent()) { 
            return new Portfolio();
        }

        Portfolio portfolio = result.get();
        List<Asset> assets = assetRepo.findByCustomerId(user.getId());
        if (!assets.isEmpty()) {
            return portfolio;
        }

        for (Asset a : assets) {
            a.setCurrentPrice();
        }
        portfolio.setAssets(assets);
        return portfolio;
    }
}