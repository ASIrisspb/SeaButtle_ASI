package ASIris;
/*
    @author Ivanov Alexandr
    @version SeaButtle_ASI.VersionConsole.3
    VersionConsole 1: Игрок и ПК, однопалубные корабли
    VersionConsole 2: Игрок и 2 ПК, однопалубные корабли, укорочен метод мэйн для лучшей архитектуры
    VersionConsole 3: Исправлены баги алгоритма игры
    VersionConsole 4: Реализация выбора количества соперников (1 или 2 пока)
    VersionConsole 4.20200619: замена блоков выбора пользователя методом,
                                приведение методов стрельбы к единому виду
    VersionConsole 4.20200621: подтверждение пользователя при выстреле туда же или в пустоту
    VersionConsole 5.20200628: создание метода для ПК, расставляющего нормальные корабли
    VersionConsole 5.20200701: доделан метод расстановки нормальных кораблей для компьютера
    VersionConsole 5.20200707: выбор расстановки после первой координаты для пользователя
    VersionConsole 5.20200719: введение механизма "ранил-убил" и коррекция программы для этого
                                (переделка механизма хранения кораблей)

    @tasks
    - реализовать механизм "ранил - потопил"
    - скорректировать метод обрисовки кораблей!
    - стрельба ПК по возможным вариантам

    ?? количество кораблей, как и размер поля задает игрок
    ...
    - перевод в графический вид (десктопный вариант)
    ...
    - создание мобильного приложения (без сервера)
 */

import javafx.util.Pair;
import java.io.*;
import java.util.ArrayList;

public class Main {
    //переменная (статическая для того, чтобы была доступна везде в теле метода мэйн), хранящая ход игрока
    private static Pair<Integer,Integer> step;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        //приветствие.
        System.out.println("Приветствую! Это Морской Бой с произвольным количеством игроков");
        //предлагаем выбрать количество сопреников
        System.out.println("Для начала определите количество соперников (пока от 1 до 10):");
        System.out.println("ps: имейте в виду, что все поля должны уместиться на экране");
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


        //счетчик ходов
        int countOfSteps = 1;

        //вводим логический тригер для включения цикла игры
        boolean canGame = true;
        //вводим переменную для подсчета выбывших учатсников
        int countLooser;

        //условие окончания игры - подбиты все корабли у всех игроков, кроме одного
        while (canGame) {

            //вывод номера хода
            System.out.println("Ход номер: " + countOfSteps);

            //цикл по массиву игроков, так как каждый ходит по очереди
            for (int i = 0; i < players.length; i++) {
                //если у любого игрока еще есть корабли, то он может ходить
                //таким образом мы будем продолжать играть, даже если один игрок закончит игру
                if (players[i].ships.size() != 0) {
                    shoot(players[i].name, i, players);
                }
            }

            //определяем возможность продолжения игры (цикла)
            //обнуляем счетчик выбывших из игры
            countLooser = 0;
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
                }

