package com.ticker.tracker.utility;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.models.User;
import com.ticker.tracker.entity.CandleDetails;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class TickerUtility {
    private static SmartConnect connection = null;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static Calendar cal = Calendar.getInstance();
    public static synchronized SmartConnect getConnection(String apiKey,String clientID, String loginPassword){
        if(connection==null){
            SmartConnect smartConnect = new SmartConnect(apiKey);
            User user = smartConnect.generateSession(clientID, loginPassword);
            smartConnect.setAccessToken(user.getAccessToken());
            smartConnect.setUserId(user.getUserId());
            connection = smartConnect;
        }
        return connection;
    }

    public static synchronized Date getDateFromString(String str){
        Date date = null;
        try{
            date = formatter.parse(str);
        }catch(Exception e){
            System.out.println("Unable to parse the date :- " + str + " msg:- " + e.getMessage());
        }
        return date;
    }

    public static synchronized  Date performDateOperation(Date date, int field,int amount){
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }

    // method returns day count on the basis of request type.
    //for eg for one minute candle provider support max 30 days only
    public static int getDaysCount(String typeOfRequest){
        switch(typeOfRequest){
            case TickerConstant.ONE_MINUTE: return 30;
            case TickerConstant.THREE_MINUTE: return 90;
            case TickerConstant.FIVE_MINUTE: return 90;
            case TickerConstant.TEN_MINUTE: return 90;
            case TickerConstant.FIFTEEN_MINUTE: return 180;
            case TickerConstant.THIRTY_MINUTE: return 180;
            case TickerConstant.ONE_HOUR: return 365;
            case TickerConstant.ONE_DAY: return 2000;
            default: return 1;
        }
    }

    public static synchronized Date getBeforeDate(Date firstDate, Date secondDate){
        try {
            if (firstDate.before(secondDate)) {
                return firstDate;
            } else {
                return secondDate;
            }
        }catch(Exception ex){
            System.out.println("Either one of the date is null. firstdate:- "+ firstDate+" secondDate:-"+secondDate +" msg:- "+ ex.getMessage());
        }
        return null;
    }

    public static synchronized  boolean isFutureDate(Date date){
        Date currentDate = new Date();
        boolean isFutureDate=false;
        try {
            if (date.after(currentDate)) {
                isFutureDate = true;
            }
        }catch(Exception ex){
            System.out.println("date is not in proper format :- "+ date + " msg:- " + ex.getMessage());
        }
        return isFutureDate;
    }

    public static synchronized String getFormattedDate(Date date){
        return formatter.format(date);
    }

    public static synchronized Optional<Object> getCandleData(SmartConnect smartConnect, CandleDetails candleDetails){
        JSONObject jsonObj = new JSONObject(candleDetails);
        Optional<Object> obj = null;
        try {
            JSONObject response = smartConnect.candleData(jsonObj);
            System.out.println(response);
            obj = Optional.of(response.get("data"));
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return obj;
    }

    public static boolean isSupportedInterval(String interval){
        switch(interval){
            case TickerConstant.ONE_WEEK:
            case TickerConstant.ONE_MONTH:
                return false;
            default: return true;
        }
    }

    public static synchronized int getWeekOfYear(String date){
        cal.setTime(getDateFromString(date));
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static synchronized int getYear(String date){
        cal.setTime(getDateFromString(date));
        return cal.get(Calendar.YEAR);
    }

    public static synchronized int getMonth(String date){
        cal.setTime(getDateFromString(date));
        return cal.get(Calendar.MONTH);
    }
}
