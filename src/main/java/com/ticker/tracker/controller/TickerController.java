package com.ticker.tracker.controller;

import com.ticker.tracker.entity.Candle;
import com.ticker.tracker.entity.CandleDetails;
import com.ticker.tracker.service.ITickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class TickerController {

    @Autowired
    public ITickerService tickerService;

    @GetMapping("stocks")
    public String getStockList(){
        String result = "success";
        tickerService.getMarginData();
        return result;
    }

    @PostMapping("candle-data")
    public List<Candle> getStockDetailsTimeFrame(@RequestBody CandleDetails details){
        //String result = "success";
        return tickerService.getCandles(details);
    }

}
