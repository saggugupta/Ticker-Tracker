package com.ticker.tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candle {
    private String timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private String date;
    private String dateTime;
}
