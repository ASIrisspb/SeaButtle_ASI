package ASIris;

/*
    @author Ivanov Alexandr
    @version SeaButtle_ASI.VersionConsole.3
    VersionConsole 1: Игрок и ПК, однопалубные корабли
    VersionConsole 2: Игрок и 2 ПК, однопалубные корабли, укорочен метод мэйн для лучшей архитектуры
    VersionConsole 3: Исправлены баги алгоритма игры
    VersionConsole 4: Реализация выбора количества соперников (1 или 2 пока)
 */

import javafx.util.Pair;
import java.io.*;

public class Main {
    //переменная (статическая для того, чтобы была доступна везде в теле метода мэйн), хранящая ход игрока
    private static Pair<Integer,Integer> step;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        //приветствие.
        System.out.println("Приветствую! Это Морской Бой с произвольным количеством игроков");
        //предлагаем выбрать количество сопреников
        System.out.println("Для начала определите количество соперников (пока от 1 до 10):");
        //выбор игрока получаем через метод
        int countEnemys = choiceFromRangeNumbers(1,10);
        //создаем массив игроков (на 1 больше количества соперников, так как Игрок)
        Field[] players = new Field[countEnemys + 1];

        //создаем объект "поле" для игрока (всегда первое поле)
        players[0] = new Field("Игрок");

        // создаем объекты "поля" для всех ПК
        for (int i = 1; i < players.length; i++) {
            String name = "Компьютер" + i;
            players[i] = new Field(name);
        }

        //выводим пустые поля
        Field.print(players);

        //инструкция к игре 1
        System.out.println("Первое поле для Вас, остальные для отображения ваших выстрелов по полям соперников");
        System.out.println("Теперь вам надо расставить свои корабли.");

        //заполняем поля компьютеров кораблями
        for (int i = 1; i < players.length ; i++) {
            fillFieldPC(players[i]);
        }

        //Игрок расставляет свои корабли!
        fillFieldUser(players[0]);

        //выводим на экран все игровые поля
        Field.print(players);

        //инструкция к игре 2
        System.out.println("Отлично! Теперь можно играть.");
        System.out.println("Ход нужно делать в том же формате, что расстановку кораблей - " +
                "указать адрес ячейки (например \"а5\")");
        System.out.println("Обратите внимание, что любой выстрел, осуществляется СРАЗУ ПО ВСЕМ ПОЛЯМ ПРОТИВНИКОВ!");
        System.out.print("Итак, Ваш ход: ");

        //ПРОЦЕСС ИГРЫ////////////////////////////////////////////////////////////////////////////////////////

        //вводим логический тригер для включения цикла игры
        boolean canGame = true;

        //условие окончания игры - подбиты все корабли у всех игроков, кроме одного
        while (canGame) {

            //цикл по массиву игроков, так как каждый ходит по очереди
            for (int i = 0; i < players.length; i++) {
                //если у любого игрока еще есть корабли, то он может ходить
                //таким образом мы будем продолжать играть, даже если один игрок закончит игру
                //чтобы зайти в метод выстрела пользователя
                if (i == 0) {
                    //если игрок имеет еще корабли, то он может стрелять
                    if (players[i].ships.size() != 0) {
                        shootUser(players[i].name, players);
////тестовая автоматическая игра за игрока
// shootPC("Игрок", 0, players);
                    }
                //если речь не об игроке, то иначе
                } else {
                    //если и-тый ПК имеет корабли, то он может стрелять
                    if (players[i].ships.size() != 0) {
                        shootPC(players[i].name, i, players);
                    }
                }
            }

            //определяем возможность продолжения игры (цикла)
            //вводим переменную для подсчета выбывших учатсников
            int countLooser = 0;
            //проходим по массиву игроков и считаем количество нулевых кораблей
            for (Field player : players) {
                //если у данного игрока нет кораблей, то добавляем его в количество проигравших
                if (player.ships.size() == 0) {
                    countLooser ++;
                }
                //если количество проигравших равно общее количество минус 1, то игра закончена
                if (countLooser == players.length - 1) {
                    canGame = false;
                }
            }

            //если Игрок еще в игре, то выводим текущее состояние полей, т.о. мы не будем рисовать поля,
            // когда ПК доигрывают между собой
            if (players[0].ships.size() != 0) {
                Field.print(players);
                //вывод количества кораблей у игроков, чтобы видеть текующую ситуацию
                for (Field field : players) {
                    System.out.println("У " + field.name + " осталось кораблей: " + field.ships.size());
//тестовый вывод
//                  System.out.println("Доступные ходы для: " + field.name + ":" + field.availableSteps);
                }

                //и поскольку Игрок еще в игре, а ход снова перешел к нему, то предлагаем снова сделать выстрел
                System.out.print("И снова ваш ход: ");
            }
            //и цикл запускается заново, если countLooser не говорит, что остался всего один игрок
        }

