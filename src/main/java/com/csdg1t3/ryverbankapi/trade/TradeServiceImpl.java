package com.csdg1t3.ryverbankapi.trade;

import org.springframework.stereotype.Service;

import java.util.*;

import com.csdg1t3.ryverbankapi.account.*;

@Service
public class TradeServiceImpl implements TradeService {
    private TradeRepository tradeRepo;
    private StockRepository stockRepo;
    private AccountRepository accountRepo;
    private PortfolioRepository portfolioRepo;
    private AssetRepository assetRepo;

    public TradeServiceImpl(TradeRepository tradeRepo, StockRepository stockRepo, 
    AccountRepository accountRepo, PortfolioRepository portfolioRepo, AssetRepository assetRepo) {
        this.tradeRepo = tradeRepo;
        this.stockRepo = stockRepo;
        this.accountRepo = accountRepo;
        this.portfolioRepo = portfolioRepo;
        this.assetRepo = assetRepo;
    }

    public void updateTradeExpiry() {
        
    }

    public void processExpiredTrade() {

    }

    public List<Trade> listValidBuyTrades() {
        return null;
    }

    public List<Trade> listValidSellTrades() {
        return null;
    }

    public Trade getLowestAskTradeForStock(String symbol) {
        return null;
    }

    public Trade getHighestBidTradeForStock(String symbol) {
        return null;
    }

    public void processTrade(Trade trade) {
        
    }

    public void processBuy(Trade trade) {
        
    }

    public void processSell(Trade trade) {
        
    }

    public void processMarketBuy(Trade trade) {

    }

    public void processMarketSell(Trade trade) {

    }

    /**
     * Cancels a trade. If the trade is a buy, update account available balance
     */
    public void cancelTrade(Long id) {
        Trade trade = tradeRepo.findById(id).get();

        if (trade.getAction().equals("buy")) {
            Account acc = accountRepo.findById(trade.getAccount_id()).get();
            acc.setAvailableBalance(
                acc.getAvailableBalance() + (trade.getBid() * trade.getQuantity())
            );
        }

        trade.setStatus("cancelled");
    }
}