package com.example.hp.battleship.Logic;

import java.util.Random;

public class Board {
    public final static String HORIZONTAL = "horizontal";
    public final static String VERTICAL = "vertical";
    public final static int BOARD_SIZE = 10;
    public final static int FLEET_SIZE = 5;

    private Tile[][] mTiles;
    private Ship[] fleet;
    private int numOfShipsLeft;
    private Random random = new Random();
    private int numOfTilesLeft;

    public Board(int[] shipsInFleetSizes) {
        mTiles = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                mTiles[i][j] = new Tile();
            }
        }

        fleet = new Ship[FLEET_SIZE];
        for (int i = 0; i < FLEET_SIZE; i++) {
            this.fleet[i] = new Ship(shipsInFleetSizes[i]);
            placeShipOnBoard(this.fleet[i]);
        }
        setNumOfShipsLeft(FLEET_SIZE);
        numOfTilesLeft = getTotalBoardSize();
    }

    public void placeShipOnBoard(Ship ship) {
        boolean flag = true;
        int randomRow, randomCol, randomDirectionInt;
        String randomDirection;
        do {
            randomRow = random.nextInt(BOARD_SIZE);   //[0-9]
            randomCol = random.nextInt(BOARD_SIZE);
            randomDirectionInt = random.nextInt(2);        //[0-1]
            if (randomDirectionInt == 1)
                randomDirection = VERTICAL;
            else
                randomDirection = HORIZONTAL;
            flag = checkValidLocation(randomRow, randomCol, randomDirection, ship.getSize());
        } while (!flag);
        ship.setDirection(randomDirection);
        ship.setHeadPoint(randomRow, randomCol);
        place(randomRow, randomCol, ship);
    }

    public boolean checkValidLocation(int row, int col, String direction, int shipSize) {
        if (direction.compareTo(HORIZONTAL) == 0) {
            //check if the ship in the bounds of the board
            if (col + shipSize - 1 > BOARD_SIZE - 1)
                return false;

            for (int i = 0; i < shipSize; i++) {
                //check for collision
                if (mTiles[row][col + i].getTileStatus() != Tile.Status.NONE)
                    return false;
            }
        }

        //vertical case
        else {
            if (row + shipSize - 1 > BOARD_SIZE - 1)
                return false;

            for (int i = 0; i < shipSize; i++) {
                //check for collision
                if (mTiles[row + i][col].getTileStatus() != Tile.Status.NONE)
                    return false;
            }
        }
        return true;
    }

    public void place(int row, int col, Ship ship) {
        int size = ship.getSize();
        Point[] shipPoints = new Point[ship.getSize()];

        if (ship.getDirection().compareTo(HORIZONTAL) == 0) {
            for (int i = 0; i < size; i++) {
                mTiles[row][col + i].setShip(ship);
                shipPoints[i] = new Point(row, col + i);
                if (row < BOARD_SIZE - 1)
                    mTiles[row + 1][col + i].setTileStatus(Tile.Status.NONE_X);
                if (row > 0)
                    mTiles[row - 1][col + i].setTileStatus(Tile.Status.NONE_X);
            }

            if (col > 0)
                mTiles[row][col - 1].setTileStatus(Tile.Status.NONE_X);
            if (col + size - 1 < BOARD_SIZE - 1)
                mTiles[row][col + size].setTileStatus(Tile.Status.NONE_X);
        }
        //vertical
        else {
            for (int i = 0; i < size; i++) {
                mTiles[row + i][col].setShip(ship);
                shipPoints[i] = new Point(row + i, col);

                if (col < BOARD_SIZE - 1)
                    mTiles[row + i][col + 1].setTileStatus(Tile.Status.NONE_X);

                if (col > 0)
                    mTiles[row + i][col - 1].setTileStatus(Tile.Status.NONE_X);

            }
            if (row > 0)
                mTiles[row - 1][col].setTileStatus(Tile.Status.NONE_X);
            if (row + size - 1 < BOARD_SIZE - 1)
                mTiles[row + size][col].setTileStatus(Tile.Status.NONE_X);

        }
        ship.setPointsOnBoard(shipPoints);
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public Ship[] getFleet() {
        return fleet;
    }

    public int getNumOfShipsLeft() {
        return numOfShipsLeft;
    }

    public Tile getTile(int row, int col) {
        return mTiles[row][col];
    }


    public void setNumOfShipsLeft(int numOfShipsLeft) {
        this.numOfShipsLeft = numOfShipsLeft;
    }

    public void countDownOneTileFromBoard() {
        if (numOfTilesLeft > 0)
            numOfTilesLeft--;
    }

    public int getNumOfTilesLeft() {
        return numOfTilesLeft;
    }

    public int getTotalBoardSize() {
        return BOARD_SIZE * BOARD_SIZE;
    }

    public void ChangeAllCompShipsLocations() {
        clearTheBoard();
        int row, col, size;
        for (Ship ship : this.fleet) {
            if (ship.isSunk()) {
                row = ship.getHeadPoint().getRow();
                col = ship.getHeadPoint().getCol();
                size = ship.getSize();
                if (ship.getDirection().compareTo(HORIZONTAL) == 0) {

                    for (int i = 0; i < size; i++) {
                        if (row < BOARD_SIZE - 1)
                            mTiles[row + 1][col + i].setTileStatus(Tile.Status.NONE_X);
                        if (row > 0)
                            mTiles[row - 1][col + i].setTileStatus(Tile.Status.NONE_X);
                    }

                    if (col > 0)
                        mTiles[row][col - 1].setTileStatus(Tile.Status.NONE_X);
                    if (col + size - 1 < BOARD_SIZE - 1)
                        mTiles[row][col + size].setTileStatus(Tile.Status.NONE_X);

                } else {
                    for (int i = 0; i < size; i++) {

                        if (col < BOARD_SIZE - 1)
                            mTiles[row + i][col + 1].setTileStatus(Tile.Status.NONE_X);
                        if (col > 0)
                            mTiles[row + i][col - 1].setTileStatus(Tile.Status.NONE_X);

                    }
                    if (row > 0)
                        mTiles[row - 1][col].setTileStatus(Tile.Status.NONE_X);
                    if (row + size - 1 < BOARD_SIZE - 1)
                        mTiles[row + size][col].setTileStatus(Tile.Status.NONE_X);

                }
            }
        }

        for (Ship ship : this.fleet) {
            if (!ship.isSunk()) {
                ship.setHitPoints(ship.getSize());
                placeShipOnBoard(ship);
            }
        }
    }

    public void clearTheBoard() {
        Tile.Status status;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                status = mTiles[i][j].getTileStatus();
                if (status != Tile.Status.SUNK) {
                    mTiles[i][j] = new Tile();
                }
            }
        }
    }

}



