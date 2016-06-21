package com.teamtreehouse.model;

import java.io.Serializable;

public class Player implements Comparable<Player>, Serializable {
  private static final long serialVersionUID = 1L;

  private String firstName;
  private String lastName;
  private int heightInInches;
  private boolean previousExperience;
  private boolean drafted;

  public Player(String firstName, String lastName, int heightInInches, boolean previousExperience, boolean drafted) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.heightInInches = heightInInches;
    this.previousExperience = previousExperience;
    this.drafted = drafted;
  }

  public int getHeightInInches() {
    return heightInInches;
  }

  public boolean isPreviousExperience() {
    return previousExperience;
  }

  public boolean isDrafted() { return drafted; }

  public void setDrafted(){
    drafted = true;
  }

  public void notDrafted(){
    drafted = false;
  }

  @Override
  public int compareTo(Player o) {
    if(equals(o)){
      return 0;
    }
    if(lastName.equals(o.lastName)){
      return firstName.compareTo(o.firstName);
    } else {
      return lastName.compareTo(o.lastName);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Player)) return false;

    Player player = (Player) o;

    if (heightInInches != player.heightInInches) return false;
    if (previousExperience != player.previousExperience) return false;
    if (!firstName.equals(player.firstName)) return false;
    return lastName.equals(player.lastName);

  }

  @Override
  public int hashCode() {
    int result = firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    result = 31 * result + heightInInches;
    result = 31 * result + (previousExperience ? 1 : 0);
    return result;
  }

  /*Converts player height in inches to height in feet. Used for a couple stats printing*/
  public String heightFeet(){
    return heightInInches/12 + "\' " + heightInInches%12 + "\"";
  }

  @Override
  public String toString(){
    return firstName + " " + lastName;
  }

  /*Used for printing out the stats of a certain player*/
  public String toStringStats(){
    String experience;
    if(isPreviousExperience()){
      experience = " (has previous experience)";
    } else {
      experience = " (does not have previous experience)";
    }
    return firstName + " " + lastName + ", " + "height: " + heightFeet() + experience;
    }

  }

