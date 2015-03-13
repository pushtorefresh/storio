package com.pushtorefresh.storio.db.operation;

/**
 * MapFunc defines map operation for converting one data format into another
 *
 * @param <From> type of "what do you want to convert"
 * @param <To>   type of "what do you want to receive after convert"
 */
public interface MapFunc<From, To> {

    /**
     * Maps one object into another
     *
     * @param from object to convert
     * @return result of conversion
     */
    public To map(From from);
}
