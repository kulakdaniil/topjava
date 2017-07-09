package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Predicate<UserMeal> pFilterStartEndTime = e -> TimeUtil.isBetween(e.getDateTime().toLocalTime(),startTime,endTime);
        BiPredicate<LocalDateTime,Integer> logicExceed =
                (date,exceed) -> (mealList
                        .stream()
                        .filter(e -> e.getDateTime().toLocalDate().equals(date.toLocalDate()))
                        .mapToInt(UserMeal::getCalories).sum() > exceed)?true:false;

        return mealList.stream().filter(pFilterStartEndTime).
                map(e -> new UserMealWithExceed(e.getDateTime(),
                        e.getDescription(),
                        e.getCalories(),
                        logicExceed.test(e.getDateTime(),caloriesPerDay)))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed>  getFilteredWithExceededWithoutAll(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with correctly exceeded field

        //Формируем справочную карту, суммируем калории за день
        Map<LocalDate,Integer> dayCaloryMap = new HashMap<>();
        for (UserMeal userMeal: mealList) {
            LocalDate localDate = userMeal.getDateTime().toLocalDate();
            dayCaloryMap.put(localDate, userMeal.getCalories() +
                            (dayCaloryMap.containsKey(localDate)?dayCaloryMap.get(localDate):0));
        }

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
        return exceedList;
    }
}