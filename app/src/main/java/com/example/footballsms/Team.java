package com.example.footballsms;

public class Team {
    public int id;
    public String name;
    public int matchesPlayed;
    public int matchesWon;
    public int matchesLost;
    public int matchesDrawn;
    public int goalAverage;

    public Team(int id, String name, int matchesWon, int matchesLost, int matchesDrawn, int goalAverage){
        this.id = id;
        this.name = name;
        this.matchesPlayed = matchesWon + matchesLost + matchesDrawn;
        this.matchesWon = matchesWon;
        this.matchesLost = matchesLost;
        this.matchesDrawn = matchesDrawn;
        this.goalAverage = goalAverage;
    }

    public Team(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", matchesPlayed=" + matchesPlayed +
                ", matchesWon=" + matchesWon +
                ", matchesLost=" + matchesLost +
                ", matchesDrawn=" + matchesDrawn +
                ", goalAverage=" + goalAverage +
                '}';
    }
}
