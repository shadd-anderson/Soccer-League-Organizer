package com.teamtreehouse.model;

import java.io.*;
import java.util.*;

public class Menu {
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private Player[] players;
    private final int MAX_PLAYERS = 11;
    private int MAX_TEAMS;
    private Set<Player> alphaSet = new TreeSet<>();
    private Map<String, String> mainMenu = new TreeMap<>();
    private Map<String, String> organMenu = new TreeMap<>();
    private Map<String, String> coachMenu = new TreeMap<>();
    private Map<String, String> statsMenu = new TreeMap<>();
    private Map<Team, Set<Player>> teams;

    private void save() {
        try {
            FileOutputStream teamSave = new FileOutputStream("teams.ser");
            FileOutputStream playerSave = new FileOutputStream("players.ser");
            ObjectOutputStream team = new ObjectOutputStream(teamSave);
            ObjectOutputStream player = new ObjectOutputStream(playerSave);
            player.writeObject(players);
            team.writeObject(teams);
            player.close();
            team.close();
            teamSave.close();
            playerSave.close();
            System.out.println("Teams successfully saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importFiles() throws IOException,ClassNotFoundException {
            FileInputStream teamLoad = new FileInputStream("teams.ser");
            FileInputStream playerLoad = new FileInputStream("players.ser");
            ObjectInputStream teamsIn = new ObjectInputStream(teamLoad);
            ObjectInputStream playersIn = new ObjectInputStream(playerLoad);
            teams = (Map<Team,Set<Player>>) teamsIn.readObject();
            players = (Player[]) playersIn.readObject();
            teamsIn.close();
            teamLoad.close();
            System.out.println("Teams successfully loaded!");
    }

    /*Defines the various menu options*/
    public Menu() {
        mainMenu.put("1", "Organizer");
        mainMenu.put("2", "Coach");
        mainMenu.put("3", "Quit");
        organMenu.put("1", "Add a new team");
        organMenu.put("2", "Add players to a team");
        organMenu.put("3", "Remove players from a team");
        organMenu.put("4", "View current rosters and their stats");
        organMenu.put("5", "Go to the coach menu");
        organMenu.put("6", "Go back to the main menu");
        coachMenu.put("1", "View/print your team roster");
        coachMenu.put("2", "Go to the organizer menu");
        coachMenu.put("3", "Go back to the main menu");
        statsMenu.put("1", "View team rosters");
        statsMenu.put("2", "Team-by-team height distribution");
        statsMenu.put("3", "Team fairness report");
        statsMenu.put("4", "Back to organizer menu");
    }

    /*A shortcut to skip the tedious try/catch blocks*/
    private String readLine() {
        String result = "";
        try {
            result = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*Another shortcut, prompting the user to press enter before continuing*/
    private void enter() {
        System.out.println("Please press enter to continue.");
        readLine();
    }

    /*Simplifies user input*/
    private String inputTrimmer() {
        return readLine().trim().toLowerCase();
    }

    /*Default for all menus*/
    private void invalidChoice() {
        System.out.println("That is not a valid choice. Please enter the NUMBER of the choice you would like.");
    }

    /*Checks to make sure input by user is a number. Also allows for typos (eg: 23f)*/
    private int numberChecker() {
        int number = 0;
        try {
            number = Integer.parseInt(readLine().replaceAll("[\\D]", ""));
        } catch (NumberFormatException nfe) {
            System.out.println("Please enter a number.");
        }
        return number;
    }

    /*Boolean used to check if the team name already exists. Avoids duplication of team names.*/
    private boolean containsTeam(Map<Team, Set<Player>> teams, String name) {
        for (Map.Entry team : teams.entrySet()) {
            if (team.getKey().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /*Boolean used to make sure teams have been set up before. Does not allow for user to add/remove players from teams
    that don't exist*/
    private boolean teamsExist() {
        if (teams.isEmpty()) {
            System.out.println("No teams are currently set up. Please add teams from the organizer menu.");
            enter();
            return false;
        } else {
            return true;
        }
    }

    /*Boolean used to make sure a team has players on its roster. Does not allow for roster viewing, stats viewing, or
    removal of players from teams that have no roster*/
    private boolean playersExist(Set<Player> playerList, Team team) {
        if (playerList.isEmpty()) {
            System.out.printf("There are currently no players on %s. Please add some before removing them.%n",
                    team.toString());
            enter();
            return false;
        } else {
            return true;
        }
    }

    /*Boolean used to disallow selection of a player number from a list that does not have that number of players.
    Example: Picking player #35 from a list of only 33 players*/
    private boolean playerExists(Player[] playerList, int selection) {
        if (selection > playerList.length) {
            System.out.printf("Sorry, there are only %d players to choose from. Please select one of them.%n",
                    playerList.length);
            return false;
        } else {
            return true;
        }
    }

    /*Repeatable method used to select the team. Also includes placeholders for the method to be used
    inside of different menu options*/
    @SuppressWarnings("MalformedFormatString")
    private Team chooseTeam(String option, String menu) {
        System.out.println("Here are the current teams in the league:");
        Map<Integer, Team> numberedTeams = new LinkedHashMap<>();
        int teamNumber = 1;
        for (Map.Entry team : teams.entrySet()) {
            numberedTeams.put(teamNumber, (Team) team.getKey());
            teamNumber++;
        }

        for (Map.Entry numberedTeam : numberedTeams.entrySet()) {
            System.out.printf("%d. %s%n", numberedTeam.getKey(), numberedTeam.getValue().toString());
        }
        System.out.printf("%d. Go back to %s menu.%n", teamNumber, menu);
        int selection;
        do {
            System.out.printf("Please enter the number of the team you would like to %s:%n", option);
            selection = numberChecker();
            if (selection > numberedTeams.size() && selection != teamNumber) {
                System.out.printf("Sorry, there are only %d teams. Please choose one of them.%n", numberedTeams.size());
                selection = 0;
            }
        } while (selection == 0);
        if (selection != teamNumber) {
            return numberedTeams.get(selection);
        } else {
            switch (menu) {
                case "coach":
                    runCoachMenu();
                    break;
                case "stats":
                    runStatsMenu();
                    break;
                case "organizer":
                    runOrganMenu();
                    break;
                default:
                    runMainMenu();
                    break;
            }
            return null;
        }
    }

    /*Adds all the players from the player array into a set, which then alphabetizes the set. It is then put back
    into the original array, now alphabetized.*/
    public void run() {
        System.out.printf("Attempting to load teams... Please wait...%n%n");
        try {
            importFiles();
            MAX_TEAMS = players.length/MAX_PLAYERS;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Teams unable to be loaded.%nPlease create new teams from the organizer menu.%n%n");
            players = Players.load();
            Collections.addAll(alphaSet, players);
            players = alphaSet.toArray(new Player[alphaSet.size()]);
            teams = new TreeMap<>();
            MAX_TEAMS = players.length/MAX_PLAYERS;
        }
        runMainMenu();
    }

    /*Runs the main menu*/
    private void runMainMenu() {
        String choice;
        do {
            System.out.println("Welcome to the Youth Soccer League! Here are the user types available:");
            for (Map.Entry type : mainMenu.entrySet()) {
                System.out.printf("%s. %s%n", type.getKey(), type.getValue());
            }
            System.out.println("Please enter the number of the user you would like to log in as:");
            choice = readLine();
            switch (choice) {
                case "1":
                    runOrganMenu();
                    break;
                case "2":
                    if (teamsExist()) {
                        runCoachMenu();
                    } else {
                        runMainMenu();
                    }
                    break;
                case "3":
                    System.out.println("Thanks for visiting the Youth Soccer League! Come again soon.");
                    save();
                    System.exit(0);
                    break;
                default:
                    invalidChoice();
            }
        } while (!choice.equals("3") && !choice.equals("2") && !choice.equals("1"));
    }

    /*Runs the organizer menu*/
    private void runOrganMenu() {
        String choice;
        do {
            System.out.println("Welcome, organizer! Here are your options:");
            for (Map.Entry option : organMenu.entrySet()) {
                System.out.printf("%s. %s%n", option.getKey(), option.getValue());
            }
            System.out.println("Please enter the number of which option you would like to choose:");
            choice = readLine();
            switch (choice) {
                case "1":
                    newTeam();
                    enter();
                    runOrganMenu();
                    break;
                case "2":
                    if (teamsExist()) {
                        addPlayerToTeam();
                    } else {
                        runOrganMenu();
                    }
                    break;
                case "3":
                    if (teamsExist()) {
                        removePlayerFromTeam();
                    } else {
                        runOrganMenu();
                    }
                    break;
                case "4":
                    if (teamsExist()) {
                        runStatsMenu();
                    } else {
                        runOrganMenu();
                    }
                    break;
                case "5":
                    if (teamsExist()) {
                        runCoachMenu();
                    } else {
                        runOrganMenu();
                    }
                    break;
                case "6":
                    runMainMenu();
                    break;
                default:
                    invalidChoice();
            }
        }
        while (!choice.equals("5") && !choice.equals("6") && !choice.equals("4")
                && !choice.equals("3") && !choice.equals("2"));
    }

    /*Method used to create a new team*/
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void newTeam() {
        if (teams.size() < MAX_TEAMS) {
            Set<Player> playerList = new TreeSet<>();
            String name;
            String coach;
            do {
                System.out.println("Please enter the name of the new team:");
                name = readLine();
                if (containsTeam(teams, name)) {
                    System.out.printf("Sorry! There's already a team named %s. Please enter a different name.%n", name);
                }
            } while (containsTeam(teams, name));
            System.out.printf("Please enter the coach for the %s:%n", name);
            coach = readLine();
            teams.put(new Team(name, coach), playerList);
            System.out.printf("Team \"%s\" (coached by %s) created! You may now add new players.%n", name, coach);
        } else {
            System.out.printf("Sorry, there can only be a max of %d teams.%n", MAX_TEAMS);
        }
    }

    /*Method used to add players to a team*/
    @SuppressWarnings("MalformedFormatString")
    private void addPlayerToTeam() {
        Team currentTeam = chooseTeam("add players to", "organizer");
        if (currentTeam != null) {
            Set<Player> currentRoster = teams.get(currentTeam);
            String continuing;
            do {
                if (currentRoster.size() < MAX_PLAYERS) {
                    System.out.println("Here is a list of the current free agents:");
                    System.out.println("(Players already on a team will not be shown)");
                    int playerNumber = 1;
                    for (Player player : players) {
                        if (!player.isDrafted()) {
                            System.out.printf("%d. %s%n", playerNumber, player.toStringStats());
                        }
                        playerNumber++;
                    }
                    System.out.printf("Please select the number of the player you would like to add to the %s:%n",
                            currentTeam.getName());
                    int choice = 0;
                    do {
                        do {
                            try {
                                choice = Integer.parseInt(readLine().replaceAll("[\\D]", ""));
                            } catch (NumberFormatException nfe) {
                                System.out.println("Please enter a number.");
                            }
                        } while (choice == 0);
                        if (playerExists(players, choice)) {
                            if (!players[choice - 1].isDrafted()) {
                                currentRoster.add(players[choice - 1]);
                                players[choice - 1].setDrafted();
                                System.out.printf("%s added to %s!%n",
                                        players[choice - 1].toString(), currentTeam.getName());
                                enter();
                            } else {
                                System.out.printf("%s is already on a team. " +
                                                "Please select a different player or remove %s from his current team.%n",
                                        players[choice - 1].toString(), players[choice - 1].toString());
                                enter();
                            }
                        } else {
                            choice = 0;
                        }
                    } while (!playerExists(players, choice));
                    System.out.printf("Would you like to add another player to the %s? (yes or no)%n",
                            currentTeam.getName());
                    do {
                        continuing = inputTrimmer();
                        if (continuing.equals("no")) {
                            teams.put(currentTeam, currentRoster);
                            otherTeam("add players to");
                        } else if (!continuing.equals("yes")) {
                            System.out.println("Input not recognized. Please enter either yes or no.");
                        }
                    } while (!continuing.equals("yes") && !continuing.equals("no"));
                } else {
                    System.out.printf("Sorry, too many players on %s. There can only be a max of %d.%n" +
                            "Please remove a player before adding another.%n", currentTeam.toString(), MAX_PLAYERS);
                    continuing = "no";
                    enter();
                }
            } while (continuing.equals("yes"));
        }
        runOrganMenu();
    }

    /*Method used to remove players from teams*/
    @SuppressWarnings("MalformedFormatString")
    private void removePlayerFromTeam() {
        Team currentTeam = chooseTeam("remove players from", "organizer");
        if (currentTeam != null) {
            Set<Player> currentRoster = teams.get(currentTeam);
            if (playersExist(currentRoster, currentTeam)) {
                String continuing;
                do {
                    Player[] rosterArray = currentRoster.toArray(new Player[currentRoster.size()]);
                    System.out.printf("Here is a list of the players currently on the %s:%n", currentTeam.toString());
                    int playerNumber = 1;
                    for (Player player : rosterArray) {
                        System.out.printf("%d. %s%n", playerNumber, player.toStringStats());
                        playerNumber++;
                    }
                    Player playerChoice = null;
                    int choice;
                    do {
                        System.out.printf("Please select the number of the player you would like " +
                                "to remove from the %s:%n", currentTeam.getName());
                        do {
                            choice = numberChecker();
                        } while (choice == 0);
                        if (choice <= rosterArray.length) {
                            playerChoice = rosterArray[choice - 1];
                        } else {
                            System.out.printf("Sorry, there is no player #%d. There are %d players to choose from.%n",
                                    choice, rosterArray.length);
                        }
                    } while (choice > rosterArray.length);
                    assert playerChoice != null;
                    System.out.printf("Are you sure you'd like to remove %s from the %s?%n",
                            playerChoice.toString(), currentTeam.toString());
                    System.out.println("Please type 'yes' if you are sure. If not, please type 'no'.");
                    String confirmation;
                    do {
                        confirmation = inputTrimmer();
                        switch (confirmation) {
                            case "yes":
                                System.out.println("Please wait.....");
                                currentRoster.remove(playerChoice);
                                System.out.printf("%s removed from %s!%n",
                                        playerChoice.toString(), currentTeam.toString());
                                for (Player player : players) {
                                    if (player.equals(playerChoice)) {
                                        player.notDrafted();
                                    }
                                }
                                break;
                            case "no":
                                break;
                            default:
                                System.out.println("Please enter either 'yes' or 'no'");
                        }
                    } while (!confirmation.equals("yes") && !confirmation.equals("no"));
                    teams.put(currentTeam, currentRoster);
                    if (playersExist(currentRoster, currentTeam)) {
                        System.out.printf("Would you like to remove another player from the %s? " +
                                "(Please enter yes or no)%n", currentTeam.toString());
                        do {
                            continuing = inputTrimmer();
                            if (continuing.equals("no")) {
                                otherTeam("remove players from");
                            } else if (!continuing.equals("yes")) {
                                System.out.println("Input not recognized. Please enter either yes or no.");
                            }
                        } while (!continuing.equals("yes") && !continuing.equals("no"));
                    } else {
                        continuing = "no";
                        otherTeam("remove players from");
                    }
                } while (continuing.equals("yes"));
            }
        }
        runOrganMenu();
    }

    /*Reusable method that allows for a user to add/remove players on a different team*/
    private void otherTeam(String menu) {
        System.out.printf("Would you like to %s a different team? (yes or no)%n", menu);
        String otherTeam;
        do {
            otherTeam = inputTrimmer();
            if (otherTeam.equals("yes")) {
                if (menu.equals("add players to")) {
                    addPlayerToTeam();
                } else if (menu.equals("remove players from")) {
                    removePlayerFromTeam();
                }
            } else if (!otherTeam.equals("no")) {
                System.out.println("Input not recognized. Please enter either yes or no.");
            }
        } while (!otherTeam.equals("no") && !otherTeam.equals("yes"));
    }

    /*Runs the coach menu*/
    private void runCoachMenu() {
        String choice;
        do {
            System.out.println("Welcome, coach! Here are your options:");
            for (Map.Entry type : coachMenu.entrySet()) {
                System.out.printf("%s. %s%n", type.getKey(), type.getValue());
            }
            System.out.println("Please enter the number of the the choice you would like to see:");
            choice = readLine();
            switch (choice) {
                case "1":
                    printRoster();
                    break;
                case "2":
                    runOrganMenu();
                    break;
                case "3":
                    runMainMenu();
                    break;
                default:
                    invalidChoice();
            }
        } while (!choice.equals("2") && !choice.equals("3"));
    }

    /*Runs the stats menu*/
    @SuppressWarnings("MalformedFormatString")
    private void runStatsMenu() {
        String choice;
        do {
            System.out.println("Here are your options:");
            for (Map.Entry option : statsMenu.entrySet()) {
                System.out.printf("%s. %s%n", option.getKey(), option.getValue());
            }
            System.out.println("Please enter the number of the option you would like to view:");
            choice = readLine();
            switch (choice) {
                case "1":
                    printRosterStats();
                    break;
                case "2":
                    heightDistribution();
                    break;
                case "3":
                    fairnessReport();
                    break;
                case "4":
                    runOrganMenu();
                    break;
                default:
                    invalidChoice();
            }
        } while (!choice.equals("4"));
    }

    /*Used by the coach to print the roster of a team*/
    private void printRoster() {
        Team currentTeam = chooseTeam("view the roster for", "coach");
        if (currentTeam != null) {
            Set<Player> currentRoster = teams.get(currentTeam);
            if (playersExist(currentRoster, currentTeam)) {
                System.out.printf("Here are all the players currently on the %s:%n", currentTeam.toString());
                for (Player player : currentRoster) {
                    System.out.printf("%s%n", player.toString());
                }
                enter();
            }
        }
    }

    /*Used by the organizer to print the roster of a team, includes player stats*/
    private void printRosterStats() {
        Team currentTeam = chooseTeam("view the roster and stats for", "stats");
        if (currentTeam != null) {
            Set<Player> currentRoster = teams.get(currentTeam);
            if (playersExist(currentRoster, currentTeam)) {
                System.out.printf("Here are all the players currently on the %s:%n", currentTeam.toString());
                for (Player player : currentRoster) {
                    System.out.printf("%s%n", player.toStringStats());
                }
                enter();
            }
        }
    }

    /*Lists the number of players at each height level for a specified team*/
    @SuppressWarnings({"MalformedFormatString", "SuspiciousMethodCalls"})
    private void heightDistribution() {
        Team currentTeam = chooseTeam("view the height distribution for", "stats");
        if (currentTeam != null) {
            Set<Player> currentRoster = teams.get(currentTeam);
            if (playersExist(currentRoster, currentTeam)) {
                Map<Integer, Set<Player>> heightDistribution = new TreeMap<>();
                for (Player player : currentRoster) {
                    if (heightDistribution.containsKey(player.getHeightInInches())) {
                        heightDistribution.get(player.getHeightInInches()).add(player);
                    } else {
                        Set<Player> playersHeight = new TreeSet<>();
                        playersHeight.add(player);
                        heightDistribution.put(player.getHeightInInches(), playersHeight);
                    }
                }
                System.out.printf("Here is the height distribution for the %s:%n", currentTeam.toString());
                for (Map.Entry entry : heightDistribution.entrySet()) {
                    System.out.printf("%d inches - %d players%n",
                            entry.getKey(), heightDistribution.get(entry.getKey()).size());
                }
                enter();
            }
        }
    }

    /*This is something I came up with to be able to view, from a very quick and simple glance, how fair the teams are
    set up. What it does is looks at each team's average height and experience and uses it to create an "ability score".
    That score is then used to create the league's "fairness score" from 0%-100%. The calculations are as follows:
        -Ability score = team average height in inches + percentage of team that has previous experience.
        -Fairness score = (top ability score - bottom ability score)/106
        **106 is the highest discrepancy between ability scores possible based on the current player list. (I did this
        **calculation by hand, based on the players listed in the Players class. I could probably write another formula
        **to calculate it based on a different list, but because the list was set, I didn't feel that was necessary)
    Included in this report are the following requirements for the project:
        -Group team by average height
        -Group team with an average experience level
        -Group team by how many players have previous experience
    I realize this isn't exactly what the project asked for, but I figured this would be a little cleaner, as well as
    allow for the league organizer to see EXACTLY how fair his teams were*/
    private void fairnessReport() {
        Map<Team, Double> averageHeight = averageHeight();
        Map<Team, Double> averageExperience = averageExperience();
        Map<Team, Integer> totalExperience = totalExperience();
        Map<Team, Double> fairnessTeams = new HashMap<>();
        for (Map.Entry entry : averageHeight.entrySet()) {
            fairnessTeams.put((Team) entry.getKey(), averageHeight.get(entry.getKey()) +
                    averageExperience.get(entry.getKey()));
        }
        System.out.println("Teams:");
        for (Map.Entry team : fairnessTeams.entrySet()) {
            System.out.printf("%s (coached by %s)%n" +
                            "        %.0f\" average height%n" +
                            "        %.0f%% experience (%d out of %d)%n" +
                            "        Ability score: %.0f%n",
                    team.getKey().toString(), ((Team) team.getKey()).getCoach(),
                    averageHeight.get(team.getKey()),
                    averageExperience.get(team.getKey()), totalExperience.get(team.getKey()),
                    teams.get(team.getKey()).size(),
                    fairnessTeams.get(team.getKey()));
        }
        double highScore = 0;
        for (Map.Entry entry : fairnessTeams.entrySet()) {
            if ((Double) entry.getValue() > highScore) {
                highScore = (Double) entry.getValue();
            }
        }
        double lowScore = 1000;
        for (Map.Entry entry : fairnessTeams.entrySet()) {
            if ((Double) entry.getValue() < lowScore) {
                lowScore = (Double) entry.getValue();
            }
        }
        double score = (1.00 - ((highScore - lowScore) / 106)) * 100;
        System.out.printf("%nTotal fairness score: %.0f%%%n", score);
        enter();
    }

    /*Groups all teams by their average height. Used for the fairness report*/
    private Map<Team, Double> averageHeight() {
        Map<Team, Double> teamAverageHeight = new HashMap<>();
        for (Map.Entry entry : teams.entrySet()) {
            if (playersExist((Set<Player>) entry.getValue(), (Team) entry.getKey())) {
                double sum = 0;
                for (Player player : (Set<Player>) entry.getValue()) {
                    sum += player.getHeightInInches();
                }
                double average = sum / (((Set<Player>) entry.getValue()).size());
                teamAverageHeight.put((Team) entry.getKey(), average);
            }
        }
        return teamAverageHeight;
    }

    /*Groups all teams by their average experience. Used for the fairness report*/
    private Map<Team, Double> averageExperience() {
        Map<Team, Double> teamAverageExperience = new HashMap<>();
        for (Map.Entry entry : teams.entrySet()) {
            if (playersExist((Set<Player>) entry.getValue(), (Team) entry.getKey())) {
                double sum = 0;
                for (Player player : (Set<Player>) entry.getValue()) {
                    if (player.isPreviousExperience()) {
                        sum++;
                    }
                }
                double average = (sum / (((Set<Player>) entry.getValue()).size())) * 100;
                teamAverageExperience.put((Team) entry.getKey(), average);
            }
        }
        return teamAverageExperience;
    }

    /*Groups all teams by total players with experience. Used for the fairness report*/
    private Map<Team, Integer> totalExperience() {
        Map<Team, Integer> teamAverageExperience = new HashMap<>();
        for (Map.Entry entry : teams.entrySet()) {
            if (playersExist((Set<Player>) entry.getValue(), (Team) entry.getKey())) {
                int sum = 0;
                for (Player player : (Set<Player>) entry.getValue()) {
                    if (player.isPreviousExperience()) {
                        sum++;
                    }
                }
                teamAverageExperience.put((Team) entry.getKey(), sum);
            }
        }
        return teamAverageExperience;
    }
}