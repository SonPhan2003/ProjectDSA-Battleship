/* Name: Phan Manh Son
 ID: ITDSIU21116
 Purpose: Battle ship game which play by human vs computer
*/

package battleship;

import javafx.geometry.Pos;

import java.util.LinkedList;
import java.util.Random;

public class Computer {
    private LinkedList<Position> ListofPosition;
    private Random r;
    private int hits;
    private Position LastHit;
    private String Direction;
    private Map plMap; //player Map
    private Position FirstHit;// First position
    private LinkedList<EnemyShip> EnemyShips = new LinkedList<EnemyShip>(); // the remain kind of ship
    private LinkedList<String> PosibilityShoots = new LinkedList<String>();
    private double[][] probabilityMap;



    public Computer(Map EnemyMap, LinkedList<Ship> Ships) {
        ListofPosition = new LinkedList<Position>();
        this.plMap = EnemyMap;
        for (int i = 0; i < Map.DIM_map; i++) {
            for (int j = 0; j < Map.DIM_map; j++) {
                Position p = new Position(i, j);
                ListofPosition.add(p);
            }
        }
        r = new Random();
        hits = 0;
        for (int i = 0; i < Ships.size(); i++) { // get the ramin of ship size
            EnemyShip enemyShip = new EnemyShip();
            enemyShip.ship = new Ship(Ships.get(i).getXstart(),
                                      Ships.get(i).getYstart(),
                                      Ships.get(i).getXEnd(),
                                      Ships.get(i).getYEnd(),
                                      Ships.get(i).getDimension());
            enemyShip.dimension = Ships.get(i).getDimension();
            EnemyShips.add(enemyShip);
        }
    }

    private Position ShootRandom() {
        int shoot = r.nextInt(ListofPosition.size());
        Position p = ListofPosition.remove(shoot);
        return p;
    }

    private Position ShootWithProbability(int largestSize) {
        CalculateProbabilityMap();
        int largestShipSize = GetLargestRemainingShipSize();


        // Track the maximum probability and the position with that probability
        double maxProbability = -1;
        Position bestPosition = null;

        for (int i = 0; i < probabilityMap.length; i++) {
            for (int j = 0; j < probabilityMap[i].length; j++) {
                Position tempPosition = new Position(i,j);
                if (probabilityMap[i][j] > maxProbability && !plMap.IsHitted(tempPosition) && !plMap.WATER(tempPosition)) {
                    maxProbability = probabilityMap[i][j];
                    bestPosition = new Position(i, j);
                }
            }
        }

        // Remove the selected position from the list of available positions
        ListofPosition.remove(bestPosition);
        LastHit = bestPosition;
        return bestPosition;
    }

    private void CalculateProbabilityMap() {
        int mapDIM = Map.DIM_map;
        probabilityMap = new double[mapDIM][mapDIM];
        int largestSize = GetLargestRemainingShipSize();

        for (int i = 0; i < mapDIM; i++) {
            for (int j = 0; j < mapDIM; j++) {
                if (!plMap.IsHitted(new Position(i, j)) && !plMap.WATER(new Position(i, j))) {
                    probabilityMap[i][j] = calculatePositionProbability(i, j, largestSize);
                } else {
                    probabilityMap[i][j] = 0;
                }
            }
        }
    }

    private double calculatePositionProbability(int x, int y, int shipSize) {
        int ways = 0;

        // Horizontal placement
        for (int i = Math.max(0, x - shipSize + 1); i <= x && i + shipSize <= Map.DIM_map; i++) {
            boolean canPlace = true;
            for (int j = i; j < i + shipSize; j++) {
                if (plMap.IsHitted(new Position(j, y)) || plMap.WATER(new Position(j, y))) {
                    canPlace = false;
                    break;
                }
            }
            if (canPlace) ways++;
        }

        // Vertical placement
        for (int i = Math.max(0, y - shipSize + 1); i <= y && i + shipSize <= Map.DIM_map; i++) {
            boolean canPlace = true;
            for (int j = i; j < i + shipSize; j++) {
                if (plMap.IsHitted(new Position(x, j)) || plMap.WATER(new Position(x, j))) {
                    canPlace = false;
                    break;
                }
            }
            if (canPlace) ways++;
        }

        return ways;
    }