        //пустая строка для красоты и так как предыдущий вывод без перехода на новую строку
        System.out.println();
        //если цикл прервался, то значит кто-то победил, поэтому делаем завершение программы
        //выводим итоговое состояние полей, чтобы было видно попадания во все корабли проигравших
        Field.print(players);
        //говорим, кто победил
        for (int i = 0; i < players.length; i++) {
            if (i == 0) {
                if (players[0].ships.size() > 0) System.out.println("Поздравляем с ПОБЕДОЙ!");
            } else {
                if (players[i].ships.size() > 0) System.out.println("Победил " + players[i].name);
            }
        }
        //и прощаемся
        System.out.println("Спасибо за игру!");
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private static void shootUser(String nameShooter, Field[] players) throws Exception {
        //вводим массив логических переменных, согласно количеству противников (ПК): каждая определяет
        // возможность стрельбы по нему (есть ли корабли еще)
        boolean[] shootable = new boolean[players.length - 1];
        for (int i = 1; i < players.length; i++) {
            shootable[i-1] = players[i].ships.size() > 0;
        }
        //логический тригер для цикла
        boolean canShoot= true;
        //переменная для реализации логики срабатывания логического тригера, который должен стать ложным,
        // только когда игрок не может стрелять по другим полям, а именно либо все промахи,
        // либо у всех закончились корабли
        int countOfFalse = 0;
        //запускаем цикл, так как игрок должен при попадании в любое из полей делать следующий выстрел
        //цикл не запуститься, если оба компьютера не имеют кораблей, а значит игрок уже победил,
        // также цикл прервется, если в результате ходов корабли закончатся (!)
        while (canShoot) {
            //считываем ход игрока в статическую переменную, хранящую индексы клетки
            step = readUserStep();
            //получаем результат выстрела по полям противников (ПК) - в итоге получаем
            // новый массив логических переменных
            for (int i = 0; i < shootable.length; i++) {
                shootable[i] = checkResultOfShoot(nameShooter,players[i+1],step);
            }
            //проходим по массиву логических переменных и если все стали ложными,
            // то выходим из цикла (выключаем тригер)
            for (boolean b : shootable) {
                if (!b) {
                    countOfFalse++;
                }
                if (countOfFalse == shootable.length) {
                    canShoot = false;
                //при любом попадании выводим все поля
                }
            }
            if (canShoot) Field.print("Без игрока", players);
        }
    }