                //и поскольку Игрок еще в игре, а ход снова перешел к нему, то предлагаем снова сделать выстрел
                System.out.print("И снова ваш ход: ");
            }
            //и цикл запускается заново, если countLooser не говорит, что остался всего один игрок

            //плюсуем счетчик
            System.out.println();
            countOfSteps++;
        }

        //ОСНОВНОЙ ЦИКЛ ИГРЫ ЗАКОНЧЕН ////////////////////////////////////////////////////////////////////////////////////////


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

    //обобщенный метод стрельбы
    private static void shoot (String nameShooter, int positionPlayer, Field[] players) {
        //вводим массив логических переменных, согласно количеству противников:
        // каждая определяет возможность стрельбы по нему (есть ли корабли еще)
        boolean[] shootable = new boolean[players.length - 1];
        //вводим переменную для индексации логических переменных,
        // так как не можем совместить с индексом массива игроков (просто, чтобы заполнить массив)
        int indexOfShootable;
        //логический тригер для цикла
        boolean canShoot= true;
        //переменная для реализации логики срабатывания логического тригера, который должен стать ложным,
        // только когда игрок не может стрелять по другим полям, а именно либо все промахи,
        // либо у всех закончились корабли
        int countOfFalse;
        //запускаем цикл, так как игрок должен при попадании в любое из полей делать следующий выстрел
        //цикл прервется, если в результате ходов корабли закончатся (!) или будут все промахи

        do {
            //ветка для хода человека
//            if (positionPlayer == 0) {
//                //тригер цикла, когда игрок должен подтвердить свой выстрел
//                boolean wantShoot = true;
//                //делаем цикл, чтобы человек мог менять свой ход несколько раз
//                while (wantShoot){
//                    //считываем ход человека методом
//                    step = readUserStep();
//                    //счетчик уже сделаных попаданий
//                    int alreadyHit = 0;
//                    //счетчик уже сделаных промахов
//                    int alreadyMiss = 0;
//                    //делаем предварительную проверку на ошибочный выстрел
//                    for (int i = 1; i < players.length ; i++) {
//                        //если в каком-то поле уже есть подбитый корабль по этой координате
//                        if (players[i].cells[step.getKey()][step.getValue()].getStatus() == 3) {
//                            //увеличиваем счетчик
//                            alreadyHit++;
//                        }
//                        //если в каком-то поле уже есть промах корабль по этой координате
//                        if (players[i].cells[step.getKey()][step.getValue()].getStatus() == 4) {
//                            //увеличиваем счетчик
//                            alreadyMiss++;
//                        }
//                    }
//                    //если во всех полях по данному выстрелу уже стоят попадания (больше относится к игре вдвоем)
//                    if (alreadyHit == players.length - 1) {
//                        //предупреждаем и просим подтвердить выстрел
//                        System.out.println("Этот выстрел уже привел к попаданию и не даст результата. " +
//                                "Вы действительно хотите так выстрелить? (введите 1, если да, и 2 - если нет)");
//                        wantShoot = choiceFromRangeNumbers(1,2) == 2;
//                        System.out.println("Введите ваш новый ход:");
//                    }
//                    //если во всех полях по данному выстрелу уже стоят промахи (больше относится к игре вдвоем)
//                    else if (alreadyMiss == players.length - 1) {
//                        //предупреждаем и просим подтвердить выстрел
//                        System.out.println("Этот выстрел не приведет к результату (очевидная пустая клетка). " +
//                                "Вы действительно хотите так выстрелить? (введите 1, если да, и 2 - если нет)");
//                        wantShoot = choiceFromRangeNumbers(1,2) == 2;
//                        System.out.println("Введите ваш новый ход:");
//                    //если не зашли в первые две ветки, значит нужно выйти из цикла
//                    } else  {
//                        wantShoot = false;
//                    }
//                }
//
            //иначе делает ход компьютер
//            } else {
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
//            }

            //после того, как определились с самим выстрелом,

            //получаем результат выстрела по полям противников
            //предварительно обнуляем переменную индексации логических переменных
            indexOfShootable = 0;
            for (int i = 0; i < players.length; i++) {
                //если это не собственное поле, то
                if (i != positionPlayer) {
                    //получаем результат выстрела в виде логического значения и заносим в массив
                    shootable[indexOfShootable] = checkResultOfShoot(nameShooter,players[i],step);
                    indexOfShootable++;
                }
            }
            //проходим по массиву логических переменных и если все стали ложными,
            // то выходим из цикла (выключаем тригер)
            //предварительно обнуляем счетчик ложных значений (чтобы не копились от цикла к циклу)
            countOfFalse = 0;
            for (boolean b : shootable) {
                //если логическая переменная ложная, то увеличиваем счетчик
                if (!b) {
                    countOfFalse++;
                }
                //если счетчик стал равен длине массива (все ложные), то переключаем тригер цикла выстрела
                if (countOfFalse == shootable.length) {
                    canShoot = false;
                }
            }
            //если выстрел игрока:
            if (positionPlayer == 0) {
                //и при этом он еще может стрелять (попал!), то выводим поля противников
                if (canShoot) Field.print("Без игрока", players);
                System.out.println("И снова ваш ход:");
            }
        } while (canShoot);
        //если мы вышли из цикла, то метод закончил свою работу
    }

    //метод проверяющий результат выстрела и возвращающий ЛОЖЬ, если промах или уже попадал сюда,
    // и ПРАВДА, если подбил корабль.
    private static boolean checkResultOfShoot(String nameShooter, Field field, Pair<Integer, Integer> shoot) {

        if (field.cells[shoot.getKey()][shoot.getValue()].getStatus() == 2) {

            //если попал, то определяем потопил или только ранил через метод
            //в зависимости от результата, выводим сообщение
            if (isKillShip(field, shoot)) {
                System.out.println(nameShooter + " потопил корабль у " + field.name + "!");
            } else {
                System.out.println(nameShooter + " ранил корабль у " + field.name + "!");
            }

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
            // делаем проверку проигрыша (список кораблей должен быть пуст)
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
        //если попадания нет, то следующие варианты:
        //если сюда еще не стреляли, то
        if (field.cells[shoot.getKey()][shoot.getValue()].getStatus() == 1) {
            //устанавливаем статус данной клетки на "попадание"
            field.cells[shoot.getKey()][shoot.getValue()].setStatus(4);
            //а также не забываем сделать ее видимой
            field.cells[shoot.getKey()][shoot.getValue()].visible = true;
            //а также убираем данный ход из списка возможных ходов
            field.delFromAvailableSteps(shoot);

            //устанавливаем логическое "не может стрелять" так как выстрел сделан и попадания нет,
            // но если будет попадание по другому полю и будет снова ход этот параметр не будет
            // препятствовать проверке выстрела пока оба логических параметра не окажутся ложью
            return false;
        }
        //во всех остальных случаях (подбитый корабль (статус - 3) или промах или сработала обрисовка (статус - 4))
        // просто возвращаем ЛОЖЬ
        return false;
    }

////////////////////////метод определения убит корабль или только ранен

    private static boolean isKillShip(Field field, Pair<Integer, Integer> shoot) {

        boolean nord;
        if ((shoot.getKey() - 1) >= 0) {
            nord = (field.cells[shoot.getKey() - 1][shoot.getValue()].getStatus() == 1) ||
                    (field.cells[shoot.getKey() - 1][shoot.getValue()].getStatus() == 4);
        } else {
            nord = true;
        }
        boolean south;
        if ((shoot.getKey() + 1) < 10) {
            south = (field.cells[shoot.getKey() + 1][shoot.getValue()].getStatus() == 1) ||
                    (field.cells[shoot.getKey() + 1][shoot.getValue()].getStatus() == 4);
        } else {
            south = true;
        }
        boolean west;
        if ((shoot.getValue() - 1) >= 0) {
            west = (field.cells[shoot.getKey()][shoot.getValue() - 1].getStatus() == 1) ||
                    (field.cells[shoot.getKey()][shoot.getValue() - 1].getStatus() == 4);
        } else {
            west = true;
        }
        boolean east;
        if ((shoot.getValue() + 1) < 10) {
            east = (field.cells[shoot.getKey()][shoot.getValue() + 1].getStatus() == 1) ||
                    (field.cells[shoot.getKey()][shoot.getValue() + 1].getStatus() == 4);
        } else {
            east = true;
        }
        //если все переменные ИСТИНА, то значит корабль потоплен - возвращаем ИСТИНА результатом метода
        return nord && south && west && east;
    }

//////////метод обратного перевода координаты в человеческое представление
    private static String translateToHumanIndex(Pair<Integer, Integer> step) {
        char column = Field.alphabet[step.getValue()];
        String line = String.valueOf(step.getKey()+1);
        return column + line;
    }

/////////метод обрисовки подбитого корабля "ноликами"
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
        //делаем в цикле, так как нужно расставить 10 кораблей (в сумме 20 клеток)
        while (fieldPC.ships.size() < 20) {
            //максимальное количество палуб
            int countOfdecks = 4;
            //массив кораблей -----  (ПОКА НИ НА ЧТО НЕ ВЛИЯЕТ И НЕ ИСПОЛЬЗУЕТСЯ)
            Ship[] ships = new Ship[10];
            //счетчик для массива корабелй
            int k = 0;
            //цикл для создания нужного количества кораблей
            //идем от самого большого
            for (int i = countOfdecks; i > 0; i--) {
                //так чтобы количество палуб уменьшалось, а количество кораблей увеличивалось
                for (int j = i - 1; j < countOfdecks; j++) {
                    //создаем экземпляр корабля с заданным количеством палуб
                    ships[k] = new Ship(i);
                    //размещаем его на поле через метод
                    postingShip(ships[k], fieldPC);
                    //увеличиваем счетчик
                    k++;
                }
            }
        }
    }

