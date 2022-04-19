package com.ticker.tracker.service;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticker.tracker.dao.TickerDAO;
import com.ticker.tracker.entity.Candle;
import com.ticker.tracker.entity.CandleDetails;
import com.ticker.tracker.entity.Ticker;
import com.ticker.tracker.utility.TickerConstant;
import com.ticker.tracker.utility.TickerUtility;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class TickerServiceImpl implements ITickerService{
    Logger logger = LoggerFactory.getLogger(TickerServiceImpl.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    TickerDAO tickerDAO;

    @Value("${spring.smartapi.key}")
    private String apiKey;
    @Value("${angelbroking.api}")
    private String angelAPI;
    @Value("${angelbroking.candledata}")
    private String candleDataURI;
    @Value("${angelbroking.clientID}")
    private String clientID;
    @Value("${angelbroking.loginpassword}")
    private String loginPassword;


    @Override
    public void getMarginData() {
       Ticker[] tickerArr =  restTemplate.getForObject("http://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json", Ticker[].class);
       List<Ticker> existingTickers = tickerDAO.findAll();
       List<Ticker> updateTickerList = new ArrayList<>();
       List<Ticker> insertTickerList = new ArrayList<>();
       Map<String,Ticker> existingTickerMap = existingTickers.stream().collect(Collectors.toMap(x->x.getSymbol()+"_"+x.getName()+"_"+x.getToken(),y->y,(old,newVal)->{return newVal;}));

           for (Ticker tick : tickerArr) {
               if (existingTickerMap.containsKey(tick.getSymbol() + "_" + tick.getName() + "_" + tick.getToken())) {
                   Ticker tempTicker = existingTickerMap.get(tick.getSymbol() + "_" + tick.getName() + "_" + tick.getToken());
                   if (tempTicker != null && !tempTicker.equals(tick)) {
                       tempTicker.setToken(tick.getToken());
                       tempTicker.setSymbol(tick.getSymbol());
                       tempTicker.setName(tick.getName());
                       tempTicker.setExpiry(tick.getExpiry());
                       tempTicker.setStrike(tick.getStrike());
                       tempTicker.setLotSize(tick.getLotSize());
                       tempTicker.setInstrumentType(tick.getInstrumentType());
                       tempTicker.setExchangeSegment(tick.getExchangeSegment());
                       tempTicker.setTickSize(tick.getTickSize());
                       updateTickerList.add(tempTicker);
                   }
               } else {
                   insertTickerList.add(tick);
               }
           }
       tickerDAO.saveAll(updateTickerList);
       tickerDAO.saveAll(insertTickerList);
       //tickerDAO.saveAll(Arrays.asList(tickerArr));
       System.out.println("No of tickers:- " + tickerArr.length);
    }

    @Override
    public List<Candle> getCandles(CandleDetails candleDetails) {
        logger.info("inside service getCandles");
        List<Candle> candleList = new ArrayList<Candle>();
        Date currentDate = new Date();
        try {
            Date startDate = TickerUtility.getDateFromString(candleDetails.getFromdate());
            logger.info("start date :- " + startDate);
            Date endDate = TickerUtility.getDateFromString(candleDetails.getTodate());
            logger.info("end date :- " +endDate);
            if(TickerUtility.isSupportedInterval(candleDetails.getInterval())) {
                SmartConnect smartConnect = TickerUtility.getConnection(apiKey,clientID,loginPassword);
                int noOfDays = TickerUtility.getDaysCount(candleDetails.getInterval());
                Date tempDate = TickerUtility.performDateOperation(startDate, Calendar.DAY_OF_MONTH, noOfDays);
                logger.info("After noOfDays:- " + noOfDays + " date: " + tempDate);
                Date tempEndDate = TickerUtility.getBeforeDate(tempDate, endDate);
                if (TickerUtility.isFutureDate(tempEndDate)) {
                    tempEndDate = currentDate;
                    endDate = currentDate;
                }
                boolean flag = true;
                while (flag) {
                    candleDetails.setFromdate(TickerUtility.getFormattedDate(startDate));
                    candleDetails.setTodate(TickerUtility.getFormattedDate(tempEndDate));
                    logger.info("Candle token :- " + candleDetails.getSymboltoken() +" start date :- " + candleDetails.getFromdate() + " end date:- " + candleDetails.getTodate());

                    try {
                        Optional<Object> data = TickerUtility.getCandleData(smartConnect, candleDetails);
                        JSONArray outerArr = (JSONArray) data.get();

                        for (int j = 0; j < outerArr.length(); j++) {
                            JSONArray jsonArr = (JSONArray) outerArr.get(j);
                            Candle candle = new Candle();
                            String[] timeStamp = ((String) jsonArr.get(0)).split("T");
                            candle.setDate(timeStamp[0]);
                            candle.setTimestamp(timeStamp[1]);
                            candle.setDateTime(candle.getDate() + " " + candle.getTimestamp());
                            candle.setOpen((double) jsonArr.get(1));
                            candle.setHigh((double) jsonArr.get(2));
                            candle.setLow((double) jsonArr.get(3));
                            candle.setClose((double) jsonArr.get(4));
                            candleList.add(candle);
                        }
                    } catch (Exception ex) {
                         ex.printStackTrace();
                    }
                    if (tempEndDate.before(endDate)) {
                        startDate = TickerUtility.performDateOperation(tempEndDate, Calendar.DAY_OF_MONTH, 1);
                        tempDate = TickerUtility.performDateOperation(tempEndDate, Calendar.DAY_OF_MONTH, noOfDays);
                        tempEndDate = tempDate.before(endDate) ? tempDate : endDate;
                        if (TickerUtility.isFutureDate(tempEndDate)) {
                            tempEndDate = currentDate;
                            endDate = currentDate;
                        }
                    } else {
                        flag = false;
                    }
                }
            }else{
                candleList = getCandleData(candleDetails);
            }
            //System.out.println(candleList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return candleList;

    }

    @Override
    public List<Candle> getCandleData(CandleDetails candleDetails) {
        String oldInterval = candleDetails.getInterval();
        candleDetails.setInterval(TickerConstant.ONE_DAY);
        List<Candle> candleList = getCandles(candleDetails);
        Map<String,Candle> map = new LinkedHashMap<>();
        Function<String,String> fun = (x)->{
            switch(oldInterval){
                case TickerConstant.ONE_WEEK: return TickerUtility.getWeekOfYear(x)+"_"+TickerUtility.getYear(x);
                case TickerConstant.ONE_MONTH: return TickerUtility.getMonth(x)+"_"+TickerUtility.getYear(x);
                default : return "";
            }
        };
        for (Candle tempCandle: candleList) {
            String key = fun.apply(tempCandle.getDateTime());
            //System.out.println(" Key :- " + key);
            if(map.containsKey(key)){
                Candle existingCandle = map.get(key);
                //existingCandle.setOpen(Math.min(existingCandle.getOpen(),tempCandle.getOpen()));
                existingCandle.setClose(tempCandle.getClose());
                existingCandle.setLow(Math.min(existingCandle.getLow(),tempCandle.getLow()));
                existingCandle.setHigh(Math.max(existingCandle.getHigh(),tempCandle.getHigh()));
                map.put(key,existingCandle);
            }else{
                map.put(key,tempCandle);
            }
        }
        //System.out.println(map);
        List<Candle> resultList = new ArrayList<>();
        for(Map.Entry<String,Candle> entry : map.entrySet()){
            resultList.add(entry.getValue());
        }
        return resultList;
    }
}
