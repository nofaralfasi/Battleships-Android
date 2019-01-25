package com.example.hp.battleship;

class ScoreRecord {
    private int id;
    private String name;
    private int score;
    private double latitude, longtitude;


    public ScoreRecord( String name, int score, double latitude, double longtitude){

        this.name=name;
        this.score=score;
        this.latitude=latitude;
        this.longtitude=longtitude;
    }


    public ScoreRecord(int id, String name, int score, double latitude, double longtitude){
        this.id=id;
        this.name=name;
        this.score=score;
        this.latitude=latitude;
        this.longtitude=longtitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

}
