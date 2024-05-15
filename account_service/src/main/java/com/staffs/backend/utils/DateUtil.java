package com.staffs.backend.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtil {

    public static Date dateFormat(String date) {
        Date date1 = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            log.warn("Error while parsing date: %s".formatted(date));
        }
        return date1;
    }

    public static Date dateStringFormat(String date) {
        Date date1 = null;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            log.warn("Error while parsing date: {}", date);
        }
        return date1;
    }

    public static String dateToString(Date date) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        return format.format(date);
    }

    public static String dateToJoinedString(Date date) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");

        return format.format(date);
    }

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return localDateTime.format(format);
    }

    public static Date dateTimeFullFormat(String date) {
        Date date1 = null;
        String dateTime = date + " 00:00:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date1 = format.parse(dateTime);
        } catch (ParseException e) {
            log.warn(dateTime, "Error while parsing date: {}");
        }
        return date1;
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {

        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime stringToLocalDateTime(String date) {
        Date date1 = null;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            log.warn("Error while parsing date: %s".formatted(date));
        }

        assert date1 != null;
        return dateToLocalDateTime(date1);
    }

    public static Date todayDate() {
        Date todayDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = simpleDateFormat.format(todayDate);
        return dateTimeFullFormat(today);
    }

    public static int calculateAge(int year, int month, int day) {

        LocalDate dob = LocalDate.of(year, month, day);
        LocalDate curDate = LocalDate.now();
        //calculates the amount of time between two dates and returns the years
        return Period.between(dob, curDate).getYears();
    }

    public static boolean isSameMonth(Date date1, Date date2){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static Date todayDateInAnyYear(int yearDiff) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, yearDiff);
        Date anyYear = cal.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String yearDate = simpleDateFormat.format(anyYear);
        return dateTimeFullFormat(yearDate);
    }

    public static boolean dateFormatCheck(String format, String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean lessThan18(String dateOfBirth) {
        String[] dob = dateOfBirth.split("/");

        return calculateAge(Integer.parseInt(dob[2]), Integer.parseInt(dob[1]), Integer.parseInt(dob[0])) < 18;
    }

}
