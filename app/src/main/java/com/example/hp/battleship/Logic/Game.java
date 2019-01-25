package com.example.hp.battleship.Logic;

import java.util.Random;

public class Game {
    final static int[] LARGE_FLEET = {3, 4, 4, 5, 5};
    final static int[] MEDIUM_FLEET = {2, 3, 3, 4, 4};
    final static int[] SMALL_FLEET = {1, 2, 2, 3, 3};

    private int difficulty;
    private Board playerBoard;
    private Board computerBoard;
    private boolean playerTurn;
    private Random random = new Random();

    private boolean huntMode;
    private Point computerCurrentPoint;
    private Point randomPoint;
    private Point calculatedPoint;
    private int computerDirection;
    private Point computerLastHitPoint;


    public Game(int difficulty) {
        switch (difficulty) {
            case 0:
                this.playerBoard = new Board(LARGE_FLEET);
                this.computerBoard = new Board(LARGE_FLEET);
                break;
            case 1:
                this.playerBoard = new Board(MEDIUM_FLEET);
                this.computerBoard = new Board(MEDIUM_FLEET);
                break;
            case 2:
                this.playerBoard = new Board(SMALL_FLEET);
                this.computerBoard = new Board(SMALL_FLEET);
                break;
        }
        this.difficulty = difficulty;
        randomPoint = new Point();
        calculatedPoint = new Point();
        setPlayerTurn(true);
        setHuntMode(false);
        computerDirection = 1;
        computerCurrentPoint = getRandomPoint();
    }

    public int playTile(Point point, Board board) {
        boolean sunk;
        Tile.Status tileStatus = board.getTile(point.getRow(), point.getCol()).getTileStatus();
        //in case player MISS
        if (tileStatus == Tile.Status.NONE || tileStatus == Tile.Status.NONE_X) {
            board.getTile(point.getRow(), point.getCol()).setTileStatus(Tile.Status.MISS);
            board.countDownOneTileFromBoard();
            toggleTurn();
            return 0;
        }
        //in case player HIT
        else if (tileStatus == Tile.Status.SHIP) {
            sunk = board.getTile(point.getRow(), point.getCol()).hitTile();

            if (sunk) {
                Point[] p = board.getTile(point.getRow(), point.getCol()).getShipPoints();
                for (int i = 0; i < p.length; i++)
                    board.getTile(p[i].getRow(), p[i].getCol()).setTileStatus(Tile.Status.SUNK);
                board.setNumOfShipsLeft(board.getNumOfShipsLeft() - 1);
                board.countDownOneTileFromBoard();
                toggleTurn();
                return 2;
            } else {
                board.getTile(point.getRow(), point.getCol()).setTileStatus(Tile.Status.HIT);
                board.countDownOneTileFromBoard();
                toggleTurn();
                return 1;
            }
        } else
            return -1;
    }

    public void toggleTurn() {
        playerTurn = !playerTurn;
    }

    public int computerPlay() {
        int theState;
        theState = playTile(computerCurrentPoint, playerBoard);
        //hit a ship - now trying to sink it
        if (huntMode) {
            //case HIT
            if (theState == 1) {
                computerCurrentPoint = getCalculatedPoint(computerCurrentPoint);
            }

            //case MISS
            else if (theState == 0) {
                nextDirection();
                computerCurrentPoint = getCalculatedPoint(computerLastHitPoint);
            }

            //case SUNK
            else {
                computerCurrentPoint = getRandomPoint();
                huntMode = false;
            }
        }

        //the ship is sunk- now searching for new random point
        else {
            //case HIT
            if (theState == 1) {
                computerLastHitPoint = computerCurrentPoint;
                huntMode = true;
                computerCurrentPoint = getCalculatedPoint(computerLastHitPoint);
            }
            //case MISS or SUNK
            else {
                computerCurrentPoint = getRandomPoint();
            }
        }
        return theState;
    }

