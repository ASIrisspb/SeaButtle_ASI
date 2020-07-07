package ASIris;

public class Example {
    public static void main(String[] args) {
        int dimension = 10;
        int square = dimension*dimension;
        int countOfdecks = square/25;
        Ship[] ships = new Ship[(countOfdecks+1)*2];
        int k = 0;
        for (int i = countOfdecks; i > 0; i--) {
            for (int j = i - 1; j < countOfdecks; j++) {
//                if (k < ships.length) {
                    ships[k] = new Ship(i);
//                    System.out.println(ships[k].decks);
                System.out.println(k);
                    k++;
                }
//            }
        }
        for (Ship ship : ships) {
            System.out.println(ship);
        }
    }
}
