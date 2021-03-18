package com.example.footballsms;

import java.time.LocalDateTime;

public class Match {
    Team team1, team2;
    String result;
    LocalDateTime createDate;

    public Match(Team team1, Team team2, String result){
        this.team1 = team1;
        this.team2 = team2;
        this.result = result;
    }

}
