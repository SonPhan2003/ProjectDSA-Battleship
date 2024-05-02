package battleship;

import javafx.geometry.Pos;

import java.util.LinkedList;
import java.util.Random;

public class Computer {
    private LinkedList<Position> list;
    private Random r;
    private int hits;
    private LinkedList<String> possibility;
    private Position LastHit;
    private String direction;
    private Map plMap; //player Map
    private Position FirstHit;// First position


    public Computer(Map EnemyMap) {
        list = new LinkedList<Position>();
        this.plMap = EnemyMap;
        for (int i = 0; i < Map.DIM_map; i++) {
            for (int j = 0; j < Map.DIM_map; j++) {
                Position p = new Position(i, j);
                list.add(p);
            }
        }
        r = new Random();
        hits = 0;
    }

    public Report myTurn() {
        Report rep = new Report();
        if (hits == 0) {
            boolean hit = ShootRandom();
            rep.setP(LastHit);
            rep.setHit(hit);
            Ship sunkship;
            if (hit) {
                hits++;
                sunkship = plMap.Sunk(LastHit);
                if (sunkship != null) {
                    rep.setSunkShip(true);
                    RemoveBorders(sunkship);
                    hits = 0;
                    direction = null;
                } else {
                    FirstHit = LastHit;
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
        int shoot = r.nextInt(list.size());
        Position p = list.remove(shoot);
        LastHit = p;
        boolean hit = plMap.Hit(p);
        return hit;
    }

    private boolean shootTarget1() {
        boolean error = true;
        Position p = null;
        do {
            int shoot = r.nextInt(possibility.size());
            String dir = possibility.remove(shoot); //direction
            p = new Position(FirstHit);
            p.move(dir.charAt(0));
            direction = dir;
            if (!plMap.WATER(p)) {
                list.remove(p);
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
        list.remove(p);
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
                    list.remove(p);
                    plMap.setWATER(p);

                }
            }
            if (Yend != Map.DIM_map - 1) {
                Position p = new Position(Xstart, Yend + 1);
                if (!plMap.WATER(p)) {
                    list.remove(p);
                    plMap.setWATER(p);
                }
            }
            if (Xstart != 0) {
                for (int i = 0; i <= Yend - Ystart; i++) {
                    Position p = new Position(Xstart - 1, Ystart + i);
                    if (!plMap.WATER(p)) {
                        list.remove(p);
                        plMap.setWATER(p);
                    }
                }

            }
            if (Xstart != Map.DIM_map - 1) {
                for (int i = 0; i <= Yend - Ystart; i++) {
                    Position p = new Position(Xstart + 1, Ystart + i);
                    if (!plMap.WATER(p)) {
                        list.remove(p);
                        plMap.setWATER(p);
                    }
                }
            }
        } else {// vertical
            if (Xstart != 0) {
                Position p = new Position(Xstart - 1, Ystart);
                if (!plMap.WATER(p)) {
                    list.remove(p);
                    plMap.setWATER(p);
                }
            }
            if (XEnd != Map.DIM_map - 1) {
                Position p = new Position(XEnd + 1, Ystart);
                if (!plMap.WATER(p)) {
                    list.remove(p);
                    plMap.setWATER(p);
                }
            }
            if (Ystart != 0) {
                for (int i = 0; i <= XEnd - Xstart; i++) {
                    Position p = new Position(Xstart + i, Ystart - 1);
                    if (!plMap.WATER(p)) {
                        list.remove(p);
                        plMap.setWATER(p);
                    }
                }

            }
            if (Yend != Map.DIM_map - 1) {
                for (int i = 0; i <= XEnd - Xstart; i++) {
                    Position p = new Position(Xstart + i, Ystart + 1);
                    if (!plMap.WATER(p)) {
                        list.remove(p);
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
