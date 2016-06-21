package com.teamtreehouse.model;

public class Team implements Comparable<Team>{
    private String name;
    private String coach;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;

        Team team = (Team) o;
        return coach.equals(team.coach) && name.equals(team.name);
    }
}
