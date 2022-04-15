package com.ticker.tracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String token;
    String symbol;
    String name;
    String expiry;
    float strike;
    @JsonProperty("lotsize")
    int lotSize;
    @JsonProperty("instrumenttype")
    String instrumentType;
    @JsonProperty("exch_seg")
    String exchangeSegment;
    @JsonProperty("tick_size")
    float tickSize;
}
