package ASIris;

import javafx.util.Pair;
import java.util.Arrays;

public class Ship {
    //переменная, хранящая количество палуб.
    public Integer decks = 1;

    //суть корабля в его позиции, то есть координате, поэтому поле, хранящее координату
    public Pair<Integer,Integer>[] positions;

    //в конструктор передаем координату (при вводе игрока или при случайном выборе возможной координаты ПК)
    public Ship(int decks) {
        this.decks = decks;
        this.positions = new Pair[decks];
        Arrays.fill(positions, null);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.positions);
    }

    //метод проверки готовности (заполненности) корабля (координатами)
    public boolean shipIsReady() {
        int countOfNull = 0;
        for (Pair<Integer,Integer> position : positions) {
            if (position == null) {
                countOfNull++;
            }
        }
        return countOfNull == positions.length;
    }

    //метод проверки потоплен или нет корабль
    public boolean shipIsDrown(Field field) {
        int countOfHits = 0;
        for (Pair<Integer,Integer> position : positions) {
            if (field.cells[position.getKey()][position.getValue()].getStatus() == 3) {
                countOfHits ++;
            }
        }
        return countOfHits == positions.length;
    }

}
