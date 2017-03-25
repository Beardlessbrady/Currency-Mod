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
    public static String IDENTIFIER = "currencyBankData";

    public BankAccountSavedData(String name) {
        super(name);
    }

    public BankAccount getBankAccount(String name){
        System.out.println(bankArray.size());
        for(int i = 0; i < bankArray.size(); i++){
            System.out.println(bankArray.get(i));
            if(name.toLowerCase().equals(bankArray.get(i).name.toLowerCase())) return bankArray.get(i);
        }
        return null;
    }

    public void setBankAccount(BankAccount account){
        boolean hasAcc = false;
        for(int i = 0; i < bankArray.size(); i++){
            if(account.name.toLowerCase().equals(bankArray.get(i).name.toLowerCase())){
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
            bankTag.setString("name", bankArray.get(i).name);
            bankTag.setInteger("balance", bankArray.get(i).balance);

            bankList.appendTag(bankTag);
        }

        compound.setTag("bankData", bankList);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList bankList = nbt.getTagList("bankData", 10);

        for (int i = 0; i < bankList.tagCount(); i++) {
            NBTTagCompound bankTag = new NBTTagCompound();
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
