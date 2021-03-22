package com.example.footballsms;

import java.time.LocalDateTime;

public class Match {
    Team team1, team2;
    int team1Goals, team2Goals;
    LocalDateTime createDate;

    public Match(Team team1, Team team2, int team1Goals, int team2Goals){
        this.team1 = team1;
        this.team2 = team2;
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }

}
