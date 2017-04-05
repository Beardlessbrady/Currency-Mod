package gunn.modcurrency.mod.core.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.ArrayList;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-18
 */
public class BankAccountSavedData extends WorldSavedData{
    ArrayList<BankAccount> bankArray = new ArrayList<>();
    public static String IDENTIFIER = "currency_bankdata";

    public BankAccountSavedData(String name) {
        super(name);
    }

    public BankAccount getBankAccount(String name){
        for(int i = 0; i < bankArray.size(); i++){
            if(name.toLowerCase().equals(bankArray.get(i).getName().toLowerCase())) return bankArray.get(i);
        }

        //If no account found, make one and run method again
        setBankAccount(new BankAccount(name, 0));
        return getBankAccount(name);
    }

    public void setBankAccount(BankAccount account){
        boolean hasAcc = false;
        for(int i = 0; i < bankArray.size(); i++){
            if(account.getName().toLowerCase().equals(bankArray.get(i).getName().toLowerCase())){
                bankArray.set(i, account);
                hasAcc = true;
            }
        }

        if(!hasAcc) bankArray.add(account);
        markDirty();
    }

    public void clearData(){
        bankArray = new ArrayList<>();
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList bankList = new NBTTagList();


        for(int i = 0; i < bankArray.size(); i++){
            NBTTagCompound bankTag = new NBTTagCompound();
            bankTag.setString("name", bankArray.get(i).getName());
            bankTag.setInteger("balance", bankArray.get(i).getBalance());

            bankList.appendTag(bankTag);
        }

        compound.setTag("bankData", bankList);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList bankList = nbt.getTagList("bankData", 10);

        for (int i = 0; i < bankList.tagCount(); i++) {
            NBTTagCompound bankTag = bankList.getCompoundTagAt(i);
            String name = bankTag.getString("name");
            int bal = bankTag.getInteger("balance");
            bankArray.add(new BankAccount(name, bal));
        }
    }

    public static BankAccountSavedData getData(World world) {
        BankAccountSavedData data = (BankAccountSavedData) world.getMapStorage().getOrLoadData(BankAccountSavedData.class, IDENTIFIER);
        if (data == null) {
            data = new BankAccountSavedData(IDENTIFIER);
            world.setData(IDENTIFIER, data);
        }
        return data;
    }

}