    private int WayShipCreated(int x, int y, int largestSize) {
        int ways = 0;
        int mapDIM = Map.DIM_map;

        if (largestSize == 4) {
            if (x <= 4) {
                ways += x;
            } else if (x <= 7) {
                ways += 4;
            } else {
                ways += 10 - x + 1;
            }

            if (y <= 4) {
                ways += y;
            } else if (y <= 7) {
                ways += 4;
            } else {
                ways += 10 - y + 1;
            }
        } else if (largestSize == 3) {
            if (x <= 2) {
                ways += x;
            } else if (x <= 8) {
                ways += 3;
            } else {
                ways += 11 - x;
            }

            if (y <= 2) {
                ways += y;
            } else if (y <= 8) {
                ways += 3;
            } else {
                ways += 11 - y;
            }
        } else if (largestSize == 2) {
            if (x < 2 || x == 10) {
                ways += 1;
            } else {
                ways += 2;
            }

            if (y < 2 || y == 10) {
                ways += 1;
            } else {
                ways += 2;
            }
        } else { // size = 1
            ways = 1;
        }

        return ways;
    }


    // Method to get the size of the largest remaining ship
    private int GetLargestRemainingShipSize() {
        int largestSize = 0;
        for (EnemyShip enemyShip : EnemyShips) {
            if (enemyShip.dimension > largestSize) {
                largestSize = enemyShip.dimension;
            }
        }
        return largestSize;
    }

    private void removeSunkShip(int dimension) {
        for (int i = 0; i < EnemyShips.size(); i++) {
            if (EnemyShips.get(i).dimension == dimension) {
                EnemyShips.remove(i);
                break;
            }
        }
    }

    private int DefineDirection()
        {
            if (PosibilityShoots.isEmpty()) {
                throw new IllegalStateException("PosibilityShoots list is empty.");
            }

            int Direction = r.nextInt(PosibilityShoots.size());
            return Direction;
        }

    private void ShootPosibility()
    {
        PosibilityShoots = new LinkedList<String>();

        if (FirstHit.getCoordX() != 0) {
            PosibilityShoots.add("N");
        }
        if (FirstHit.getCoordX() != Map.DIM_map - 1) {
            PosibilityShoots.add("S");
        }
        if (FirstHit.getCoordY() != 0) {
            PosibilityShoots.add("O");
        }
        if (FirstHit.getCoordY() != Map.DIM_map - 1) {
            PosibilityShoots.add("E");
        }
    }

    private Position GetShootPosition() {
        // Get the size of the largest remaining ship
        int largestSize = GetLargestRemainingShipSize();

        // If the largest remaining ship size is 1, use the shootRandom method
        if (largestSize == 1) {
            // Call the shootRandom method and return the position
            return ShootRandom(); // LastHit is set in the ShootRandom method
        }

        if (hits == 0) {
            //Bắn dò
            Position shootPosition = ShootWithProbability(largestSize);
            return shootPosition;
        }

        if (hits == 1) {
            return ShootTarget1();
        }

        if(hits >= 2){
            if (plMap.WATER(LastHit) || LastHit.outMap()){
                invertDirection();
            }
            Position LastHit = ShootTarget2();
            return LastHit;
        }

        return null;
    }

    public Report myTurn() {
        //Vị trí muốn bắn
        Position shootPosition = GetShootPosition();

        //Check bắn trúng hay ko?
        boolean isHit = plMap.Hit(shootPosition);

        //Check tàu đã chìm hay ko?
        boolean isSunk = false;

        Ship sunkship;

        if (isHit) // nếu đúng
        {
            sunkship = plMap.Sunk(shootPosition);
            if (sunkship != null)
            {
                isSunk = true;
                //rep.setSunkShip(true);
                RemoveBorders(sunkship);
                hits = 0;
                Direction = null; // Direction = 0;
                FirstHit = null;

                // Remove the sunk ship from the list of enemy ships
                removeSunkShip(sunkship.getDimension());

            }else {

                hits++;

                if (hits == 1) //Check có phải phát bắn dính đầu tiên
                {
                    FirstHit = shootPosition;
                    ShootPosibility();
                } else if (hits > 1) {
                    // Xét Logic giữa: FirstHit, shootPosition
                    // Gán data cho Direction
                    DefineDirection();
                }
            }
        }
        CalculateProbabilityMap();
        Report rep = new Report(shootPosition, isHit, isSunk);

        return rep;
    }


