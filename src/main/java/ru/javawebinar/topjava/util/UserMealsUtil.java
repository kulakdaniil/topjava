package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with correctly exceeded field
        //
        // http://bazhenov.me/blog/2014/07/20/java-8-top10.html
        // map.getOrDefault() & map.merge();

        //Превышает ли сумма калорий за день заданного пользователем значения?

        //Формируем справочную карту, суммируем калории за день
        Map<LocalDate,Integer> dayCaloryMap = new HashMap<>();
        for (UserMeal userMeal: mealList) {
            LocalDate localDate = userMeal.getDateTime().toLocalDate();
            dayCaloryMap.put(localDate, userMeal.getCalories() +
                            (dayCaloryMap.containsKey(localDate)?dayCaloryMap.get(localDate):0));
        }
//        System.out.println(dayCaloryMap);

        //Фильтруем и конвертируем объекты
        List<UserMealWithExceed> exceedList = new ArrayList<>();
        for (UserMeal userMeal: mealList) {
            LocalDate mealDate = userMeal.getDateTime().toLocalDate();
            if (TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(),startTime,endTime))
                exceedList.add(new UserMealWithExceed(userMeal.getDateTime(),
                                                      userMeal.getDescription(),
                                                      userMeal.getCalories(),
                                                      (dayCaloryMap.get(mealDate)>caloriesPerDay)?true:false));
        }
//        for (UserMealWithExceed userMealWithExceed:exceedList)
//            System.out.println(userMealWithExceed.toString());
        return exceedList;
    }
}