    //метод проверяющий результат выстрела и возвращающий ЛОЖЬ, если промах, и ПРАВДА, если попал.
    private static boolean checkResultOfShoot(String nameShooter, Field field, Pair<Integer, Integer> shoot) {

        if (field.cells[shoot.getKey()][shoot.getValue()].getStatus() == 2) {
            //если попал, то выводим сообщение
            System.out.println(nameShooter + " потопил корабль у " + field.name + "!");
            //уменьшаем список количества кораблей
            field.delFromShips(shoot);
            //устанавливаем статус данной клетки на "попадание"
            field.cells[shoot.getKey()][shoot.getValue()].setStatus(3);
            //не забываем сделать ее видимой
            field.cells[shoot.getKey()][shoot.getValue()].visible = true;
            //делаем обрисовку корабля
            noShoot(shoot, field);
            //а также убираем данный ход из списка возможных ходов
            field.delFromAvailableSteps(shoot);
            //так как попали в корабль, то, возможно, это последний корабль, поэтому
            // делаем проверку проигрыша ПК1 (список кораблей должен быть пуст)
            if (field.ships.size() == 0) {
                //выводим сообщение о проигрыше
                System.out.println(field.name + " ПРОИГРАЛ!");
                //логическую переменную переводим в положение "нелья больше стрелять"
                return false;
                }
            //так как попадание, то игрок может ходить еще, поэтому возвращаем ИСТИНУ,
            // но если корабли закончаться, то этот параметр не сработает (сработает ЛОЖЬ выше), кроме того,
            // если будет снова попадание из-за продолжения по другому полю, то ход снова возобновиться
            return true;
        }
        //если попадания нет, то продолжаем
        //если сюда еще не стреляли, то
        if (field.cells[shoot.getKey()][shoot.getValue()].getStatus() == 1) {
            //устанавливаем статус данной клетки на "попадание"
            field.cells[shoot.getKey()][shoot.getValue()].setStatus(4);
            //а также не забываем сделать ее видимой
            field.cells[shoot.getKey()][shoot.getValue()].visible = true;
            //устанавливаем логическое "не может стрелять" так как выстрел сделан и попадания нет,
            // но если будет попадание по другому полю и будет снова ход этот параметр не будет
            // препятствовать проверке выстрела пока оба логических параметра не окажутся ложью
            return false;
        }
        //во всех остальных случаях (подбитый корабль (статус - 3) или промах или сработала обрисовка (статус - 4))
        // просто возвращаем ЛОЖЬ
        return false;
    }


    //метод, осуществляющий выстрел ПК
    private static void shootPC(String nameShooter, int positionPlayer, Field[] players) {
        //вводим массив логических переменных, согласно количеству противников для данного игрока:
        // каждая определяет возможность стрельбы по нему (есть ли корабли еще)
        boolean[] shootable = new boolean[players.length - 1];
        //вводим переменную для индексации логических переменных,
        // так как не можем совместить с индексом массива игроков
        int indexOfShootable = 0;
        //проходим циклом по массиву игроков, исключая собственное поле
        for (int i = 0; i < players.length; i++) {
            if (i != positionPlayer) {
                shootable[indexOfShootable] = players[i].ships.size() > 0;
                indexOfShootable++;
            }
        }

        //логический тригер для цикла
        boolean canShoot= true;
        //переменная для реализации логики срабатывания логического тригера, который должен стать ложным,
        // только когда игрок не может стрелять по другим полям, а именно либо все промахи,
        // либо у всех закончились корабли (счетчик ложных shootable)
        int countOfFalse = 0;

        //запускаем цикл, так как ПК должен при попадании делать следующий выстрел
        //цикл не запуститься, если игрок и другой компьютер не имеют кораблей, а значит этот ПК уже победил
        while (canShoot) {

            //координаты выстрела выбираем из поля, в котором осталось больше кораблей
            //переменная для определения максимального значения кораблей у игроков
            int countOfShips = 0;
            //переменная для переноса индекса поля с максимальным значением кораблей
            int choiceIndex = 0;
            //проходим циклом по массиву игроков
            for (int i = 0; i < players.length; i++) {
                //если количество кораблей данного игрока больше максимума, то
                if (countOfShips <= players[i].ships.size()) {
                    //если это не сам стреляющий игрок,
                    if (i != positionPlayer) {
                        //записываем в максимум новое значение
                        countOfShips = players[i].ships.size();
                        //фиксируем индекс игрока с максимальным количеством кораблей
                        choiceIndex = i;
                    }
                }
            }

            //создаем переменную для хранения случайного индекса выстрела
            int randomCoordinate = (int) (Math.random() * (players[choiceIndex].availableSteps.size() - 1));
            //извлекаем случайные координаты для поля с максимальным количеством кораблей
            step = players[choiceIndex].availableSteps.get(randomCoordinate);

            //выводим ход ПК
            System.out.println(nameShooter + " сделал такой ход: " + translateToHumanIndex(step));

            //получаем результат выстрела по полям противников - в итоге получаем
            // новый массив логических переменных

            //вводим переменную для индексации логических переменных,
            // так как не можем совместить с индексом массива игроков
            indexOfShootable = 0;

            for (int i = 0; i < players.length; i++) {
                if (i != positionPlayer) {
                    shootable[indexOfShootable] = checkResultOfShoot(nameShooter,players[i],step);
                    indexOfShootable++;
                }
            }

            //проходим по массиву логических переменных и если все стали ложными,
            // то выходим из цикла (выключаем тригер)
            for (boolean b : shootable) {
                if (!b) {
                    countOfFalse++;
                }
                if (countOfFalse == shootable.length) {
                    canShoot = false;
                }
            }
        }
    }

