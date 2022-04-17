package com.ticker.tracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
}
