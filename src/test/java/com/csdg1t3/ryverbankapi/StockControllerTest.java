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

import com.csdg1t3.ryverbankapi.trade.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {
    @Mock
    private StockService stockSvc;

    @InjectMocks
    private StockController stockController;

    private static final Stock stock1 = new Stock("A17U",3.23,20000, 3.19, 20000,3.24);
    private static final Stock stock2 = new Stock("A17U",1.65,20000, 1.51, 20000,1.81);


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
        Optional<Stock> found = Optional.of(stock1);
        when(stockSvc.getUpdatedStock(any(String.class))).thenReturn(found);

        Stock returned = stockController.getStock("A17U");
        assertEquals(stock1,returned);
        verify(stockSvc).getUpdatedStock("A17U");
    }
}
