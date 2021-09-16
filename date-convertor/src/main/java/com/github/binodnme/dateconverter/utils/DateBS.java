package com.github.binodnme.dateconverter.utils;

import com.github.binodnme.dateconverter.converter.DateConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * Created by binodnme
 * Created on 4/9/16
 */
public class DateBS implements Comparable<DateBS> {
  private int year;
  private int month;
  private int day;
  private int hourOfDay = 0;
  private int minute = 0;
  private int seconds = 0;

  /**
   * constructs {@link DateBS} with current date and time
   */
  public DateBS(){
    LocalDateTime localDateTime = LocalDateTime.now(ZoneId.systemDefault());

    Calendar calendar = Calendar.getInstance();
    calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth());
    DateBS dateBS = DateConverter.convertADToBS(calendar.getTime());

    if(dateBS != null){
      this.year = dateBS.getYear();
      this.month = dateBS.getMonth();
      this.day = dateBS.getDay();
      this.hourOfDay = localDateTime.getHour();
      this.minute = localDateTime.getMinute();
      this.seconds = localDateTime.getSecond();
    }
  }

  /** {@link DateBS} with given year, month and day
   * index of month starts from 0 i.e index of BAISAKH is 0
   * constructs
   * @param year year
   * @param month month
   * @param day day
     */
  public DateBS(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getDay() {
    return day;
  }

  public int getHourOfDay() {
    return hourOfDay;
  }

  public int getMinute() {
    return minute;
  }

  public int getSeconds() {
    return seconds;
  }

  public int getHour(){
    return this.hourOfDay > 12 ? this.hourOfDay % 12 : this.hourOfDay;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DateBS)) {
      return false;
    }
    DateBS date = (DateBS) obj;
    return this.year == date.year && this.month == date.month && this.day == date.day;
  }

  @Override
  public String toString() {
    return "DateBS{" +
            "year=" + year +
            ", month=" + month +
            ", day=" + day +
            ", hourOfDay=" + hourOfDay +
            ", minute=" + minute +
            ", seconds=" + seconds +
            '}';
  }

  @Override
  public int compareTo(DateBS dateBS) {
    if (this.year > dateBS.getYear()) {
      return 1;
    } else if (this.year < dateBS.getYear()) {
      return -1;
    } else if (this.month > dateBS.getMonth()) {
      return 1;
    } else if (this.month < dateBS.getMonth()) {
      return -1;
    } else if (this.day > dateBS.getDay()) {
      return 1;
    } else if (this.day < dateBS.getDay()) {
      return -1;
    } else if(this.hourOfDay > dateBS.getHourOfDay()) {
      return 1;
    } else if(this.hourOfDay < dateBS.getHourOfDay()){
      return -1;
    } else if(this.minute > dateBS.getMinute()){
      return 1;
    } else if(this.minute < dateBS.getMinute()){
      return -1;
    } else if(this.seconds > dateBS.getSeconds()){
      return 1;
    } else if(this.seconds < dateBS.getSeconds()){
      return -1;
    } else {
      return 0;
    }
  }
}
