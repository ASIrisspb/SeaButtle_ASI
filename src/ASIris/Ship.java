package ASIris;

import javafx.util.Pair;
import java.util.Arrays;

public class Ship {
    //переменная, хранящая количество палуб.
    public Integer decks;

    //суть корабля в его позиции, то есть координате, поэтому поле, хранящее координату
    public Pair<Integer,Integer>[] positions;

    //маркер ранения. ПО умолчанию ЛОЖЬ, но если ранили, то ИСТИНА
    public boolean hurt = false;

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

    //метод проверки потоплен или нет корабль. На вход подаем координату выстрела и поле, которому принадлежит корабль
    public boolean shipIsDrown(Field field) {
        //обнуляем счетчик попаданий даного корабля
        int countOfHits = 0;
        //проходим циклом по всем позициям корабля
        for (Pair<Integer,Integer> position : positions) {
            //если статус клетки-позиции корабля "подбит",
            if (field.cells[position.getKey()][position.getValue()].getStatus() == 3) {
                //то плюсуем счетчик
                countOfHits ++;
            }
        }
        //возвращаем истину, если ВСЕ позиции корабля "подбиты"
        return countOfHits == positions.length;
    }
}
