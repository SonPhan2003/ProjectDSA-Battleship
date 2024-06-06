package battleship;

import java.util.LinkedList;
import java.util.Random;

public class Map {
    public static final int DIM_map = 10;  // dimension
    private final char EMPTY = '0', SHIP = 'X', WATER = 'A', HIT = 'C';
    private char[][] map;
    private LinkedList<Ship> ShipList;
    // make new 4 small grids from big map
    private int grid1_x1, grid1_y1, grid1_x2, grid1_y2;
    private int grid2_x1, grid2_y1, grid2_x2, grid2_y2;
    private int grid3_x1, grid3_y1, grid3_x2, grid3_y2;
    private int grid4_x1, grid4_y1, grid4_x2, grid4_y2;

    public Map() {
        ShipList = new LinkedList<Ship>();
        map = new char[DIM_map][DIM_map];
        for (int i = 0; i < DIM_map; i++)
            for (int j = 0; j < DIM_map; j++)
                map[i][j] = EMPTY;

        // Divide the map into 4 grids
        divideMapIntoGrids();
    }

    private void divideMapIntoGrids() {
        // Calculate the grid coordinates
        int halfWidth = DIM_map / 2;
        int halfHeight = DIM_map / 2;

        // Grid 1: A to E as row, 1 to 5 as column
        grid1_x1 = 0;
        grid1_y1 = 0;
        grid1_x2 = halfWidth;
        grid1_y2 = halfHeight;

        // Grid 2: F to L, 1 to 5
        grid2_x1 = 0;
        grid2_y1 = halfHeight;
        grid2_x2 = halfWidth;
        grid2_y2 = DIM_map;

        // Grid 3: A to E, 6 to 10
        grid3_x1 = halfWidth;
        grid3_y1 = 0;
        grid3_x2 = DIM_map;
        grid3_y2 = halfHeight;

        // Grid 4: F to L, 6 to 10
        grid4_x1 = halfWidth;
        grid4_y1 = halfHeight;
        grid4_x2 = DIM_map;
        grid4_y2 = DIM_map;
    }

    public int getGrid1_x1() { return grid1_x1; }
    public int getGrid1_y1() { return grid1_y1; }
    public int getGrid1_x2() { return grid1_x2; }
    public int getGrid1_y2() { return grid1_y2; }

    public int getGrid2_x1() { return grid2_x1; }
    public int getGrid2_y1() { return grid2_y1; }
    public int getGrid2_x2() { return grid2_x2; }
    public int getGrid2_y2() { return grid2_y2; }

    public int getGrid3_x1() { return grid3_x1; }
    public int getGrid3_y1() { return grid3_y1; }
    public int getGrid3_x2() { return grid3_x2; }
    public int getGrid3_y2() { return grid3_y2; }

    public int getGrid4_x1() { return grid4_x1; }
    public int getGrid4_y1() { return grid4_y1; }
    public int getGrid4_x2() { return grid4_x2; }
    public int getGrid4_y2() { return grid4_y2; }


    public void markBlockedCellsAroundSunkShip(Ship ship) {
        int startX = ship.getXstart();
        int startY = ship.getYstart();
        int endX = ship.getXEnd();
        int endY = ship.getYEnd();

        // Mark cells around the ship as WATER
        for (int i = startX - 1; i <= endX + 1; i++) {
            for (int j = startY - 1; j <= endY + 1; j++) {
                if (i >= 0 && i < DIM_map && j >= 0 && j < DIM_map) {
                    if (map[i][j] != HIT) {  // Skip cells that are already hit
                        map[i][j] = WATER;
                    }
                }
            }
        }
    }

    private boolean[][] getBlockedCells() {
        boolean[][] blockedCells = new boolean[Map.DIM_map][Map.DIM_map];

        for (Ship ship : ShipList) {
            if (ship.isSunk(this)) { // Pass the Map object (this) to isSunk method
                markBlockedCellsAroundSunkShip(ship);
            }
        }

        return blockedCells;
    }

    public char getCellStatus(int x, int y) {
        return map[x][y];
    }

    public char getHitConstant() {
        return HIT;
    }

    public void fillMapRandom() {
        clear();
        Random r = new Random();
        insertRandomShip(r, 4);
        insertRandomShip(r, 3);
        insertRandomShip(r, 3);
        insertRandomShip(r, 2);
        insertRandomShip(r, 2);
        insertRandomShip(r, 2);
        insertRandomShip(r, 1);
        insertRandomShip(r, 1);
        insertRandomShip(r, 1);
        insertRandomShip(r, 1);
    }

    private void clear() {
        for (int i = 0; i < DIM_map; i++)
            for (int j = 0; j < DIM_map; j++)
                map[i][j] = EMPTY;

    }

