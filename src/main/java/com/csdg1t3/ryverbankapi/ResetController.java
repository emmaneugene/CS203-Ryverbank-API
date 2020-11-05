package com.csdg1t3.ryverbankapi;

import com.csdg1t3.ryverbankapi.content.ContentRepository;
import com.csdg1t3.ryverbankapi.trade.TradeRepository;
import com.csdg1t3.ryverbankapi.trade.StockRepository;
import com.csdg1t3.ryverbankapi.trade.PortfolioRepository;
import com.csdg1t3.ryverbankapi.user.UserRepository;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetController {
    @Autowired
    private ApplicationContext ctx;

    public ResetController() {}

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/api/reset")
    public void reset() {
        ctx.getBean(ContentRepository.class).deleteAll();
        ctx.getBean(TradeRepository.class).deleteAll();
        ctx.getBean(StockRepository.class).deleteAll();
        ctx.getBean(PortfolioRepository.class).deleteAll();
        ctx.getBean(UserRepository.class).deleteAll();
        RyverbankApiApplication.initApplicationData(ctx);
    }
}
