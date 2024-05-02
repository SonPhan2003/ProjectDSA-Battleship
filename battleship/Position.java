package battleship;

public class Position {
    private int coordX,coordY;

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public Position(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public Position(Position p){
        this.coordX = p.coordX;
        this.coordY = p.coordY;
    }

    public void move(char direction){
        switch(direction){
            case 'N':
                coordX--;
                break;
            case 'S':
                coordX++;
                break;
            case 'O':
                coordY--;
                break;
            case 'E':
                coordY++;
                break;
        }
    }

    public String toString(){
        char Y=(char)(coordY+65);
        return ""+(coordX+1)+" "+Y;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;
        if (coordX != other.coordX)
            return false;
        if (coordY != other.coordY)
            return false;
        return true;
    }

    public boolean outMap(){
        if(coordX>= Map.DIM_map||coordY >= Map.DIM_map||coordX<0||coordY<0)
            return true;
        return false;
    }


}
