package com.ticker.tracker.dao;

import com.ticker.tracker.entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerDAO extends JpaRepository<Ticker,Long> {
}
