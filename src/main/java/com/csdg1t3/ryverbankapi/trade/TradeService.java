package com.csdg1t3.ryverbankapi.trade;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

import com.csdg1t3.ryverbankapi.account.*;

@Service
public class TradeService {
    private TradeRepository tradeRepo;
    private AccountRepository accountRepo;
    private AccountController accountController;
    private AssetRepository assetRepo;
    private PortfolioRepository portfolioRepo;
    
    private static final List<String> VALID_STATUSES = Arrays.asList("open", "partial-filled");

    public TradeService(TradeRepository tradeRepo, AccountRepository accountRepo, 
    AccountController accountController, AssetRepository assetRepo, PortfolioRepository portfolioRepo) {
        this.tradeRepo = tradeRepo;
        this.accountRepo = accountRepo;
        this.accountController = accountController;
        this.assetRepo = assetRepo;
        this.portfolioRepo = portfolioRepo;
    }

    public void processExpiredTrade(Trade trade) {
        if (trade.getAction().equals("buy")) {
            Account acc = accountRepo.findById(trade.getAccount_id()).get();
            acc.setAvailable_balance(acc.getBalance());
            accountRepo.save(acc);
        } else if (trade.getAction().equals("sell")) {
            Asset asset = assetRepo.findByPortfolioCustomerIdAndCode(
                trade.getCustomer_id(), trade.getSymbol()).get();
            asset.setAvailable_quantity(asset.getAvailable_quantity() + trade.getQuantity() 
            - trade.getFilled_quantity());
            assetRepo.save(asset);
        }

        trade.setStatus("expired");
        tradeRepo.save(trade);
    }

    /**
     * Retrieves all open or partial-filled buy trades
     * 
     * @param symbol The symbol of the stock which buy trades are to be retrieved.
     * @return a list of open or partial-filled buy trades
     */
    public List<Trade> listValidBuyTradesForStock(String symbol) {
        return tradeRepo.findByActionAndSymbolAndStatusIn("buy", symbol, VALID_STATUSES);
    }

    /**
     * Retrieves all open or partial-filled sell trades
     * 
     * @param symbol The symbol of the stock which sell trades are to be retrieved.
     * @return a list of open or partial-filled sell trades
     */
    public List<Trade> listValidSellTradesForStock(String symbol) {
        return tradeRepo.findByActionAndSymbolAndStatusIn("sell", symbol, VALID_STATUSES);
    }

    /**
     * Retrieves an open or partial-filled sell trade with the lowest ask price.
     * - If multiple trades are found, returns the trade which was placed the earliest.
     * - If no trades are found, return null
     * 
     * @param symbol The symbol of the stock which trade is to be retrieved.
     * @return The trade with lowest ask price.
     */
    public Trade getLowestAskTradeForStock(String symbol) {
        List<Trade> validTrades = listValidSellTradesForStock(symbol);
        
        if (validTrades == null || validTrades.isEmpty()) {
            return null;
        }
        
        Trade lowest = validTrades.get(0);
        for (Trade trade : validTrades) {
            if (trade.getAsk() < lowest.getAsk() ||
                trade.getAsk() == lowest.getAsk() && trade.getDate() < lowest.getDate()) 
                lowest = trade;
        }

        if (lowest.getAsk() == 0)
            return null;

        return lowest;
    }

    /**
     * Retrieves an open or partial-filled buy trade with the highest bid price.
     * - If multiple trades are found, returns the trade which was placed the earliest
     * - If no trades are found, return null
     * 
     * @param symbol The symbol of the stock which trade is to be retrieved.
     * @return The trade with highest bid price.
     */
    public Trade getHighestBidTradeForStock(String symbol) {
        List<Trade> validTrades = listValidBuyTradesForStock(symbol);
        
        if (validTrades == null || validTrades.isEmpty()) {
            return null;
        }
        
        Trade highest = validTrades.get(0);
        for (Trade trade : validTrades) {
            if (trade.getBid() > highest.getBid() ||
                trade.getBid() == highest.getBid() && trade.getDate() < highest.getDate()) 
                highest = trade;
        }

        if (highest.getBid() == 0)
            return null;

        return highest;
    }

    /**
     * Returns the earliest valid market buy for a given stock
     * 
     * @param symbol The symbol of the stock which buy trade is to be retrieved.
     * @return The earliest market buy.
     */
    public Trade getEarliestMarketBuyForStock(String symbol) {
        List<Trade> trades = tradeRepo.findByActionAndSymbolAndBidAndStatusIn("buy", symbol, 
        Double.valueOf(0), VALID_STATUSES);

        if (trades == null || trades.isEmpty())
            return null;
        
        Trade earliest = trades.get(0);
        for (Trade trade : trades) {
            if (trade.getDate() < earliest.getDate()) 
                earliest = trade;
        }
        
        return earliest;
    }

