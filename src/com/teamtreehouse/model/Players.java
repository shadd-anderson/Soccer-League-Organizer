package com.teamtreehouse.model;

import java.util.*;
import java.util.stream.*;

public class Players {

    public static Player[] load() {
        return new Player[]{
                new Player("Joe", "Smith", 42, true, false),
                new Player("Jill", "Tanner", 36, true, false),
                new Player("Bill", "Bon", 43, true, false),
                new Player("Eva", "Gordon", 45, false, false),
                new Player("Matt", "Gill", 40, false, false),
                new Player("Kimmy", "Stein", 41, false, false),
                new Player("Sammy", "Adams", 45, false, false),
                new Player("Karl", "Saygan", 42, true, false),
                new Player("Suzane", "Greenberg", 44, true, false),
                new Player("Sal", "Dali", 41, false, false),
                new Player("Joe", "Kavalier", 39, false, false),
                new Player("Ben", "Finkelstein", 44, false, false),
                new Player("Diego", "Soto", 41, true, false),
                new Player("Chloe", "Alaska", 47, false, false),
                new Player("Arfalseld", "Willis", 43, false, false),
                new Player("Phillip", "Helm", 44, true, false),
                new Player("Les", "Clay", 42, true, false),
                new Player("Herschel", "Krustofski", 45, true, false),
                new Player("Andrew", "Chalklerz", 42, true, false),
                new Player("Pasan", "Membrane", 36, true, false),
                new Player("Kenny", "Lovins", 35, true, false),
                new Player("Alena", "Sketchings", 45, false, false),
                new Player("Carling", "Seacharpet", 40, false, false),
                new Player("Joseph", "Freely", 41, false, false),
                new Player("Gabe", "Listmaker", 45, false, false),
                new Player("Jeremy", "Smith", 42, true, false),
                new Player("Ben", "Droid", 44, true, false),
                new Player("James", "Dothnette", 41, false, false),
                new Player("Nick", "Grande", 39, false, false),
                new Player("Will", "Guyam", 44, false, false),
                new Player("Jason", "Seaver", 41, true, false),
                new Player("Johnny", "Thunder", 47, false, false),
                new Player("Ryan", "Creedson", 43, false, false),
        };

    }

    /*A static int used to calculate the highest discrepancy possible between two teams' average ability scores. This
    is used in the team fairness report*/
    public static double highDiscrepancy(Player[] players) {
        Set<Player> ascending = new TreeSet<>(comparator);
        Collections.addAll(ascending, players);
        Player[] sorted = ascending.toArray(new Player[ascending.size()]);
        Player[] topEleven = new Player[11];
        Player[] bottomEleven = new Player[11];
        for (int i = 0; i < 11; i++) {
            topEleven[i] = sorted[i];
        }
        for (int i = 0, j = 32; i < 11; i++, j--) {
            bottomEleven[i] = sorted[j];
        }
        double top = 0;
        double bottom = 0;
        for (Player player : topEleven) {
            top += player.abilityScore();
        }
        for (Player player : bottomEleven) {
            bottom += player.abilityScore();
        }
        return (top / 11) - (bottom / 11);
    }

    /*A static method used to create the fairest teams possible based on a set of teams and players*/
    public static void optimizeTeams(Player[] players, Map<Team, Set<Player>> teams) {
        for (Map.Entry team : teams.entrySet()) {
            ((Set<Player>) team.getValue()).clear();
            ((Team) team.getKey()).playerSet.clear();
        }
        Set<Player> ascending = new TreeSet<>(comparator);
        Collections.addAll(ascending, players);
        Player[] sorted = ascending.toArray(new Player[ascending.size()]);
        Team[] listOfTeams = new Team[teams.size()];
        int teamNumber = 0;
        for (Map.Entry team : teams.entrySet()) {
            listOfTeams[teamNumber] = (Team) team.getKey();
            teamNumber++;
        }
        for (Player aSorted : sorted) {
            lowestScore(listOfTeams).addPlayer(aSorted);
        }
        for (Team team : listOfTeams) {
            teams.put(team, team.playerSet);
        }
        for (Player player : players) {
            player.setDrafted();
        }
    }

    /*Compares players based on their ability scores*/
    public static Comparator<Player> comparator = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            if (compare(o1.abilityScore(), o2.abilityScore()) == 0) {
                return o1.compareTo(o2);
            } else {
                return compare(o1.abilityScore(), o2.abilityScore());
            }
        }

        private int compare(double v, double v1) {
            if (v == v1) {
                return 0;
            } else if (v < v1) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    /*Compares teams, first on their total ability score, then on their player set sizes. Used for team optimization*/
    private static Comparator<Team> teamComparator = new Comparator<Team>(){
        @Override
        public int compare(Team o1, Team o2){
            if(o1.equals(o2)){
                return 0;
            } else if (o1.totalAbilityScore() == o2.totalAbilityScore()){
                if(o1.playerSet.size() == o2.playerSet.size()){
                    return o1.compareTo(o2);
                } else {
                    return compare(o1.playerSet.size(),o2.playerSet.size());
                }
            } else {
                return compare(o1.totalAbilityScore(), o2.totalAbilityScore());
            }
        }

        private int compare(double v, double v1) {
            if (v == v1){
                return 0;
            } else if(v<v1){
                return -1;
            } else {
                return 1;
            }
        }

        private int compare(int size, int size1) {
            if (size == size1){
                return 0;
            } else if(size<size1){
                return -1;
            } else {
                return 1;
            }
        }
    };

    private static Team lowestScore(Team[] teams){
        assert teams.length!=0;
        Set<Team> organized = new TreeSet<>(teamComparator);
        Collections.addAll(organized,teams);
        Team[] organizedTeams = organized.toArray(new Team[organized.size()]);
        return organizedTeams[0];
    }
}