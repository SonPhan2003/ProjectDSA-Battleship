package battleship;

import javafx.geometry.Pos;

import java.util.LinkedList;
import java.util.Random;

public class Computer {
    private LinkedList<Position> ListofPosition;
    private Random r;
    private int hits;
    private LinkedList<String> possibility;
    private Position LastHit;
    private String direction;
    private Map plMap; //player Map
    private Position FirstHit;// First position
    private LinkedList<EnemyShip> EnemyShips = new LinkedList<EnemyShip>(); // the remain kind of ship


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
            EnemyShip enemy = new EnemyShip();
            enemy.ship = new Ship(Ships.get(i).getXstart(),
                                  Ships.get(i).getYstart(),
                                  Ships.get(i).getXEnd(),
                                  Ships.get(i).getYEnd(),
                                  Ships.get(i).getDimension());
            enemy.dimension = Ships.get(i).getDimension();
            EnemyShips.add(enemy);
        }
    }

    public Report myTurn() {
        Report rep = new Report();
        if (hits == 0) {
            Position target = shootWithProbability();
            boolean hit = plMap.Hit(target);
            rep.setP(target);
            rep.setHit(hit);
            Ship sunkship;
            if (hit) {
                hits++;
                sunkship = plMap.Sunk(target);
                if (sunkship != null) {
                    rep.setSunkShip(true);
                    RemoveBorders(sunkship);
                    hits = 0;
                    direction = null;
                } else {
                    FirstHit = target;
                    possibility = new LinkedList<String>();
                    InitializeList();
                }
            }
            return rep;
        } // Shoot random
        if (hits == 1) {
            boolean hit = shootTarget1();
            Ship sunkship;
            rep.setP(LastHit);
            rep.setHit(hit);
            rep.setSunkShip(false);
            if (hit) {
                hits++;
                possibility = null;
                sunkship = plMap.Sunk(LastHit);
                if (sunkship != null) {
                    rep.setSunkShip(true);
                    RemoveBorders(sunkship);
                    hits = 0;
                    direction = null;
                }
            }
            return rep;
        }
        if (hits >= 2) {
            boolean hit = shootTarget2();
            Ship sunkship;
            rep.setP(LastHit);
            rep.setHit(hit);
            rep.setSunkShip(false);
            if (hit) {
                hits++;
                sunkship = plMap.Sunk(LastHit);
                if (sunkship != null) {
                    rep.setSunkShip(true);
                    RemoveBorders(sunkship);
                    hits = 0;
                    direction = null;
                }
            } else {
                invertDirection();
            }
            return rep;
        }
        return null;
    }

    private boolean ShootRandom() {
        int shoot = r.nextInt(ListofPosition.size());
        Position p = ListofPosition.remove(shoot);
        LastHit = p;
        boolean hit = plMap.Hit(p);
        return hit;
    }

    private Position shootWithProbability() {
        double[][] probabilityMap = calculateProbabilityMap();

        // Track the maximum probability and the position with that probability
        double maxProbability = -1;
        Position bestPosition = null;

        // Determine the size of the largest remaining ship
        int largestShipSize = getLargestRemainingShipSize();

        // Iterate over the probability map
        for (int x = 0; x < Map.DIM_map; x++) {
            for (int y = 0; y < Map.DIM_map; y++) {
                // Check if the cell is empty and if its probability is greater than the maximum
                if (plMap.getCellStatus(x, y) == '0' && probabilityMap[x][y] > maxProbability) {
                    // If the largest remaining ship can fit horizontally or vertically, prioritize this cell
                    if (canPlaceShip(x, y, largestShipSize, true) || canPlaceShip(x, y, largestShipSize, false)) {
                        maxProbability = probabilityMap[x][y];
                        bestPosition = new Position(x, y);
                    }
                }
            }
        }

        // If no suitable position was found, shoot at the cell with the highest probability
        if (bestPosition == null) {
            for (int x = 0; x < Map.DIM_map; x++) {
                for (int y = 0; y < Map.DIM_map; y++) {
                    if (plMap.getCellStatus(x, y) == '0' && probabilityMap[x][y] > maxProbability) {
                        maxProbability = probabilityMap[x][y];
                        bestPosition = new Position(x, y);
                    }
                }
            }
        }

        // Remove the selected position from the list of possible positions
        if (bestPosition != null) {
            ListofPosition.remove(bestPosition);
        }

        // Return the best position to shoot
        LastHit = bestPosition;
        return bestPosition;
    }

    // Method to get the size of the largest remaining ship
    private int getLargestRemainingShipSize() {
        int largestSize = 0;
        for (EnemyShip enemyShip : EnemyShips) {
            if (enemyShip.dimension > largestSize) {
                largestSize = enemyShip.dimension;
            }
        }
        return largestSize;
    }


    // Calculate the probability map for the entire grid
    private double[][] calculateProbabilityMap() {
        double[][] probabilityMap = new double[Map.DIM_map][Map.DIM_map];

        // Calculate probabilities for each grid using public getter methods from plMap
        calculateGridProbability(probabilityMap, plMap.getGrid1_x1(), plMap.getGrid1_y1(), plMap.getGrid1_x2(), plMap.getGrid1_y2());
        calculateGridProbability(probabilityMap, plMap.getGrid2_x1(), plMap.getGrid2_y1(), plMap.getGrid2_x2(), plMap.getGrid2_y2());
        calculateGridProbability(probabilityMap, plMap.getGrid3_x1(), plMap.getGrid3_y1(), plMap.getGrid3_x2(), plMap.getGrid3_y2());
        calculateGridProbability(probabilityMap, plMap.getGrid4_x1(), plMap.getGrid4_y1(), plMap.getGrid4_x2(), plMap.getGrid4_y2());

        return probabilityMap;
    }



    private void calculateGridProbability(double[][] probabilityMap, int x1, int y1, int x2, int y2) {
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                probabilityMap[x][y] = calculateCellProbability(x, y);
            }
        }
    }

    // Calculate the probability for a single cell
    private double calculateCellProbability(int x, int y) {
        if (plMap.getCellStatus(x, y) != '0') {
            return 0.0; // Cell is not empty, so it cannot be a ship
        }

        double probability = 0.0;

        for (EnemyShip enemyShip : EnemyShips) {
            int shipSize = enemyShip.dimension;
            // Check horizontal placement
            if (canPlaceShip(x, y, shipSize, true)) {
                probability += 1.0;
            }
            // Check vertical placement
            if (canPlaceShip(x, y, shipSize, false)) {
                probability += 1.0;
            }
        }

        return probability;
    }


    // Check if a ship can be placed at the specified cell
    private boolean canPlaceShip(int x, int y, int shipSize, boolean horizontal) {
        if (horizontal) {
            if (y + shipSize > Map.DIM_map) {
                return false;
            }
            for (int i = 0; i < shipSize; i++) {
                if (plMap.getCellStatus(x, y + i) != '0') {
                    return false;
                }
            }
        } else {
            if (x + shipSize > Map.DIM_map) {
                return false;
            }
            for (int i = 0; i < shipSize; i++) {
                if (plMap.getCellStatus(x + i, y) != '0') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean shootTarget1() { // if the shoot is correct, shoot random dir of the possibility list
        boolean error = true;
        Position p = null;
        do {
            int shoot = r.nextInt(possibility.size());
            String dir = possibility.remove(shoot); //direction
            p = new Position(FirstHit);
            p.move(dir.charAt(0));
            direction = dir;
            if (!plMap.WATER(p)) {
                ListofPosition.remove(p);
                error = false;
            }
        } while (error);// make sure to not hit

        LastHit = p;
        return plMap.Hit(p);
    }

    private boolean shootTarget2() {
        boolean hit = false;
        Position p = new Position(LastHit);
        do {
            p.move(direction.charAt(0));

            if (p.outMap() || plMap.WATER(p)) {
                invertDirection();
            } else {
                if (!plMap.HIT(p)) {
                    hit = true;
                }

            }
        } while (!hit);
        ListofPosition.remove(p);
        LastHit = p;
        return plMap.Hit(p);
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

    private void InitializeList() {
        if (LastHit.getCoordX() != 0) {
            possibility.add("N");
        }
        if (LastHit.getCoordX() != Map.DIM_map - 1) {
            possibility.add("S");
        }
        if (LastHit.getCoordY() != 0) {
            possibility.add("O");
        }
        if (LastHit.getCoordY() != Map.DIM_map - 1) {
            possibility.add("E");
        }
    }

    private void invertDirection() {
        if (direction.equals("N")) {
            direction = "S";
        } else if (direction.equals("S")) {
            direction = "N";
        } else if (direction.equals("E")) {
            direction = "O";
        } else if (direction.equals("O")) {
            direction = "E";
        }
    }

}