    //метод обратного перевода координаты в человеческое представление
    private static String translateToHumanIndex(Pair<Integer, Integer> step) {
        char column = Field.alphabet[step.getValue()];
        String line = String.valueOf(step.getKey()+1);
        return column + line;
    }

    //метод обрисовки подбитого корабля "ноликами"
    private static void noShoot(Pair<Integer, Integer> pair, Field field) {
        //циклом перебираем все клетки вокруг данной
        for (int i = pair.getKey() - 1; i <= pair.getKey() + 1; i++) {
            for (int j = pair.getValue() - 1; j <= pair.getValue() + 1; j++) {
                //если координата клетки не выходит за пределы поля, то идем дальше
                if (i >= 0 && i < 10 && j >= 0 && j < 10) {
                    //если статус этой клетки "не было еще хода", то ставим статус 4 (ноль)
                    if (field.cells[i][j].getStatus() == 1) {
                        field.cells[i][j].setStatus(4);
                        //и убираем данную клетку из списка доступных ходов (для ПК)
                        Pair<Integer,Integer> pairTemp = new Pair<>(i,j);
                        field.delFromAvailableSteps(pairTemp);
                        //а также не забываем сделать ее видимой
                        field.cells[i][j].visible = true;

                    }
                }
            }
        }
    }

    //Метод для расстановки кораблей ПК. В метод передаем поле ПК, которое изменяется методом
    private static void fillFieldPC(Field fieldPC) {
        //делаем в цикле, так как нужно расставить 10 кораблей
        while (fieldPC.ships.size() < 10) {
            //случайное число от 0 до 99, которое позовлит случайно получить координату из списка "человечески" координат
            int randomCoordinate = (int)(Math.random()*99);
            //получаем пару (координаты) путем перевода из "человеческого" вида в индексы массива клеток
            step = Field.translateTable.get(Field.allCoordinates.get(randomCoordinate));
            //проверяем допустима ли данная координата с учетом уже расставленных
            if (isValidCoordinate(step,fieldPC)) {
                //если да, то добавляем координату в список кораблей и меняем статус клетки на обозначение корабля
                fieldPC.ships.add(step);
                fieldPC.cells[step.getKey()][step.getValue()].setStatus(2);
            }
        }
    }

