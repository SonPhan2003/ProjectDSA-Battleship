/* Name: Phan Manh Son
 ID: ITDSIU21116
 Purpose: Battle ship game which play by human vs computer
*/
package battleship;

public class Report {
    private Position p;
    private boolean hit;
    private boolean sunkship;

    public Report(){
    }

    public Report(Position p, boolean hit, boolean sunkship) {
        this.p = p;
        this.hit = hit;
        this.sunkship = sunkship;
    }
    public Position getP() {
        return p;
    }
    public void setP(Position p) {
        this.p = p;
    }
    public boolean isHit() {
        return hit;
    }
    public void setHit(boolean hit) {
        this.hit = hit;
    }
    public boolean isSunkShip() {
        return sunkship;
    }
    public void setSunkShip(boolean sunkship) {
        this.sunkship = sunkship;
    }
    public String toString(){
        return "coordinate:"+p+" hit:"+hit+" sunk ship:"+sunkship;
    }
}