    /**
     * Returns the earliest valid market sell for a given stock
     * 
     * @param symbol The symbol of the stock which sell trade is to be retrieved.
     * @return The earliest market sell.
     */
    public Trade getEarliestMarketSellForStock(String symbol) {
        List<Trade> trades = tradeRepo.findByActionAndSymbolAndAskAndStatusIn("sell", symbol, 
        Double.valueOf(0), VALID_STATUSES);

        if (trades == null || trades.isEmpty())
            return null;
        
        Trade earliest = trades.get(0);
        for (Trade trade : trades) {
            if (trade.getDate() < earliest.getDate())
                earliest = trade;
        }

        return earliest;
    }

    /**
     * Method that intiates the processing of a newly created or unprocessed trade. Depending on whether the trade
     * is a buy or sell, and whether it is made at market price, the method will call different 
     * processing functions.
     * 
     * If the current time is before 9am or after 5pm, the trade will not be processed
     * 
     * @param trade The trade to be made.
     * @return The processed trade.
     */
    public Trade makeTrade(Trade trade) {
        // updateTradeExpiry();
        Calendar now = Calendar.getInstance();
        
        if (now.get(now.HOUR_OF_DAY) < 9 || now.get(now.HOUR_OF_DAY) > 17) {
            trade.setProcessed(false);
            tradeRepo.save(trade);
            return trade;
        }

        
        if (trade.getAction().equals("buy")) {
            if (trade.getBid() == 0)
                processMarketBuy(trade);
            else 
                processBuy(trade);
        } else if (trade.getAction().equals("sell")) {
            if (trade.getAsk() == 0)
                processMarketSell(trade);
            else 
                processSell(trade);
        }
        trade.setProcessed(true);
        return trade;
    }

    /**
     * Processes a buy trade. The method tries to match with the lowest ask available, until the 
     * quantity is filled, there are no more sell trades, or the lowest ask exceeds the bid. 
     * It then matches with market
     * sell trades, if any.
     * 
     * @param buy The buy trade to be made.
     */
    public void processBuy(Trade buy) {
        Trade sell = getLowestAskTradeForStock(buy.getSymbol());
        while (!buy.isFilled() && sell != null && sell.getAsk() <= buy.getBid()) {

            int needed = buy.getRemaining_quantity();
            int avail = sell.getRemaining_quantity();
            int toFill = Math.min(needed, avail);
            
            fillTrades(buy, sell, sell.getAsk(), toFill);

            if (sell.isFilled())
                sell = getLowestAskTradeForStock(buy.getSymbol());
        }

        Trade marketSell = getEarliestMarketSellForStock(buy.getSymbol());
        while (!buy.isFilled() && marketSell != null) {
            int needed = buy.getRemaining_quantity();
            int avail = marketSell.getRemaining_quantity();
            int toFill = Math.min(needed, avail);
            
            fillTrades(buy, marketSell, buy.getBid(), toFill);

            if (marketSell.isFilled())
                marketSell = getEarliestMarketSellForStock(buy.getSymbol());
        }
    }

    /**
     * Processes a sell trade. The method tries to match with the highest bid available, until the
     * quantity is filled, there are no more buy trades, or the highest bid is lesser than the ask. 
     * It then matches with market buy trades, if any.
     * 
     * When matching with market buy trades, it is also crucial to ensure that the quantity does not
     * exceed what can the account can afford. We will thus take the minimum of the quantity to fill
     * and the quantity that be afforded by that account
     * 
     * @param sell The sell trade to be made.
     */
    public void processSell(Trade sell) {
        Trade buy = getHighestBidTradeForStock(sell.getSymbol());
        while (!sell.isFilled() && buy != null && buy.getBid() >= sell.getAsk()) {

            int needed = sell.getRemaining_quantity();
            int avail = buy.getRemaining_quantity();
            int toFill = Math.min(needed, avail);

            
            fillTrades(buy, sell, sell.getAsk(), toFill);

            if (buy.isFilled())
                buy = getLowestAskTradeForStock(sell.getSymbol());
        }

        Trade marketBuy = getEarliestMarketBuyForStock(sell.getSymbol());
        while (!sell.isFilled() && marketBuy != null) {
            int needed = sell.getRemaining_quantity();

            int qty_affordable = (int)Math.round(
                marketBuy.getAccount().getAvailable_balance() / (sell.getAsk() * 100)
                ) * 100;
            int avail = Math.min(qty_affordable, marketBuy.getRemaining_quantity());
            int toFill =  Math.min(needed, avail);

            fillTrades(marketBuy, sell, sell.getAsk(), toFill);

            if (marketBuy.isFilled())
                marketBuy = getEarliestMarketBuyForStock(sell.getSymbol());
        }
    }  
    
