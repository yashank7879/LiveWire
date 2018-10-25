package com.livewire.model;

/**
 * Created by mindiii on 10/9/18.
 */

public class WeekListModel {
    private String weekDays;
    private boolean isWeekDay;

    public WeekListModel(String week, boolean isweek) {
        this.weekDays = week;
        this.isWeekDay = isweek;

    }


    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public boolean isWeekDay() {
        return isWeekDay;
    }

    public void setisWeekDay(boolean weekDay) {
        isWeekDay = weekDay;
    }

    @Override
    public String toString() {
        return weekDays;
    }
}
