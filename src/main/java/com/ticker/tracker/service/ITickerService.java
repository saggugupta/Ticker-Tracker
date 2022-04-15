package com.ticker.tracker.service;

import com.ticker.tracker.entity.Candle;
import com.ticker.tracker.entity.CandleDetails;

import java.io.IOException;
import java.util.List;

public interface ITickerService {
    public void getMarginData();

    public List<Candle> getCandles(CandleDetails candleDetails);
    public List<Candle> getCandleData(CandleDetails candleDetails);
}