    public Point getCalculatedPoint(Point computerLastHitPoint) {
        int lastRow = computerLastHitPoint.getRow();
        int lastCol = computerLastHitPoint.getCol();
        int boardSize = getPlayerBoard().getBoardSize();

        while (true) {
            //right
            if (computerDirection == 1) {
                if (lastCol < boardSize - 1) {
                    if ((getPlayerBoard().getTile(lastRow, lastCol + 1).getTileStatus() == Tile.Status.NONE)
                            || (getPlayerBoard().getTile(lastRow, lastCol + 1).getTileStatus() == Tile.Status.NONE_X)
                            || (getPlayerBoard().getTile(lastRow, lastCol + 1).getTileStatus() == Tile.Status.SHIP)) {
                        calculatedPoint.setRow(lastRow);
                        calculatedPoint.setCol(lastCol + 1);
                        return calculatedPoint;
                    }
                }
                nextDirection();
            }
            //up
            if (computerDirection == 2) {

                if (lastRow > 0) {
                    if ((getPlayerBoard().getTile(lastRow - 1, lastCol).getTileStatus() == Tile.Status.NONE)
                            || (getPlayerBoard().getTile(lastRow - 1, lastCol).getTileStatus() == Tile.Status.NONE_X)
                            || (getPlayerBoard().getTile(lastRow - 1, lastCol).getTileStatus() == Tile.Status.SHIP)) {
                        calculatedPoint.setRow(lastRow - 1);
                        calculatedPoint.setCol(lastCol);
                        return calculatedPoint;
                    }
                }
                nextDirection();

            }
            //left
            if (computerDirection == 3) {

                if (lastCol > 0) {
                    if ((getPlayerBoard().getTile(lastRow, lastCol - 1).getTileStatus() == Tile.Status.NONE)
                            || (getPlayerBoard().getTile(lastRow, lastCol - 1).getTileStatus() == Tile.Status.NONE_X)
                            || (getPlayerBoard().getTile(lastRow, lastCol - 1).getTileStatus() == Tile.Status.SHIP)) {
                        calculatedPoint.setRow(lastRow);
                        calculatedPoint.setCol(lastCol - 1);
                        return calculatedPoint;
                    }
                }
                nextDirection();
            }

            //down
            if (computerDirection == 4) {

                if (lastRow < boardSize - 1) {
                    if ((getPlayerBoard().getTile(lastRow + 1, lastCol).getTileStatus() == Tile.Status.NONE)
                            || (getPlayerBoard().getTile(lastRow + 1, lastCol).getTileStatus() == Tile.Status.NONE_X)
                            || (getPlayerBoard().getTile(lastRow + 1, lastCol).getTileStatus() == Tile.Status.SHIP)) {
                        calculatedPoint.setRow(lastRow + 1);
                        calculatedPoint.setCol(lastCol);
                        return calculatedPoint;
                    }
                }
                nextDirection();
            }
        }
    }

    public Point getRandomPoint() {
        int row, col;
        if (difficulty == 0) {
            do {
                row = random.nextInt(playerBoard.getBoardSize());
                col = random.nextInt(playerBoard.getBoardSize());
            } while ((playerBoard.getTile(row, col).getTileStatus() == Tile.Status.HIT)
                    || (playerBoard.getTile(row, col).getTileStatus() == Tile.Status.MISS)
                    || (playerBoard.getTile(row, col).getTileStatus() == Tile.Status.SUNK));
        } else {
            do {
                row = random.nextInt(playerBoard.getBoardSize());
                col = random.nextInt(playerBoard.getBoardSize());
            } while ((playerBoard.getTile(row, col).getTileStatus() == Tile.Status.HIT)
                    || (playerBoard.getTile(row, col).getTileStatus() == Tile.Status.MISS)
                    || (playerBoard.getTile(row, col).getTileStatus() == Tile.Status.SUNK)
                    || (playerBoard.getTile(row, col).getTileStatus() == Tile.Status.NONE_X));
        }

        randomPoint.setRow(row);
        randomPoint.setCol(col);
        return randomPoint;
    }

    public void setHuntMode(boolean huntMode) {
        this.huntMode = huntMode;
    }

    public boolean checkIfWin(Board board) {
        return board.getNumOfShipsLeft() == 0;
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getComputerBoard() {
        return computerBoard;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public void nextDirection() {
        if (computerDirection >= 4)
            computerDirection = 1;
        else
            computerDirection++;
    }

}