    /**
     * Processes a market buy trade. The method matches with the lowest bid sell trades until the 
     * trade is filled, there are no more sell trades, or there are insufficient funds for any 
     * further purchases (in units of 100 stocks)
     * 
     * @param buy The buy trade to be made.
     */
    public void processMarketBuy(Trade buy) {
        Account acc = buy.getAccount();
        Trade sell = getLowestAskTradeForStock(buy.getSymbol());
        while (!buy.isFilled() && sell != null && acc.getAvailable_balance() >= sell.getAsk() * 100) {
            int needed = buy.getRemaining_quantity();
            int avail = sell.getRemaining_quantity();
            int toFill = needed <= avail ? needed : avail;

            if (acc.getAvailable_balance() < sell.getAsk() * toFill)
                toFill = (int)Math.floor(acc.getAvailable_balance() / (sell.getAsk() * 100)) * 100;

            fillTrades(buy, sell, sell.getAsk(), toFill);

            if (sell.isFilled())
                sell = getLowestAskTradeForStock(buy.getSymbol());
        }
    }

    /**
     * Processes a market buy trade. The method matches with the lowest bid sell trades until the 
     * trade is filled, there are no more sell trades, or there are insufficient funds for any 
     * further purchases (in units of 100 stocks)
     * 
     * @param sell The sell trade to be made.
     */
    public void processMarketSell(Trade sell) {
        Trade buy = getHighestBidTradeForStock(sell.getSymbol());
        while (!sell.isFilled() && buy != null) {
            int needed = sell.getRemaining_quantity();
            int avail = buy.getRemaining_quantity();
            int toFill = needed <= avail ? needed : avail;

            fillTrades(buy, sell, buy.getBid(), toFill);

            if (buy.isFilled())
                buy = getHighestBidTradeForStock(sell.getSymbol());
        }
    }

    /**
     * Fills a buy-sell trade pair according to the price and quantity specified. If the buy and 
     * sell trades are posted by the same customer, however, the function does not fill them,
     * and returns immediately
     * 
     * 1. Buyer makes an account transfer to the seller.
     * - Balance is updated for buyer (also update available balance if it's a market buy),
     * - Balance and available balance are updated for the seller
     * 
     * 2. Seller's assets are deducted, assets transferred to buyer
     * - If buyer does not have assets of that stock, a new asset is created
     * 
     * 3. Update filled_quantity for each of the trades, and set status as needed 
     * (partial-filled or filled)
     * 

     * 
     * @param buy The buy trade to be filled.
     * @param sell The sell trade to be filled.
     * @param price The price of the trade.
     * @param qty The quantity of stocks to be traded.
     */
    public void fillTrades(Trade buy, Trade sell, Double price, int qty) {
        if (buy.getCustomer_id() == sell.getCustomer_id())
            return;

        Transfer transfer = new Transfer();
        transfer.setSender(buy.getAccount());
        transfer.setReceiver(sell.getAccount());
        transfer.setFrom(buy.getAccount_id());
        transfer.setTo(sell.getAccount_id());
        transfer.setAmount(price * qty);

        accountController.createTradeTransfer(transfer, buy.getAccount(), sell.getAccount());

        Optional<Portfolio> sellerPortfolioOpt = portfolioRepo.findByCustomerId(sell.getCustomer_id());
        Optional<Portfolio> buyerPortfolioOpt = portfolioRepo.findByCustomerId(buy.getCustomer_id());

        if (sellerPortfolioOpt.isPresent()) {
            updatePortfolioAsset(sellerPortfolioOpt.get(), sell.getSymbol(),
            price, -qty);
        }

        if (buyerPortfolioOpt.isPresent()) {
            updatePortfolioAsset(buyerPortfolioOpt.get(), buy.getSymbol(), 
            price, qty);
        }

        buy.setAvg_price(
            (buy.getAvg_price() * buy.getFilled_quantity() + price * qty) / (buy.getFilled_quantity() + qty));
        buy.setFilled_quantity(buy.getFilled_quantity() + qty);
        String buyStatus = buy.isFilled() ? "filled" : "partial-filled";
        buy.setStatus(buyStatus);
        tradeRepo.save(buy);

        sell.setAvg_price(
            (sell.getAvg_price() * sell.getFilled_quantity() + price * qty) / (sell.getFilled_quantity() + qty));
        sell.setFilled_quantity(sell.getFilled_quantity() + qty);
        String sellStatus = sell.isFilled() ? "filled" : "partial-filled";
        sell.setStatus(sellStatus);
        tradeRepo.save(sell);
    }

