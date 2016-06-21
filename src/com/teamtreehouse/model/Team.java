package com.teamtreehouse.model;

import java.util.Set;

public class Team implements Comparable<Team>{
    private String name;
    private String coach;
    private Set<Player> playerList;

    public Team (String name, String coach){
        this.name = name;
        this.coach = coach;
    }

    public String getName(){
        return name;
    }

    public String getCoach(){
        return coach;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCoach(String coach){
        this.coach = coach;
    }

    public Set<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(Set<Player> list){
        playerList = list;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Team o) {
        if(equals(o)){
            return 0;
        }
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Team)) return false;

        Team team = (Team) o;
        if(coach != team.coach){return false;}
        return name.equals(team.name);
    }
}
