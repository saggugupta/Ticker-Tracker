package com.ticker.tracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"symbol","token" })
})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticker ticker = (Ticker) o;
        boolean result = Float.compare(ticker.strike, strike) == 0 && lotSize == ticker.lotSize && Float.compare(ticker.tickSize, tickSize) == 0 && Objects.equals(token, ticker.token) && Objects.equals(symbol, ticker.symbol) && Objects.equals(name, ticker.name) && Objects.equals(expiry, ticker.expiry) && Objects.equals(instrumentType, ticker.instrumentType) && Objects.equals(exchangeSegment, ticker.exchangeSegment);
        if(result){
            System.out.println();
        }else{
            System.out.println(ticker.toString());
            System.out.println(this.toString());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, symbol, name, expiry, strike, lotSize, instrumentType, exchangeSegment, tickSize);
    }
}
