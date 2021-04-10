package com.company;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        args = new String[]{
                "C100_1-100",
                "C200_1-120-1200",
                "C300_1-120-30",
                "C400_1-80-20",
                "C100_2-50",
                "C200_2-40-1000",
                "C300_2-200-45", "C400_2-10-20", "C100_3-10", "C200_3-170-1100", "C300_3-150-29", "C400_3-100-28", "C100_1-300", "C200_1-100-750", "C300_1-32-15"
        };
        AppContext appContext = new AppContext();
        CarParser carParser = new CarParser(appContext);
        Car[] cars = new Car[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            Car car = carParser.parse(arg);
            cars[i] = car;
            appContext.carsByType.get(car.carType).add(car);
        }
        appContext.allCars = cars;
        ReportService reportService = new ReportService(appContext);
        double totalExpenses = reportService.getSumOfExpenses(Arrays.asList(cars));
        System.out.println("Общая стоимость рахсодов на ГСМ: " + totalExpenses);
        String mostExpensiveType = "";
        double mostExpenses = 0;
        String leastExpensiveType = "";
        double leastExpenses = Double.MAX_VALUE;
        for (CarType carType : appContext.carTypes) {
            double typeExpenses = reportService.getSumOfExpensesByType(carType);
            if (typeExpenses > mostExpenses) {
                mostExpenses = typeExpenses;
                mostExpensiveType = carType.name;
            }
            if (typeExpenses < leastExpenses) {
                leastExpenses = typeExpenses;
                leastExpensiveType = carType.name;
            }
            System.out.println("Общая стоимость рахсодов на ГСМ для: " + carType.name + " - " + typeExpenses);
        }
        System.out.println("Тип авто имеющий наибольшую стомость расходов: " + mostExpensiveType);
        System.out.println("Тип авто имеющий наименьшую стомость расходов: " + leastExpensiveType);

        System.out.println();
        System.out.println("Инфо об авто в разрезе типа, сортировка: пробег потом доп параметр (если он есть)");
        for (Car car : reportService.getOrdered(appContext.carTypes[3])) {
            System.out.println("Номер        - " + car.governmentNumber);
            System.out.println("Пробег       - " + car.mileage);
            System.out.println("Доп параметр - " + car.extraParam);
        }
    }
}

class ReportService {
    private AppContext _appContext;

    public ReportService(AppContext appContext) {
        _appContext = appContext;
    }

    public Collection<Car> getOrdered(CarType carType) {
        List<Car> cars = _appContext.carsByType.get(carType);
        Comparator<Car> comparator = Comparator.comparing(x -> x.mileage);
        if (carType.hasExtraParam) {
            comparator.thenComparing(x -> x.extraParam);
        }
        return cars.stream().sorted(comparator).collect(Collectors.toList());
    }

    public Collection<Car> getOrderedByMileage(CarType carType) {
        List<Car> cars = _appContext.carsByType.get(carType);
        Comparator<Car> comparator = Comparator.comparing(x -> x.mileage);
        return cars.stream().sorted(comparator).collect(Collectors.toList());
    }

    public Collection<Car> getOrderedByExtraParam(CarType carType) {
        if (!carType.hasExtraParam) {
            throw new IllegalArgumentException("carType, Тип не имеет доп параметра.");
        }
        List<Car> cars = _appContext.carsByType.get(carType);
        Comparator<Car> comparator = Comparator.comparing(x -> x.extraParam);
        return cars.stream().sorted(comparator).collect(Collectors.toList());
    }

    public double getSumOfExpensesByType(CarType carType) {
        return getSumOfExpenses(_appContext.carsByType.get(carType));
    }

    public double getSumOfExpenses(Collection<Car> cars) {
        double totalExpenses = 0;
        for (Car car : cars) {
            totalExpenses += car.calculateExpenses();
        }
        return totalExpenses;
    }
}

class CarParser {
    private AppContext _appContext;

    public CarParser(AppContext appContext) {
        _appContext = appContext;
    }

    public Car parse(String car) {
        String[] a = car.split("_");
        String codeString = a[0].substring(1);
        int code = Integer.parseInt(codeString);
        CarType carType = _appContext.carTypeMap.get(code);
        String[] params = a[1].split("-");
        if (carType.hasExtraParam) {
            return new Car(carType, Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        }
        return new Car(carType, Integer.parseInt(params[0]), Integer.parseInt(params[1]), 0);
    }
}

class AppContext {
    public Map<Integer, CarType> carTypeMap;
    public Car[] allCars;
    public CarType[] carTypes = {
            new CarType("легковой авто", 100, 46.1, 12.5, false),
            new CarType("грузовой авто - объем перевезенного груза см. куб.", 200, 48.9, 12, true),
            new CarType("пассажирский транспорт - число перевезенных пассажиров", 300, 47.5, 11.5, true),
            new CarType("тяжелая техника(краны) - вес поднятых грузов тонн", 400, 48.9, 20, true)
    };
    public Map<CarType, ArrayList<Car>> carsByType;

    public AppContext() {
        carTypeMap = Map.of(
                carTypes[0].typeCode, carTypes[0],
                carTypes[1].typeCode, carTypes[1],
                carTypes[2].typeCode, carTypes[2],
                carTypes[3].typeCode, carTypes[3]
        );
        carsByType = Map.of(
                carTypes[0], new ArrayList<>(),
                carTypes[1], new ArrayList<>(),
                carTypes[2], new ArrayList<>(),
                carTypes[3], new ArrayList<>()
        );
    }
}

class Car {
    public CarType carType;
    public int governmentNumber;
    public int mileage;
    public int extraParam;

    public Car(CarType carType, int governmentNumber, int mileage, int extraParam) {
        this.carType = carType;
        this.governmentNumber = governmentNumber;
        this.mileage = mileage;
        this.extraParam = extraParam;
    }

    public Car() {
        // PogChamp.
    }

    public double calculateExpenses() {
        return mileage / carType.fuelConsumption * carType.fuelCost;
    }
}

class CarType {
    public String name;
    public int typeCode;
    public double fuelCost;
    public double fuelConsumption;
    public boolean hasExtraParam;

    public CarType(String name, int typeCode, double fuelCost, double fuelConsumption, boolean hasExtraParam) {
        this.name = name;
        this.typeCode = typeCode;
        this.fuelCost = fuelCost;
        this.fuelConsumption = fuelConsumption;
        this.hasExtraParam = hasExtraParam;
    }
}

class YourMom {
    String weight = "A LOT!";
}