    //Метод для расстановки кораблей игроком. В метод передаем поле игрока, которое изменяется методом
    private static void fillFieldUser(Field fieldUser) {
        //инструкция Расстановка.1
        System.out.println("Выбор расстановки: введите \"1\", если будете расставлять корабли самостоятельно, " +
                "или \"2\", для выбора случайной авторасстановки");

        //переменная типа расстановки (1 - ручная, 2 - авто)
        int typeFillField = choiceFromRangeNumbers(1, 2);

        //вводим логическую переменную, определяющую тип расстановки true - ручная (1), а false - авто (2).
        boolean choiceUser = typeFillField == 1;

        //если выбран ручной тип (введена "1")
        if (choiceUser) {
            System.out.println("Для расстановки кораблей нужно будет указывать координаты " +
                    "коробля в формате, например, \"а5\"");
            System.out.print("Начнем! ");
            //цикл так как нужно расставить 10 караблей
            while (fieldUser.ships.size() < 10) {
                System.out.println("Введите координату вашего корабля:");
                //считываем введенную игроком координату для создания корабля и заносим ее в статическую переменную
                step = readUserStep();
                //проверяем допустима ли данная координата
                if (isValidCoordinate(step, fieldUser)) {
                    //если да, то заносим коодинату в список кораблей и меняем значение статуса клетки
                    fieldUser.ships.add(step);
                    fieldUser.cells[step.getKey()][step.getValue()].setStatus(2);
                    //а также делаем данную клету видимой для печати
                    fieldUser.cells[step.getKey()][step.getValue()].visible = true;
                    //гооврим какой корабль по счету введен и просим ввести следующий
                    System.out.println("Вы разместили " + fieldUser.ships.size() + "-й корабль. " +
                            "Всего надо 10 кораблей :)");
                    //для визуального удобства делаем вывод поля
                    Field.print(fieldUser);
                    //если нет, то просим ввести новую
                } else {
                    System.out.println("Сюда нельзя поставить корабль (либо уже стоит, либо слишком близко к другим.");
                    System.out.println("Введите, пожалуйста, заново:");
                }
            }
        //иначе выбран тип автозаполнения (введена "2")
        } else {
            //делаем в цикле, так как нужна возможность переиграть, если игроку не понравилась расстановка
            //вводим тригер выхода из цикла
            boolean isOK;
            do {
                //автозаполнение поля игрока
                fillFieldPC(fieldUser);
                //коррекция видимости клеток чтобы видеть корабли при автозаполнении
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (fieldUser.cells[i][j].getStatus() == 2) {
                            fieldUser.cells[i][j].visible = true;
                        }
                    }
                }
                //вывод полученного поля
                Field.print(fieldUser);
                System.out.println("Вас устраивает данная расстановка?");
                System.out.println("Если да - введите цифру 1 и продолжим игру, если нет - цифру 2 " + "и сделаем автозаполнение заново");
                //определяем тригер выхода из цикла через метод с проверкой вводимых значений
                isOK = choiceFromRangeNumbers(1, 2) == 1;
                //если пользователь ввел 2, то есть НЕдоволен расстановкой, то обнуляаем результаты расстановки
                if (!isOK) {
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (fieldUser.cells[i][j].getStatus() == 2) {
                                fieldUser.cells[i][j].setStatus(1);
                                fieldUser.cells[i][j].visible = false;
                            }
                        }
                    }
                    fieldUser.delFromShipsAll(fieldUser);
                }
            } while (isOK);
        }
    }

    //метод, проверяющий допустимость выбора клетки при условии наличия других кораблей
    private static boolean isValidCoordinate(Pair<Integer, Integer> pair, Field field) {
        if (field.cells[pair.getKey()][pair.getValue()].getStatus() == 2) return false;
        if (field.ships.size()>0) {
            for (int i = 0; i < field.ships.size(); i++) {
                //временные переменные созданы для краткости записи условий
                //а - разница по столбцам, взятая по модулю
                int a = Math.abs(field.ships.get(i).getKey() - pair.getKey());
                //разница по строкам, взятая по модулю
                int b = Math.abs(field.ships.get(i).getValue() - pair.getValue());
                //проверяем условия близости координат введенных раннее кораблей с проверяемой
                if (a == 0 && b == 1) return false;
                if (b == 0 && a == 1) return false;
                if (a == 1 && b == 1) return false;
            }
        }
        return true;
    }

    //метод, считывающий человеческую координату и переводящий ее в индексы массива клеток с проверкой правильности
    private static Pair<Integer,Integer> readUserStep() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //цикл нужен для того, чтобы добиться от игрока правильного ввода
        while (true) {
            //считываем введенную строку и делаем маленькие буквы на всякий случай, если игрок забыл
            String coordinate = null;
            try {
                coordinate = bufferedReader.readLine().toLowerCase();
            } catch (IOException e) {
                System.out.println("Что-то пошло не так при вводе с клавиатуры");;
            }
            //проверяем, допустимое ли значение ввел игрок
            if (Field.allCoordinates.contains(coordinate)) {
                return Field.translateTable.get(coordinate);
            //если же игрок ввел неправильные координаты, то заставляем его сделать это заново
            } else {
                System.out.println("Некорректная координата. Введите, пожалуйста, заново");
            }
        }
    }
    //метод выбора пользователем из диапазона цифр. Границы включаются.
    private static int choiceFromRangeNumbers (int beginInterval, int endInterval) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //переменная, которую вернем в качестве выбора пользователя
        int choiceValue = 0;
        //помещаем выбор в цикл, чтобы добиться выбора согласно условиям
        do {
            try {
                choiceValue = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести цифры!");
            } catch (IOException e) {
                System.out.println("Что-то пошло не так при вводе :(");
            }
            if (!(choiceValue >= beginInterval && choiceValue <= endInterval)) {
                System.out.printf("Нужно ввести цифры от %d до %d", beginInterval, endInterval);
            }
        } while (!(choiceValue >= beginInterval && choiceValue <= endInterval));
    return choiceValue;
    }
}
