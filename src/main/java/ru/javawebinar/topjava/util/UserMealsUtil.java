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

        final Map<LocalDate,Integer> dayCaloryMap = mealList.stream().collect(
                Collectors.groupingBy(e -> e.getDateTime().toLocalDate(),
                                      Collectors.summingInt(UserMeal::getCalories)));

        return mealList.parallelStream().filter(e -> TimeUtil.isBetween(e.getDateTime().toLocalTime(),startTime,endTime))
                       .map(e -> new UserMealWithExceed(e.getDateTime(),e.getDescription(), e.getCalories(),
                       dayCaloryMap.get(e.getDateTime().toLocalDate())>caloriesPerDay))
                       .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed>  getFilteredWithExceededWithoutAll(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with correctly exceeded field

        //Формируем справочную карту, суммируем калории за день
        Map<LocalDate,Integer> dayCaloryMap = new HashMap<>();
        for (UserMeal uM: mealList)
            dayCaloryMap.merge(uM.getDateTime().toLocalDate(), uM.getCalories(), Integer::sum);

        //Фильтруем и конвертируем объекты
        List<UserMealWithExceed> exList = new ArrayList<>();
        for (UserMeal uM: mealList)
            if (TimeUtil.isBetween(uM.getDateTime().toLocalTime(),startTime,endTime))
                exList.add(new UserMealWithExceed(uM.getDateTime(),uM.getDescription(),uM.getCalories(),
                          dayCaloryMap.get(uM.getDateTime().toLocalDate())>caloriesPerDay));

        return exList;
    }
}