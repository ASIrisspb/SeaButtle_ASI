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

    //метод проверки потоплен или нет корабль. На вход подаем координату выстрела и поле, которому принадлежит корабль
    public boolean shipIsDrown(Field field, Pair<Integer,Integer> shoot) {
        //обнуляем счетчик попаданий дааного корабля
        int countOfHits = 0;

//        //записываем во временную переменную статус клетки, по которой был выстрел
//        int temp = field.cells[shoot.getKey()][shoot.getValue()].getStatus();
//        //временно меняем статус клетки по которой был выстрел на попадание
//        field.cells[shoot.getKey()][shoot.getValue()].setStatus(3);

        //проходим циклом по всем позициям корабля
        for (Pair<Integer,Integer> position : positions) {
            //если статус клетки-позиции корабля "подбит",
            if (field.cells[position.getKey()][position.getValue()].getStatus() == 3) {
                //то плюсуем счетчик
                countOfHits ++;
            }
        }

//        //возвращаем клетке ее изначальный статус
//        field.cells[shoot.getKey()][shoot.getValue()].setStatus(temp);

        //возвращаем истину, если все позиции корабля "подбиты"
        return countOfHits == positions.length;
    }

}
