package beardlessbrady.modcurrency2.command;

import beardlessbrady.modcurrency2.ModCurrency;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-17
 */
public class CurrencyCommand implements ICommand {
    ArrayList aliases = Lists.newArrayList(ModCurrency.MODID, "CREATE-CURRENCY", "Create-Currency");

    @Override
    public String getName() {
        return "create-currency";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "create-currency <currency ID> <texture link>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(!server.getEntityWorld().isRemote) { //IS CLIENT
            if (args.length == 2) {
                int currID = Integer.parseInt(args[0]);
                String textureLink = args[1];

                createDirectory(new File("resourcepacks/testme"));
                createDirectory(new File("resourcepacks/testme/assets"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency/models"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency/models/item"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency/models/item/currency"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency/textures"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency/textures/items"));
                createDirectory(new File("resourcepacks/testme/assets/modcurrency/textures/items/currency"));

                createpackData();
                createCurrencyJSON(currID);
                grabTextureFromWeb(textureLink, currID);
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    public void createDirectory(File directory){
        directory.mkdir();
    }

    public void createpackData(){
        String fileString = "{\n" +
                "   \"pack\": {\n" +
                "      \"pack_format\": 3,\n" +
                "      \"description\": \"Test Resource Pack\"\n" +
                "   }\n" +
                "}";

        try (FileWriter file = new FileWriter("resourcepacks/testme/pack.mcmeta")) {
            file.write(fileString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createCurrencyJSON(int value){
        JsonObject jsonFile = new JsonObject();
        jsonFile.addProperty("parent", "builtin/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "modcurrency2:items/currency/currency_" + value);
        jsonFile.add("textures", textures);

        JsonObject headTransform = new JsonObject();
        JsonObject transform;
        JsonArray rotation;
        JsonArray translation;
        JsonArray scale;

        //Loops through all transforms
        for(int i = 0; i < 6; i++){
            transform = new JsonObject();
            rotation = new JsonArray();
            translation = new JsonArray();
            scale = new JsonArray();

            String  transformTitle = "Something Broke";
            switch(i){
                case 0:
                    transformTitle = "thirdperson_righthand";
                    break;
                case 1:
                    transformTitle = "thirdperson_lefthand";
                    break;
                case 2:
                    transformTitle = "firstperson_righthand";
                    break;
                case 3:
                    transformTitle = "firstperson_lefthand";
                    break;
                case 4:
                    transformTitle = "ground";
                    break;
                case 5:
                    transformTitle = "fixed";
                    break;
            }

            switch(i) {
                case 0:
                case 1:
                case 4:
                    rotation.add(0);
                    rotation.add(0);
                    rotation.add(0);

                    translation.add(0.00);
                    translation.add(1.00);
                    translation.add(0.25);

                    scale.add(0.60);
                    scale.add(0.60);
                    scale.add(0.26);
                    break;
                case 2:
                case 3:
                    rotation.add(346);
                    rotation.add(306);
                    rotation.add(276);

                    translation.add(0.00);
                    translation.add(3.25);
                    translation.add(0.00);

                    scale.add(0.60);
                    scale.add(0.60);
                    scale.add(0.23);
                    break;
                case 5:
                    rotation.add(0);
                    rotation.add(-180);
                    rotation.add(0);

                    translation.add(0.00);
                    translation.add(-1.50);
                    translation.add(0.00);

                    scale.add(1.00);
                    scale.add(1.00);
                    scale.add(1.00);
                    break;
            }
            transform.add("rotation", rotation);
            transform.add("translation", translation);
            transform.add("scale", scale);
            headTransform.add(transformTitle, transform);
        }

        jsonFile.add("display", headTransform);


        //Used to create pretty formatting of JSON file
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder = gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();


        try (FileWriter file = new FileWriter("resourcepacks/testme/assets/modcurrency/models/item/currency/currency_" + value + ".json")) {
            file.write(gson.toJson(jsonFile));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void grabTextureFromWeb(String link, int value){
        BufferedImage image =null;
        try{
            URL url =new URL("http://i.imgur.com/" + link);
            image = ImageIO.read(url);
            ImageIO.write(image, "png",new File("resourcepacks/testme/assets/modcurrency/textures/items/currency/currency_" + value + ".png"));

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
