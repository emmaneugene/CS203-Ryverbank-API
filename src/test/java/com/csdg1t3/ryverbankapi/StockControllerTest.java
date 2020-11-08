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

    private static final Stock stock1 = new Stock("A17U",3.23,20000, 3.19, 20000,3.24);
    private static final Stock stock2 = new Stock("A17U",1.65,20000, 1.51, 20000,1.81);

    // Assert that method throws StockNotFoundException
    @Test
    void getStock_InvalidStockSymbol_ThrowStockNotFoundException() {
        // mock
        when(stockSvc.getUpdatedStock(any(String.class))).thenReturn(Optional.empty());

        // assert
        assertThrows(StockNotFoundException.class, () -> stockController.getStock("XXXX"), "Could not find stock XXXX");
        verify(stockSvc).getUpdatedStock("XXXX");
    }

    // Assert that method returns found stock
    @Test
    void getStock_WithStock_returnFoundStock() {
        // mock
        Optional<Stock> found = Optional.of(stock1);
        when(stockSvc.getUpdatedStock(any(String.class))).thenReturn(found);

        // act
        Stock returned = stockController.getStock("A17U");

        // assert
        assertEquals(stock1,returned);
        verify(stockSvc).getUpdatedStock("A17U");
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
        List<Stock> stocks = new ArrayList<Stock>();
        stocks.add(stock1);
        stocks.add(stock2);
        when (stockSvc.getAllUpdatedStocks()).thenReturn(stocks);

        // act
        List<Stock> returned = stockController.getStocks();
        
        // assert
        assertEquals(stocks, returned);
        verify(stockSvc).getAllUpdatedStocks();
    }

}
