package com.teamtreehouse.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Team implements Comparable<Team>, Serializable {
    private String name;
    private String coach;
    public Set<Player> playerSet;

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

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + coach.hashCode();
        return result;
    }

    public void addPlayer(Player player){
        playerSet.add(player);
    }

    public void removePlayer(Player player){
        playerSet.remove(player);
    }

    public double averageHeight(){
        double total = 0;
        for(Player player: playerSet){
            total+=player.getHeightInInches();
        }
        return total/playerSet.size();
    }

    public double totalExperience(){
        if(!playerSet.isEmpty()) {
            double total = 0;
            for (Player player : playerSet) {
                if (player.isPreviousExperience()) {
                    total++;
                }
            }
            return total;
        } else {
            return 0;
        }
    }

    public double averageExperience() {
        if (playerSet.isEmpty()) {
            return 0;
        } else {
            return totalExperience() / playerSet.size();
        }
    }

    public double averageAbilityScore(){
        if(playerSet.isEmpty()){
            return 0;
        } else {
            List<Double> scores = new LinkedList<>();
            for (Player player : playerSet) {
                scores.add(player.abilityScore());
            }
            double total = 0;
            for (Double thisScore : scores) {
                total += thisScore;
            }
            return total / scores.size();
        }
    }

    public double totalAbilityScore(){
        if(playerSet.isEmpty()){
            return 0;
        } else {
            double score = 0;
            for(Player player: playerSet){
                score+=player.abilityScore();
            }
            return score;
        }
    }
}
