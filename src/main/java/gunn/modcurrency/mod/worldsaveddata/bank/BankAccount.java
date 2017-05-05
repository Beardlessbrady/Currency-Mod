package gunn.modcurrency.mod.worldsaveddata.bank;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-18
 */
public class BankAccount {
    private String name;
    private int balance;

    public BankAccount(String name){
        this.name = name;
        this.balance = 0;
    }

    public BankAccount(String name, int bal){
        this.name = name;
        this.balance = bal;
    }

    public void setBalance(int num){
        this.balance = num;
    }

    @Override
    public String toString() {
        return name + ":" + balance;
    }

    public String getName(){
        return name;
    }

    public int getBalance(){
        return balance;
    }
}
