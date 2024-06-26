/* Name: Phan Manh Son
 ID: ITDSIU21116
 Purpose: Battle ship game which play by human vs computer
*/

package battleship;

public class Ship {

    private int xstart, ystart;
    private int xEnd, yEnd;
    private int dimension;

    public Ship(int xstart, int ystart, int xEnd, int yEnd, int dimension) {
        this.xstart = xstart;
        this.ystart = ystart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.dimension = dimension;
    }

    public int getDimension(){
        return dimension;
    }

    public int getXstart() {
        return xstart;
    }

    public int getYstart() {
        return ystart;
    }

    public int getXEnd() {
        return xEnd;
    }

    public int getYEnd() {
        return yEnd;
    }

    public boolean uguale(int x, int y) {
        if (x <= xEnd && x >= xstart && y <= yEnd && y >= ystart) {
            return true;
        }
        return false;

    }
    public boolean isSunk(Map map) {
        for (int i = getXstart(); i <= getXEnd(); i++) {
            for (int j = getYstart(); j <= getYEnd(); j++) {
                if (map.getCellStatus(i, j) != map.getHitConstant()) {
                    return false;
                }
            }
        }
        return true;
    }


    public String toString() {
        return xstart + "-" + ystart + "  " + xEnd + "-" + yEnd;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ship other = (Ship) obj;
        if (xEnd != other.xEnd)
            return false;
        if (xstart != other.xstart)
            return false;
        if (yEnd != other.yEnd)
            return false;
        if (ystart != other.ystart)
            return false;
        return true;
    }

}
