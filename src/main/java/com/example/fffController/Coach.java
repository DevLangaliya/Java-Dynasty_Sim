package com.example.fffController;

public class Coach {
    private String firstName, lastName;
    private int wins, losses, ties;

    public Coach(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getName(){
        return this.firstName + " " + this.lastName;
    }


}
