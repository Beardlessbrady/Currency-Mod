package gunn.modcurrency.mod.core.data;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-18
 */
public class BankAccount {
    String name;
    int balance;

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

    public void shrinkBalance(int num){
        if(balance - num <= 0) balance = 0;
        balance = balance - num;
    }

    public void growBalance(int num){
        balance = balance + num;
    }

    @Override
    public String toString() {
        return name + ":" + balance;
    }
}