    /**
     * Updates a portfolio asset according to stock symbol, price and quantity
     * 
     * - If the quantity is negative, the asset has just been sold. Update total gain/loss of the 
     * portfolio and only modify quantity (available quantity was already modified when the trade
     * was placed)
     * 
     * - If the quantity is positive, the asset has just been bought. Update the average price of 
     * the asset, available quantity as well as quantity.
     * 
     * - If no asset with the desired stock symbol is found, we create a new asset for that 
     * portfolio. We can be sure that this is a bought asset, since no user would be able to sell
     * a stock that they did not own.
     * 
     * @param portfolio The portfolio to be updated.
     */
    public void updatePortfolioAsset(Portfolio portfolio, String symbol, Double price, int qty) {
        List<Asset> assets = portfolio.getAssets();

        boolean assetFound = false;
        Asset toUpdate = null;
        for (Asset asset : assets) {
            if (asset.getCode().equals(symbol)) {
                assetFound = true;
                toUpdate = asset;
            }
        }

        if (assetFound) {
            if (qty < 0) {
                Double change = (price - toUpdate.getAvg_price()) * -qty;
                portfolio.setRealized_gain_loss(portfolio.getRealized_gain_loss() + change);
            } else if (qty > 0) {
                toUpdate.setAvg_price(
                    (toUpdate.getAvg_price() * toUpdate.getQuantity() + price * qty) / (toUpdate.getQuantity() + qty));
                toUpdate.setAvailable_quantity(toUpdate.getAvailable_quantity() + qty);
            }
            toUpdate.setQuantity(toUpdate.getQuantity() + qty);
            
        } else {
            Asset newAsset = new Asset();
            newAsset.setCode(symbol);
            newAsset.setPortfolio(portfolio);
            newAsset.setQuantity(qty);
            newAsset.setAvailable_quantity(qty);
            newAsset.setAvg_price(price);
            
            assetRepo.save(newAsset);
        }
    }

    /**
     * Cancels a trade. Operations performed are largely similar to the process of trade expiry.
     * 
     * @param trade The trade to be cancelled.
     */
    public void processCancelTrade(Trade trade) {
        if (trade.getAction().equals("buy")) {
            Account acc = accountRepo.findById(trade.getAccount_id()).get();
            acc.setAvailable_balance(acc.getBalance());
            accountRepo.save(acc);
        } else if (trade.getAction().equals("sell")) {
            Asset asset = assetRepo.findByPortfolioCustomerIdAndCode(
                trade.getCustomer_id(), trade.getSymbol()).get();
            asset.setAvailable_quantity(asset.getAvailable_quantity() + trade.getQuantity());
            assetRepo.save(asset);
        }

        trade.setStatus("cancelled");
        tradeRepo.save(trade);
    }

    /**
     * Scheduler method that runs at 9am daily. The method retrieves all trades that have not 
     * been processed yet. These are trades that have been placed after 5pm on the previous day
     * or before 9am on the current day. These trades will enter the system and be matched from
     * earliest to latest
     */
    @Scheduled(cron = "0 0 9 ? * *")
	public void processUnprocessedTrades() {
        List<Trade> unprocessedTrades = tradeRepo.findByProcessed(false);
        
        unprocessedTrades.sort(new TradeTimeComparator());
        for (Trade trade : unprocessedTrades) 
            makeTrade(trade);
    }

    /**
     * Schedule method that runs at 5pm daily. The method retrieves all trades that are either open
     * or partial-filled, and expires them.
     */
    @Scheduled(cron = "0 0 17 ? * *")
    public void expireTrades() {
        List<Trade> toExpire = tradeRepo.findByStatusIn(VALID_STATUSES);

        for (Trade trade : toExpire)
            processExpiredTrade(trade);
    }
    
    @Scheduled(fixedRate = 300000)
    public void checkMarketBuyTrades() {
        
    }
}