package com.example.hp.battleship.Logic;

public class Tile {
    private Status tileStatus;
    private Ship ship;
    private boolean mIsFired, mIsSunk;

    public Tile()
    {
        this.tileStatus = Status.NONE;
        this.ship = null;
        setFired(false);
    }

    public enum Status {
        NONE,NONE_X,HIT,MISS,SHIP,SUNK
    }

    public boolean hitTile(){
        this.ship.hitShip();
        return this.ship.isSunk();
    }

    public void setShip(Ship ship){
        this.ship = ship;
        setTileStatus(Tile.Status.SHIP);
    }

    public void setTileStatus(Status tileStatus) {
        this.tileStatus = tileStatus;
    }

    public Status getTileStatus() {
        return tileStatus;
    }

    public Point[] getShipPoints(){
        return this.ship.getPointsOnBoard();
    }

    public boolean isSunk() {
        return mIsSunk;
    }

    public boolean isFired(){
        return mIsFired;
    }

    public void setFired(boolean fired){
        mIsFired = fired;
    }

    public void setmIsSunk(boolean mIsSunk) {
        this.mIsSunk = mIsSunk;
    }

}
