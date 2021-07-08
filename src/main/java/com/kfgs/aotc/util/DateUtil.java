package com.kfgs.aotc.util;

import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.data.domain.Pageable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 获取当前时间
     */
    public static Date getNowDate(){
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取当前时间 - N个年
     */
    public static Date getNowDateMinusYear(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个年
     */
    public static Date getNowDateAddYear(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 - N个月
     */
    public static Date getNowDateMinusMonth(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个月
     */
    public static Date getNowDateAddMonth(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,+ n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 - N个周
     */
    public static Date getNowDateMinusWeek(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个周
     */
    public static Date getNowDateAddWeek(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 - N个天
     */
    public static Date getNowDateMinusDay(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个天
     */
    public static Date getNowDateAddDay(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 判断该日期是否是周一
     */
    public static boolean isFirstDayOfWeek(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        //java中，一周中的第一天是周日，但我们生活中一周中的第一天指的是周一
        return cal.get(Calendar.DAY_OF_WEEK) == 2;
    }

    public static Boolean isFirstDayOfWeek(Calendar cal){
        return cal.get(Calendar.DAY_OF_WEEK) == 2;
    }

    /**
     * 判断该日期是否是该月的第一天
     */
    public static boolean isFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
     * 判断该日期是否是该年的第一天
     */
    public static boolean isFirstDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR) == 1;
    }

    /**
     * 判断该日期是否是季度的第一天
     */
    public static boolean isFirstDayOfQuarter(Date date) {
        String mMdd = new SimpleDateFormat("MMdd").format(date);
        return "0101".equals(mMdd) || "0401".equals(mMdd) || "0701".equals(mMdd) || "1001".equals(mMdd);
    }

    /**
     * 获取上周一的日期
     */
    public static Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    /**
     * 获取本周一的日期
     */
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();

    }

    /**
     * 获取下周一的日期
     */
    public static Date getNextWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }


    public static void main(String[] args) {
        //LinkedList list= getWeekByLinkedList(getWeekLinkedList("2021-07-05", "2021-07-25"));
        /*List list = getBetweenDays("20210705", "20210725");
        System.out.println(list.toString());*/

    }

    /**
     * 合并每周一到周日为一个值，放入list
     * @param week 每周的一和每周日 的list
     * @return
     */
    private static LinkedList getWeekByLinkedList(LinkedList week) {
        LinkedList linkedList = new LinkedList();
        int j = 1;
        for (int i = 0; i < week.size(); i = i+2) {
            linkedList.add(week.get(i)+"~"+week.get( i + 1 ));
        }
        return linkedList;
    }

    /**
     * 获取开始日期，和结束日期中间周一和周日
     * @param startDateStr 开始日期
     * @param endDateStr 结束日期
     * @return
     */
    public static LinkedList getWeekLinkedList(String startDateStr,String endDateStr){
        LinkedList linkedList = new LinkedList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startdate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            Date thisWeekSundayOrMondy = getThisWeekSunday(startdate);
            if(null == thisWeekSundayOrMondy){
                return linkedList;
            }
            // 添加第一个周一
            linkedList.add(startDateStr);
            while(!thisWeekSundayOrMondy.equals(endDate)){
                linkedList.add(sdf.format(thisWeekSundayOrMondy));
                thisWeekSundayOrMondy = getDateByAddOne(thisWeekSundayOrMondy);
                linkedList.add(sdf.format(thisWeekSundayOrMondy));
            }
            //添加最后一个周日
            linkedList.add(sdf.format(thisWeekSundayOrMondy));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return linkedList;
    }

    /**
     * 获取当前日期 +1
     */
    public static Date getDateByAddOne(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR,1);
        return cal.getTime();
    }

    /**
     * 获取本周周日的日期
     */
    public static Date getThisWeekSunday(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //1.判断是否为周一
        if(isFirstDayOfWeek(cal)){
            cal.add(Calendar.DAY_OF_WEEK,6);
            return cal.getTime();
        }else{
            System.out.println("日期非周一");
            return null;
        }
    }

    /**
     * 获取两日期间的日期(N天)
     */
    public static List<String> getBetweenDays(String startDay, String endDay){
        //返回的日期集合
        List<String> days = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date start = dateFormat.parse(startDay);
            Date end = dateFormat.parse(endDay);
            Calendar tempStart = Calendar.getInstance();
            Calendar tempEnd  = Calendar.getInstance();
            tempStart.setTime(start);
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE,+1);
            while (tempStart.before(tempEnd)){
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR,1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

}