//////////РАЗМЕЩЕНИЕ КОРАБЛЯ
    private static void postingShip(Ship ship, Field fieldPC) {

        //массив кораблей для конечного выбора
        ArrayList<Ship> possibleShips = new ArrayList<>();

        //логический тригер цикла. Делаем ИСТИНА, чтобы зайти в цикл
        boolean isPossiblePosition = true;

        //МАССИВ ЛОГИЧЕСКИХ ПЕРЕМЕННЫХ
        boolean nord = true, west=true, east=true, south=true;

        //цикл, чтобы добиться допустимого размещения корабля
        while (isPossiblePosition) {

            //обнуляем логические тригеры, чтобы избежать зацикливания
            nord = true;
            west = true;
            east = true;
            south = true;

            //случайное число от 0 до 99, которое позовлит случайно получить координату из списка "человечески" координат
            int randomCoordinate = (int) (Math.random() * (fieldPC.availableSteps.size() - 1));
            //получаем пару (координаты) путем перевода из "человеческого" вида в индексы массива клеток
            step = Field.translateTable.get(Field.allCoordinates.get(randomCoordinate));

            //проверяем допустима ли данная координата с учетом уже расставленных
            if (isValidCoordinate(step, fieldPC)) {

                //запускаем цикл для определения возможности пострения корабля во всех направлениях
                for (int i = 1; i < ship.decks; i++) {
                    //определяем новую координату смещением на один вверх (север). Сейчас она не приаязана к полю
                    Pair<Integer, Integer> stepNord = new Pair<Integer, Integer>(step.getKey() + i, step.getValue());
                    //если она в рамках поля, проверяем возможные расположения корабля
                    if (validRange(stepNord)) {
                        //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                        nord = nord && isValidCoordinate(stepNord, fieldPC);
                    } else {
                        //если новая координата выходит
                        nord = false;
                    }
                    //определяем новую координату смещением на один влево (запад). Сейчас она не приаязана к полю
                    Pair<Integer, Integer> stepWest = new Pair<Integer, Integer>(step.getKey(), step.getValue() - i);
                    //если она в рамках поля, проверяем возможные расположения корабля
                    if (validRange(stepWest)) {
                        //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                        west = west && isValidCoordinate(stepWest, fieldPC);
                    } else {
                        //если новая координата выходит
                        west = false;
                    }
                    //определяем новую координату смещением на один вниз (юг). Сейчас она не приаязана к полю
                    Pair<Integer, Integer> stepSouth = new Pair<Integer, Integer>(step.getKey() - i, step.getValue());
                    //если она в рамках поля, проверяем возможные расположения корабля
                    if (validRange(stepSouth)) {
                        //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                        south = south && isValidCoordinate(stepSouth, fieldPC);
                    } else {
                        //если новая координата выходит
                        south = false;
                    }
                    //определяем новую координату смещением на один вниз (юг). Сейчас она не приаязана к полю
                    Pair<Integer, Integer> stepEast = new Pair<Integer, Integer>(step.getKey(), step.getValue() + i);
                    //если она в рамках поля, проверяем возможные расположения корабля
                    if (validRange(stepEast)) {
                        //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                        east = east && isValidCoordinate(stepEast, fieldPC);
                    } else {
                        //если новая координата выходит
                        east = false;
                    }
                }
            //если полученная координата не прошла проверку, то ставим тригеры в ЛОЖЬ, чтобы не выходить из цикла
            } else {
                nord = false;
                west = false;
                east = false;
                south = false;
            }
            //цикл будет повторяться только если ВСЕ логические переменные будут ложными,
            // то есть не будет возможности разместить корабль
            isPossiblePosition = !nord && !west && !east && !south;
        }
        //мы вышли из цикла и у нас есть 4 логические переменные, которые говорят о возможности построения
        // корабля в заданном направлении, теперь надо выбрать из доступных случайно одно направление
        // и записать в переданный в метод аргумент Ship[i]

        //заполняем список возможных кораблей, чтобы из него потом выбрать один
        //если норд ИСТИНА (корабль может быть размещен таким образом),
        // то заполняем элемент списка кораблей нужными значениями
        if (nord) {
            //создаем корабль
            Ship shipToNord = new Ship(ship.decks);
            //заполняем координаты корабля
            for (int i = 0; i < ship.decks; i++) {
                shipToNord.positions[i] = new Pair<>(step.getKey() + i, step.getValue());
            }
            //добавляем корабль в список
            possibleShips.add(shipToNord);
        }
        //повтор для другого направления (юг)
        if (south) {
            Ship shipToSouth = new Ship(ship.decks);
            for (int i = 0; i < ship.decks; i++) {
                shipToSouth.positions[i] = new Pair<>(step.getKey() - i, step.getValue());
            }
            possibleShips.add(shipToSouth);
        }
        //повтор для другого направления (запад)
        if (west) {
            Ship shipToWest = new Ship(ship.decks);
            for (int i = 0; i < ship.decks; i++) {
                shipToWest.positions[i] = new Pair<>(step.getKey(), step.getValue() - i);
            }
            possibleShips.add(shipToWest);
        }
        //повтор для другого направления (восток)
        if (east) {
            Ship shipToEast = new Ship(ship.decks);
            for (int i = 0; i < ship.decks; i++) {
                shipToEast.positions[i] = new Pair<>(step.getKey(), step.getValue() + i);
            }
            possibleShips.add(shipToEast);
        }
        //присваиваем переданному параметру ссылку на выбранный корабль через случайный выбор из списка
        // возможных позиций корабля при данной изначальной координате
        ship = possibleShips.get((int) (Math.random() * (possibleShips.size())));
//проходим циклом по созданному кораблю
        for (int i = 0; i < ship.positions.length; i++) {
            //и заносим клетки корабля в список "корабли" данного поля
            fieldPC.ships.add(ship.positions[i]);
            //а также ставим статусы "корабль" (Х) этим клеткам
            fieldPC.cells[ship.positions[i].getKey()][ship.positions[i].getValue()].setStatus(2);
        }
    }

