package com.company;

import java.util.*;
import java.util.stream.Collectors;


class Test {

}

public class Main {

    public static void main(String[] args) {
        var duplicate1 = new Test();
        var duplicate2 = new Test();
        var collection = Arrays.asList(duplicate1, duplicate1, new Test(), new Test(), duplicate1, new Test(), duplicate2, new Test(), duplicate2);
        var distinct = removeDuplicates(collection);
        System.out.println("First exercise");
        for (var e: distinct) {
            System.out.println(e);
        }
        System.out.println("=".repeat(20));

        System.out.println("Second exercise");
        var hm = new HashMap<String, Integer>();
        // Put elements to the map
        hm.put("Maths", 1);
        hm.put("Science", 2);
        hm.put("English", 3);
        hm.put("Physics", 2);
        hm.put("Chemistry", 1);
        for (var entry : swapKeyAndValues(hm).entrySet()) {
            System.out.println(entry);
        }
        System.out.println("=".repeat(20));

        System.out.println("Third exercise");
        var scores = new String[]{"Ivan 5", "Petr 3", "Alex 10", "Petr 8", "Ivan 6", "Alex 5", "Ivan 1", "Petr 5", "Alex 1"};
        game(scores);
    }


    // Задача №1. Написать 'Distinct'.
    public static <T> Collection<T> removeDuplicates(Collection<T> collection) {
        return collection.stream().distinct().collect(Collectors.toList());
    }

    // Задача №2.
    public static <K, V> Map<V, Collection<K>> swapKeyAndValues(Map<K, V> map) {
        var result = new HashMap<V, Collection<K>>();
        var distinctValues = removeDuplicates(map.values());
        for (var value : distinctValues) {
            result.put(value, new ArrayList<>());
        }
        for (var entry : map.entrySet()) {
            result.get(entry.getValue()).add(entry.getKey());
        }
        return result;
    }

    // Задача №3.
    public static void game(String[] scores) {
        var nameToScore = new HashMap<String, Score>();
        var i = 0;
        for (var score : scores) {
            var nameAndPoints = score.split(" ");
            if (!nameToScore.containsKey(nameAndPoints[0])) {
                var scoreWithOrder = new Score();
                scoreWithOrder.owner = nameAndPoints[0];
                scoreWithOrder.value = 0;
                nameToScore.put(nameAndPoints[0], scoreWithOrder);
            }
            nameToScore.get(nameAndPoints[0]).value += Integer.parseInt(nameAndPoints[1]);
            nameToScore.get(nameAndPoints[0]).order = i;
            i++;
        }

        Comparator<Score> comparator = Comparator.comparing(x -> -x.value);
        comparator = comparator.thenComparing(x -> x.order);
        var scoreTable = Arrays.stream(nameToScore.values().toArray(new Score[0])).sorted(comparator).collect(Collectors.toList());
        System.out.println("And the winner is: " + scoreTable.get(0).owner);
        System.out.println();
        System.out.println("Score Table");

        for (var score : scoreTable) {
            System.out.println(score);
        }
    }
}

class Score {
    public String owner;
    public Integer value;
    public Integer order;

    @Override
    public String toString() {
        return "Score{" +
                "owner='" + owner + '\'' +
                ", value=" + value +
                ", order=" + order +
                '}';
    }
}