    private Position ShootTarget1() { // if the shoot is correct, shoot random dir of the possibility list
        boolean error = true;
        Position p = null;
        do {
            int shoot = r.nextInt(PosibilityShoots.size());
            String dir = PosibilityShoots.remove(shoot); //direction
            p = new Position(FirstHit);
            p.move(dir.charAt(0));
            Direction = dir;
            if (!plMap.WATER(p)) {
                ListofPosition.remove(p);
                error = false;
            }
        } while (error);// make sure to not hit

        LastHit = p;
        return p;
    }

    private Position ShootTarget2() {
        boolean hit = false;
        Position p = new Position(LastHit);
        do {
            p.move(Direction.charAt(0));

            if (p.outMap() || plMap.WATER(p)) {
                invertDirection();
            } else {
                if (!plMap.IsHitted(p)) {
                    hit = true;
                }

            }
        } while (!hit);
        ListofPosition.remove(p);
        LastHit = p;
        return p;
    }


    private void RemoveBorders(Ship sunkship) {
        int Xstart = sunkship.getXstart();
        int XEnd = sunkship.getXEnd();
        int Ystart = sunkship.getYstart();
        int Yend = sunkship.getYEnd();
        if (Xstart == XEnd) {// horizontal
            if (Ystart != 0) {
                Position p = new Position(Xstart, Ystart - 1);
                if (!plMap.WATER(p)) {
                    ListofPosition.remove(p);
                    plMap.setWATER(p);

                }
            }
            if (Yend != Map.DIM_map - 1) {
                Position p = new Position(Xstart, Yend + 1);
                if (!plMap.WATER(p)) {
                    ListofPosition.remove(p);
                    plMap.setWATER(p);
                }
            }
            if (Xstart != 0) {
                for (int i = 0; i <= Yend - Ystart; i++) {
                    Position p = new Position(Xstart - 1, Ystart + i);
                    if (!plMap.WATER(p)) {
                        ListofPosition.remove(p);
                        plMap.setWATER(p);
                    }
                }

            }
            if (Xstart != Map.DIM_map - 1) {
                for (int i = 0; i <= Yend - Ystart; i++) {
                    Position p = new Position(Xstart + 1, Ystart + i);
                    if (!plMap.WATER(p)) {
                        ListofPosition.remove(p);
                        plMap.setWATER(p);
                    }
                }
            }
        } else {// vertical
            if (Xstart != 0) {
                Position p = new Position(Xstart - 1, Ystart);
                if (!plMap.WATER(p)) {
                    ListofPosition.remove(p);
                    plMap.setWATER(p);
                }
            }
            if (XEnd != Map.DIM_map - 1) {
                Position p = new Position(XEnd + 1, Ystart);
                if (!plMap.WATER(p)) {
                    ListofPosition.remove(p);
                    plMap.setWATER(p);
                }
            }
            if (Ystart != 0) {
                for (int i = 0; i <= XEnd - Xstart; i++) {
                    Position p = new Position(Xstart + i, Ystart - 1);
                    if (!plMap.WATER(p)) {
                        ListofPosition.remove(p);
                        plMap.setWATER(p);
                    }
                }

            }
            if (Yend != Map.DIM_map - 1) {
                for (int i = 0; i <= XEnd - Xstart; i++) {
                    Position p = new Position(Xstart + i, Ystart + 1);
                    if (!plMap.WATER(p)) {
                        ListofPosition.remove(p);
                        plMap.setWATER(p);
                    }
                }
            }
        }
    }


    private void invertDirection() {
        if (Direction.equals("N")) {
            Direction = "S";
        } else if (Direction.equals("S")) {
            Direction = "N";
        } else if (Direction.equals("E")) {
            Direction = "O";
        } else if (Direction.equals("O")) {
            Direction = "E";
        }
    }

}