//////////для проверки НЕ выхода за рамки поля. ИСТИНА, если внутри поля
    private static boolean validRange(Pair<Integer, Integer> step) {
        return (step.getKey() >= 0)&&(step.getKey() <= 9)&&(step.getValue() >= 0)&&(step.getValue() <= 9);
    }

//////////Метод для расстановки кораблей игроком. В метод передаем поле игрока, которое изменяется методом
    private static void fillFieldUser(Field fieldUser) {
        //инструкция Расстановка.1
        System.out.println("Выбор расстановки: введите \"1\", если будете расставлять корабли самостоятельно, " +
                "или \"2\", для выбора случайной авторасстановки");

        //вводим логическую переменную, определяющую тип расстановки true - ручная (1), а false - авто (2).
        boolean choiceUser = choiceFromRangeNumbers(1, 2) == 1;

        //если выбран ручной тип (введена "1")
        if (choiceUser) {
            System.out.println("Для расстановки кораблей нужно будет указывать координаты " +
                    "коробля в формате, например, \"а5\"");
            System.out.print("Начнем! ");

//////////////////делаем в цикле, так как нужно расставить 10 кораблей (в сумме 20 клеток)
            while (fieldUser.ships.size() < 20) {
                //максимальное количество палуб
                int countOfdecks = 4;
                //массив кораблей -----  (ПОКА НИ НА ЧТО НЕ ВЛИЯЕТ И НЕ ИСПОЛЬЗУЕТСЯ)
                Ship[] ships = new Ship[10];
                //счетчик для массива корабелй
                int k = 0;
                //цикл для создания нужного количества кораблей
                //идем от самого большого
                for (int i = countOfdecks; i > 0; i--) {
                    //так чтобы количество палуб уменьшалось, а количество кораблей увеличивалось
                    for (int j = i - 1; j < countOfdecks; j++) {
                        //создаем экземпляр корабля с заданным количеством палуб
                        ships[k] = new Ship(i);
                        System.out.println("Размещаем " + i + "-палубный корабль.");
                        //размещаем его на поле через метод
                        postingShipUser(ships[k], fieldUser);
                        //увеличиваем счетчик
                        k++;
                        System.out.println("Вы успешно ввели координаты " + k + "-го корабля.");
                        Field.print(fieldUser);
                    }
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
                isOK = choiceFromRangeNumbers(1, 2) == 2;
                //если пользователь ввел 2, то есть НЕдоволен расстановкой, то обнуляаем результаты расстановки
                if (isOK) {
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

    private static void postingShipUser(Ship ship, Field fieldUser) {
        boolean validCoordinate = true;
        while (validCoordinate) {
            System.out.println("Введите первую координату вашего корабля:");
            //считываем введенную игроком ПЕРВУЮ координату для создания корабля и заносим ее в статическую переменную
            step = readUserStep();
            if (isValidCoordinate(step,fieldUser)) {
                //если зашли сюда, то координата валидная, значит больше этот цикл не нужен
                validCoordinate = false;

                //объявляем списки возможных положений корабля по направлениям
                ArrayList<Pair<Integer,Integer>> possibleStepsToNord = null;
                ArrayList<Pair<Integer,Integer>> possibleStepsToSouth = null;
                ArrayList<Pair<Integer,Integer>> possibleStepsToWest = null;
                ArrayList<Pair<Integer,Integer>> possibleStepsToEast = null;

                if (ship.decks > 1) {
                    //вводим логические переменные для определения возможных направлений
                    boolean nord = true;
                    boolean west = true;
                    boolean east = true;
                    boolean south = true;
                    //запускаем цикл для определения возможности пострения корабля во всех направлениях
                    for (int i = 1; i < ship.decks; i++) {
                        //определяем новую координату смещением на один вверх (север). Сейчас она не приаязана к полю
                        Pair<Integer, Integer> stepNord = new Pair<Integer, Integer>(step.getKey() + i, step.getValue());
                        //если она в рамках поля, проверяем возможные расположения корабля
                        if (validRange(stepNord)) {
                            //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                            nord = nord && isValidCoordinate(stepNord, fieldUser);
                        } else {
                            //если новая координата выходит
                            nord = false;
                        }
                        //определяем новую координату смещением на один влево (запад). Сейчас она не приаязана к полю
                        Pair<Integer, Integer> stepWest = new Pair<Integer, Integer>(step.getKey(), step.getValue() - i);
                        //если она в рамках поля, проверяем возможные расположения корабля
                        if (validRange(stepWest)) {
                            //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                            west = west && isValidCoordinate(stepWest, fieldUser);
                        } else {
                            //если новая координата выходит
                            west = false;
                        }
                        //определяем новую координату смещением на один вниз (юг). Сейчас она не приаязана к полю
                        Pair<Integer, Integer> stepSouth = new Pair<Integer, Integer>(step.getKey() - i, step.getValue());
                        //если она в рамках поля, проверяем возможные расположения корабля
                        if (validRange(stepSouth)) {
                            //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                            south = south && isValidCoordinate(stepSouth, fieldUser);
                        } else {
                            //если новая координата выходит
                            south = false;
                        }
                        //определяем новую координату смещением на один вниз (юг). Сейчас она не приаязана к полю
                        Pair<Integer, Integer> stepEast = new Pair<Integer, Integer>(step.getKey(), step.getValue() + i);
                        //если она в рамках поля, проверяем возможные расположения корабля
                        if (validRange(stepEast)) {
                            //определяем логическую переменную. Если хоть раз будет ЛОЖЬ, то итогом будет ЛОЖЬ
                            east = east && isValidCoordinate(stepEast, fieldUser);
                        } else {
                            //если новая координата выходит
                            east = false;
                        }
                    }
                    //на выходе из цикла мы получим массив логических переменных, которые показывают возможность
                    // расположения корабля в направлениях.


                    // Если они все ложные, то есть корабль нельзя разместить
                    // от указанной координаты в каком-либо направлении, то возвращаем пользователя к вводу координаты
                    if (nord || south || east || west) {
                        //создаем временный список для хранения достурных координат по каждому направлению
                        possibleStepsToNord = new ArrayList<>();
                        possibleStepsToSouth = new ArrayList<>();
                        possibleStepsToWest = new ArrayList<>();
                        possibleStepsToEast = new ArrayList<>();

                        //если норд ИСТИНА (корабль может быть размещен таким образом),
                        // то делаем вывод о допустимых ходах
                        if (nord) {
                            //создаем корабль
                            Ship shipToNord = new Ship(ship.decks);
                            //заполняем координаты корабля
                            for (int i = 0; i < ship.decks; i++) {
                                shipToNord.positions[i] = new Pair<>(step.getKey() + i, step.getValue());
                                possibleStepsToNord.add(shipToNord.positions[i]);
                            }
                            System.out.print("Вариант 1 (вниз от указанной координаты): ");
                            for (int i = 1; i < shipToNord.positions.length; i++) {
                                System.out.print(translateToHumanIndex(shipToNord.positions[i]) + " ");
                            }
                            System.out.println();
                        } else {
                            System.out.println("Вариант 1 (вниз от указанной координаты): НЕ ДОСТУПЕН");
                        }
                        //аналогично для другого направления (юг)
                        if (south) {
                            Ship shipToSouth = new Ship(ship.decks);
                            for (int i = 0; i < ship.decks; i++) {
                                shipToSouth.positions[i] = new Pair<>(step.getKey() - i, step.getValue());
                                possibleStepsToSouth.add(shipToSouth.positions[i]);
                            }
                            System.out.print("Вариант 2 (вверх от указанной координаты): ");
                            for (int i = 1; i < shipToSouth.positions.length; i++) {
                                System.out.print(translateToHumanIndex(shipToSouth.positions[i]) + " ");
                            }
                            System.out.println();
                        } else {
                            System.out.println("Вариант 2 (вверх от указанной координаты): НЕ ДОСТУПЕН");
                        }
                        //аналогично для другого направления (запад)
                        if (west) {
                            Ship shipToWest = new Ship(ship.decks);
                            for (int i = 0; i < ship.decks; i++) {
                                shipToWest.positions[i] = new Pair<>(step.getKey(), step.getValue() - i);
                                possibleStepsToWest.add(shipToWest.positions[i]);
                            }
                            System.out.print("Вариант 3 (влево от указанной координаты): ");
                            for (int i = 1; i < shipToWest.positions.length; i++) {
                                System.out.print(translateToHumanIndex(shipToWest.positions[i]) + " ");
                            }
                            System.out.println();
                        } else {
                            System.out.println("Вариант 3 (влево от указанной координаты): НЕ ДОСТУПЕН");
                        }
                        //аналогично для другого направления (восток)
                        if (east) {
                            Ship shipToEast = new Ship(ship.decks);
                            for (int i = 0; i < ship.decks; i++) {
                                shipToEast.positions[i] = new Pair<>(step.getKey(), step.getValue() + i);
                                possibleStepsToEast.add(shipToEast.positions[i]);
                            }
                            System.out.print("Вариант 4 (вправо от указанной координаты): ");
                            for (int i = 1; i < shipToEast.positions.length; i++) {
                                System.out.print(translateToHumanIndex(shipToEast.positions[i]) + " ");
                            }
                            System.out.println();
                        } else {
                            System.out.println("Вариант 4 (вправо от указанной координаты): НЕ ДОСТУПЕН");
                        }

                        System.out.println("Теперь выберете один из приведенных и допустимых вариантов (укажите цифру!)");

                        //в цикле, чтобы добиться допустимого выбора (только из тех номеров вариантов,
                        // которые были предложены (истинны)
                        while (true) {
                            //определяем выбор пользователя
                            int choicePosition = choiceFromRangeNumbers(1,4);
                            //если вариант норд допустим и выбрана 1, то
                            if (choicePosition == 1 && nord) {
                                //записываем координаты данного направления в список кораблей
                                choosenVariant(possibleStepsToNord, fieldUser);
                                // и выходим из цикла.
                                break;
                            }
                            //аналогично предыдущему
                            if (choicePosition == 2 && south) {
                                choosenVariant(possibleStepsToSouth, fieldUser);
                                break;
                            }
                            if (choicePosition == 3 && west) {
                                choosenVariant(possibleStepsToWest,fieldUser);
                                break;
                            }
                            if (choicePosition == 4 && east) {
                                choosenVariant(possibleStepsToEast, fieldUser);
                                break;
                            }
                            System.out.print("Нужно выбрать допустимый вариант! Попробуйте заново:");
                        }

                    } else {
                        System.out.println("Не получается разместить корабль каким-либо образом от " +
                                "указанной координаты");
                        validCoordinate = true;
                    }
                }
            } else {
                System.out.println("Сюда нельзя поставить корабль (либо уже стоит, либо слишком близко к другим.");
                System.out.println("Введите, пожалуйста, заново:");
                validCoordinate = true;
            }
        }
    }

    private static void choosenVariant(ArrayList<Pair<Integer, Integer>> possibleSteps, Field field) {
        //проходим по списку возможных координат выбранного направления
        for (Pair<Integer,Integer> coordinata : possibleSteps) {
            //заносим каждую координату в список кораблей
            field.ships.add(coordinata);
            //помечаем соответствюущую клетку статусом "корабль"
            field.cells[coordinata.getKey()][coordinata.getValue()].setStatus(2);
            //а также делаем данную клету видимой для печати
            field.cells[coordinata.getKey()][coordinata.getValue()].visible = true;
        }
    }

    //метод, проверяющий допустимость выбора клетки при условии наличия других кораблей
    private static boolean isValidCoordinate(Pair<Integer, Integer> coordinate, Field field) {
        //если в данной клетке есть корабль, то возвращаем ЛОЖЬ и выход из метода
        if (field.cells[coordinate.getKey()][coordinate.getValue()].getStatus() == 2) return false;
        //проходим циклом по списку клеток-кораблей, чтобы получить расстояние до каждой
        for (int i = 0; i < field.ships.size(); i++) {
            //временные переменные созданы для краткости записи условий
            //а - разница по столбцам, взятая по модулю
            int a = Math.abs(field.ships.get(i).getKey() - coordinate.getKey());
            //b - разница по строкам, взятая по модулю
            int b = Math.abs(field.ships.get(i).getValue() - coordinate.getValue());
            //проверяем условия близости координат введенных раннее кораблей с проверяемой
            if (a == 0 && b == 1) return false;
            if (a == 1 && b == 0) return false;
            if (a == 1 && b == 1) return false;
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
                System.out.printf("Нужно ввести цифры от %d до %d \n", beginInterval, endInterval);
            }
        } while (!(choiceValue >= beginInterval && choiceValue <= endInterval));
    return choiceValue;
    }
}
