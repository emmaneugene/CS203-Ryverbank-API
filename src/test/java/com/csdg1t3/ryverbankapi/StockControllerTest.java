package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csdg1t3.ryverbankapi.trade.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {
    @Mock
    private StockService stockSvc;

    @Mock 
    private StockRepository stockRepo;

    @InjectMocks
    private StockController stockController;

    private final String s1_Symbol = "A17U";// insert stock symbol
    private final double s1_LastPrice = 3.28;// insert stock last price
    private final int s1_BidVolume = 20000; // insert buy's bid volume
    private final double s1_bid = 3.26; // insert bid's value
    private final int s1_AskVolume = 20000; // insert sell's ask volume
    private final double s1_ask = 3.29; // insert sell's ask price
    
    private final String s2_Symbol = "C61U";// insert stock symbol
    private final double s2_LastPrice = 1.65;// insert stock last price
    private final int s2_BidVolume = 30000; // insert buy's bid volume
    private final double s2_bid = 1.63; // insert bid's value
    private final int s2_AskVolume = 30000; // insert sell's ask volume
    private final double s2_ask = 1.66; // insert sell's ask price

    // Assert that method throws StockNotFoundException
    @Test
    void getStock_InvalidStock_ThrowStockNotFoundException() {
        // mock
        String invalidStock = "A17Y";
        Optional<Stock> notFound = Optional.empty();
        when(stockSvc.getUpdatedStock(any(String.class))).thenReturn(notFound);

        // assert
        assertThrows(StockNotFoundException.class, () -> stockController.getStock(invalidStock), "Could not find stock " + invalidStock);
        verify(stockSvc).getUpdatedStock(invalidStock);
    }

    // Assert that method returns found stock
    @Test
    void getStock_WithStock_returnFoundStock() {
        // mock
        Stock stock1 = new Stock(s1_Symbol, s1_LastPrice, s1_BidVolume, s1_bid, s1_AskVolume, s1_ask);
        Optional<Stock> found = Optional.of(stock1);
        when(stockSvc.getUpdatedStock(s1_Symbol)).thenReturn(found);

        // act
        Stock returned = stockController.getStock(s1_Symbol);

        // assert
        assertEquals(returned, stock1);
        verify(stockSvc).getUpdatedStock(s1_Symbol);
    }

    //Assert that method returns an empty List<Stock> if there are no public stocks
    @Test
    void getStocks_NoPublicStocks_returnEmptyList(){
        //mock
        List<Stock> stocks = new ArrayList<Stock>();
        when (stockSvc.getAllUpdatedStocks()).thenReturn(stocks);
    
        //act
        List<Stock> returnedList = stockController.getStocks();

        //assert
        assertEquals(stocks, returnedList);
        verify(stockSvc).getAllUpdatedStocks();
    }

        // Assert that method returns list of stocks
    @Test
    void getStocks_WithStocks_returnListOfStocks(){
        //mock
        Stock stock1 = new Stock(s1_Symbol, s1_LastPrice, s1_BidVolume, s1_bid, s1_AskVolume, s1_ask);
        Stock stock2 = new Stock(s2_Symbol, s2_LastPrice, s2_BidVolume, s2_bid, s2_AskVolume, s2_ask);
        List<Stock> stocks = new ArrayList<Stock>();
        stocks.add(stock1);
        stocks.add(stock2);
        when (stockSvc.getAllUpdatedStocks()).thenReturn(stocks);

        // act
        List<Stock> returned = stockSvc.getAllUpdatedStocks();
        
        // assert
        assertEquals(stocks, returned);
        verify(stockSvc).getAllUpdatedStocks();
    }

    @Test
    void getStocks_NoStocks_ReturnEmptyList(){
        List<Stock> stocks = new ArrayList<Stock>();

        when(stockSvc.getAllUpdatedStocks()).thenReturn(stocks);

        List<Stock> returned = stockController.getStocks();

        assertEquals(returned,stocks);
        verify(stockSvc).getAllUpdatedStocks();
    }

    @Test
    void getStocks_AllStocks_ReturnAllStocks(){
        Stock stock1 = new Stock(s1_Symbol, s1_LastPrice, s1_BidVolume, s1_bid, s1_AskVolume, s1_ask);
        Stock stock2 = new Stock(s2_Symbol, s2_LastPrice, s2_BidVolume, s2_bid, s2_AskVolume, s2_ask);
        List<Stock> stocks = new ArrayList<Stock>();
        stocks.add(stock1);
        stocks.add(stock2);

        when(stockSvc.getAllUpdatedStocks()).thenReturn(stocks);

        List<Stock> returned = stockController.getStocks();

        assertEquals(returned,stocks);
        verify(stockSvc).getAllUpdatedStocks();
    }

    @Test
    void getStock_NoStock_ThrowStockNotFoundException(){
        when(stockSvc.getUpdatedStock(any(String.class))).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () ->  stockController.getStock("XXXX"), "Could not find stock XXXX");
        verify(stockSvc).getUpdatedStock("XXXX");

    }

    @Test
    void getStock_FoundStock_ReturnStockWithUpdates(){
        Stock stock1 = new Stock(s1_Symbol, s1_LastPrice, s1_BidVolume, s1_bid, s1_AskVolume, s1_ask);
        Optional<Stock> found = Optional.of(stock1);
        when(stockSvc.getUpdatedStock(any(String.class))).thenReturn(found);

        Stock returned = stockController.getStock("A17U");
        assertEquals(stock1,returned);
        verify(stockSvc).getUpdatedStock("A17U");
    }
}
