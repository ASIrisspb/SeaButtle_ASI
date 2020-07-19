package ASIris;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Field {

    //переменная размерности поля (на будущее)
    private static int dimensionField = 10;
    //шаблонный список ВСЕХ возможных ходов в человеческом формате!!!
    public static ArrayList<String> allCoordinates = new ArrayList<>(Arrays.asList(
            "а1","а2","а3","а4","а5","а6","а7","а8","а9","а10",
            "б1","б2","б3","б4","б5","б6","б7","б8","б9","б10",
            "в1","в2","в3","в4","в5","в6","в7","в8","в9","в10",
            "г1","г2","г3","г4","г5","г6","г7","г8","г9","г10",
            "д1","д2","д3","д4","д5","д6","д7","д8","д9","д10",
            "е1","е2","е3","е4","е5","е6","е7","е8","е9","е10",
            "ж1","ж2","ж3","ж4","ж5","ж6","ж7","ж8","ж9","ж10",
            "з1","з2","з3","з4","з5","з6","з7","з8","з9","з10",
            "и1","и2","и3","и4","и5","и6","и7","и8","и9","и10",
            "к1","к2","к3","к4","к5","к6","к7","к8","к9","к10"));
    //шаблон отображения линий поля
    static String[] numberOfLine = {" 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8", " 9", "10"};
    //шаблон отображения столбцов поля
    static Character[] alphabet = {'а','б','в','г','д','е','ж','з','и','к'};
    //таблица перевода строчного ввода координат в цифровой, содеражщийся в парах
    public static HashMap<String,Pair<Integer,Integer>> translateTable = new HashMap<>();
    //помещаем заполнение статической переменной в блок статик, так как это делается один раз при инициализации класса
    static {
        //цикл для строк
        for (int i = 0; i < dimensionField; i++) {
            //значение строки получаем из значения итератора плюс 1, так как строки нумеруются с 1, а не с 0
            String number = String.valueOf(i+1);
            //цикл для заполнения столбцов в данной строке
            for (int j = 0; j < dimensionField; j++) {
                //берем букву из эталона столбцов по индексу
                char letter = alphabet[j];
                //соединяем столбец и строку, чтобы получить человеческий вид координаты (индекс клетки)
                String humanIndex = letter + number;
                //создаем пару для хранения индекса клетки
                Pair<Integer,Integer> pair = new Pair<>(i,j);
                //кладем в словарь человеческие координаты и координаты для массива
                translateTable.put(humanIndex,pair);
            }
        }
    }
    //создаем заготовку поля в качестве переменной объекта (поля класса)
    public Cell[][] cells = new Cell[dimensionField][dimensionField];
    //переменная (поле класса) для учета сделанных выстрелов по данному полю в форме списка пар координат
    public ArrayList<Pair<Integer, Integer>> availableSteps = new ArrayList<>(dimensionField * dimensionField);
    // переменная для хранения клеток кораблей также в форме списка пар координат
    public ArrayList<Pair<Integer, Integer>> ships = new ArrayList<>(20);
    //переменная для хранения имени поля (игрока или ПК)
    public String name;


    //конструктор класса, в котором создаем пустое поле
    public Field(String name) {
        //задаем имя полю
        this.name = name;
        //заполняем поле элементами по умолчанию (пустые клетки = точки)
        for (int i = 0; i < dimensionField; i++) {
            for (int j = 0; j < dimensionField; j++) {
                //конструктор клетки
                cells[i][j] = new Cell();
                //тут же заносим в переменную список пар координат данного поля
                availableSteps.add(new Pair<>(i,j));
            }
        }
    }

    //метод удаления из списка доступных ходов для ПК availableSteps (пары координат)
    public void delFromAvailableSteps (Pair<Integer, Integer> pair) {
        Iterator<Pair<Integer, Integer>> iterator = this.availableSteps.iterator();
        while (iterator.hasNext()) {
            Pair<Integer, Integer> nextPair = iterator.next();
            if ((nextPair.getKey().equals(pair.getKey())) && (nextPair.getValue().equals(pair.getValue()))) {
                iterator.remove();
            }
        }
    }

    //метод удаления из списка кораблей (пары координат корабля)
    public void delFromShips (Pair<Integer, Integer> pair) {
        Iterator<Pair<Integer, Integer>> iterator = this.ships.iterator();
        while (iterator.hasNext()) {
            Pair<Integer, Integer> nextPair = iterator.next();
            if ((nextPair.getKey().equals(pair.getKey())) && (nextPair.getValue().equals(pair.getValue()))) {
                iterator.remove();
            }
        }
    }

    public void delFromShipsAll (Field field) {
        for (int i = field.ships.size() - 1; i >= 0; i--) {
            field.ships.remove(0);
        }
    }

    //метод вывода в консоль полей с текущим состоянием клеток (все поля)
    public static void print(Field[] fields) {
        for (Field value : fields) {
            System.out.print("      " + value.name + "       ");
        }
        System.out.println();
        for (int i = 0; i < fields.length; i++) {
            System.out.print("  ");
            for (char alphabet: alphabet) {
                String s = " " + alphabet;
                System.out.print(s.toUpperCase());
            }
            System.out.print(" ");
        }
        System.out.println();
        for (int i = 0; i < dimensionField; i++) {
            for (Field field : fields) {
                System.out.print(numberOfLine[i] + " ");
                for (int j = 0; j < dimensionField; j++) {
                    System.out.print(field.cells[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    //метод вывода в консоль полей с текущим состоянием клеток (все поля)
    public static void print(Field field) {
        System.out.println(field.name);
        System.out.print("  ");
            for (char alphabet: alphabet) {
                String s = " " + alphabet;
                System.out.print(s.toUpperCase());
            }
        System.out.println();
        for (int i = 0; i < dimensionField; i++) {
                System.out.print(numberOfLine[i] + " ");
                for (int j = 0; j < dimensionField; j++) {
                    System.out.print(field.cells[i][j] + " ");
                }
            System.out.println();
        }
    }


    //метод вывода в консоль полей с текущим состоянием клеток (без поля Игрока)
    public static void print(String withoutPlayer, Field[] fields) {
        withoutPlayer = "Без Игрока";
        for (int i = 1; i < fields.length; i++) {
            System.out.print("  ");
            for (char alphabet: alphabet) {
                String s = " " + alphabet;
                System.out.print(s.toUpperCase());
            }
            System.out.print(" ");
        }
        System.out.println();
        for (int i = 0; i < dimensionField; i++) {
            for (int k = 1; k < fields.length; k++) {
                System.out.print(numberOfLine[i] + " ");
                for (int j = 0; j < dimensionField; j++) {
                    System.out.print(fields[k].cells[i][j] + " ");
                }
            }
            System.out.println();
        }
    }


}