    public boolean insertShip(int x, int y, int dim, int dir) {
        if (dir == 1 && x + dim > DIM_map) {
            return false;
        } // vertical
        if (dir == 0 && y + dim > DIM_map) {
            return false;
        } // horizontal
        boolean inserted;

        if (dir == 0)
            inserted = verifyHorizontal(x, y, dim);
        else
            inserted = verifyVertical(x, y, dim);

        if (!inserted)
            return false;
        if (dir == 0) {
            Ship n = new Ship(x, y, x, y + dim - 1, dim);
            ShipList.add(n);
        } else {
            Ship n = new Ship(x, y, x + dim - 1, y, dim);
            ShipList.add(n);
        }
        for (int i = 0; i < dim; i++) {
            if (dir == 0) {
                map[x][y + i] = SHIP;
            } else
                map[x + i][y] = SHIP;
        }
        return true;
    }

    public int[] insertRandomShip(Random random, int dimension) {
        boolean inserted;
        int[] data = new int[4];
        int direction, row, column;
        do {
            inserted = true;
            direction = random.nextInt(2); // 0 = Horizontal, 1 = Vertical
            if (direction == 0) {
                column = random.nextInt(DIM_map - dimension + 1);
                row = random.nextInt(DIM_map);
            } else {
                column = random.nextInt(DIM_map);
                row = random.nextInt(DIM_map - dimension + 1);
            }
            if (direction == 0)
                inserted = verifyHorizontal(row, column, dimension);
            else
                inserted = verifyVertical(row, column, dimension);
        } while (!inserted);
        if (direction == 0) {
            Ship n = new Ship(row, column, row, column + dimension - 1, dimension);
            ShipList.add(n);
        } else {
            Ship n = new Ship(row, column, row + dimension - 1, column, dimension);
            ShipList.add(n);
        }
        for (int i = 0; i < dimension; i++) {
            if (direction == 0) {
                map[row][column + i] = SHIP;
            } else
                map[row + i][column] = SHIP;
        }
        data[0] = row;
        data[1] = column;
        data[2] = dimension;
        data[3] = direction;
        return data;
    }

    public boolean verifyVertical(int row, int column, int dimension) {
        if (row != 0)
            if (map[row - 1][column] == SHIP)
                return false;
        if (row != DIM_map - dimension)// la SHIP finisce sul bordo
            if (map[row + dimension][column] == SHIP)
                return false;
        for (int i = 0; i < dimension; i++) {
            if (column != 0)
                if (map[row + i][column - 1] == SHIP)
                    return false;
            if (column != DIM_map - 1)
                if (map[row + i][column + 1] == SHIP)
                    return false;
            if (map[row + i][column] == SHIP)
                return false;
        }
        return true;
    }

    public boolean verifyHorizontal(int row, int column, int dimension) {
        if (column != 0)
            if (map[row][column - 1] == SHIP)
                return false;
        if (column != DIM_map - dimension)
            if (map[row][column + dimension] == SHIP)
                return false;
        for (int i = 0; i < dimension; i++) {
            if (row != 0)
                if (map[row - 1][column + i] == SHIP)
                    return false;
            if (row != DIM_map - 1)
                if (map[row + 1][column + i] == SHIP)
                    return false;
            if (map[row][column + i] == SHIP)
                return false;
        }
        return true;
    }

    public boolean Hit(Position p) {
        int row = p.getCoordX();
        int column = p.getCoordY();
        if (map[row][column] == SHIP) {
            map[row][column] = HIT;
            return true;
        }
        map[row][column] = WATER;
        return false;
    }

    public Ship Sunk(Position p) {
        int row = p.getCoordX();
        int col = p.getCoordY();
        Ship SHIP = null;
        for (int i = 0; i < ShipList.size(); i++) {
            if (ShipList.get(i).uguale(row, col)) {
                SHIP = ShipList.get(i);
                break;
            }
        }
        for (int i = SHIP.getXstart(); i <= SHIP.getXEnd(); i++) {
            for (int j = SHIP.getYstart(); j <= SHIP.getYEnd(); j++) {
                if (map[i][j] != HIT) {
                    return null;
                }
            }
        }
        ShipList.remove(SHIP);
        return SHIP;
    }

    public void setWATER(Position p) {
        map[p.getCoordX()][p.getCoordY()] = WATER;
    }

    public boolean WATER(Position p) {
        return map[p.getCoordX()][p.getCoordY()] == WATER;
    }

    public boolean IsEmpty(Position p){
        return map[p.getCoordX()][p.getCoordY()] == EMPTY;
    }

    public boolean IsHitted(Position p) {
        return map[p.getCoordX()][p.getCoordY()] == HIT;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DIM_map; i++) {
            for (int j = 0; j < DIM_map; j++) {
                sb.append(map[i][j] + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // a setter method that allows receiving a list of ships belonging to the opponent (adversary)
    public void setAdvShips(LinkedList<int[]> advShips) {
        ShipList.clear();
        for (int[] a : advShips) {
            insertShip(a[0], a[1], a[2], a[3]);
            System.out.println("sto inserendo" + a[0] + a[1] + a[2] + a[3]);
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++)
                System.out.print(map[i][j]);
            System.out.println("");
        }
    }

    public LinkedList<Ship> GetShipList()
    {
        return ShipList;
    }
}
