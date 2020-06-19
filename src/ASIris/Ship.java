package ASIris;

import javafx.util.Pair;

//ПОКА НЕ РЕШИЛ КАК МОЖНО ЭТО ИСПОЛЬЗОВАТЬ!!!!

public class Ship {
    //переменная, хранящая количество палуб. Пока заморожено
    //private Integer decks;

    //суть корабля в его позиции, то есть координате, поэтому поле, хранящее координату
    private Pair<Integer,Integer> position;

    //в конструктор передаем координату (при вводе игрока или про случайном выборе возможной координаты ПК)
    public Ship(Pair<Integer, Integer> position) {
        this.position = position;
    }

}
