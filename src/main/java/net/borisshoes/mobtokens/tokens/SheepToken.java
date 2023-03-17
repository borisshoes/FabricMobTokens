package net.borisshoes.mobtokens.tokens;

import com.google.common.collect.Maps;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenInventory;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.borisshoes.mobtokens.Mobtokens.MAX_MOBS;

public class SheepToken extends MobToken{
   public SheepToken(){
      name = "Sheep Token";
      id = "sheep";
      entityId = "minecraft:sheep";
      displayName = Text.literal("Sheep Token").formatted(Formatting.BOLD,Formatting.BLUE);
      mobHead = MobHeadUtils.WHITE_SHEEP;
   
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.WHEAT));
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      activeProducts.add(new Pair<>(new ItemStack(Items.SHEARS),new ItemStack(Items.WHITE_WOOL)));
      requiredItems = new ArrayList<>();
      requiredItems.add(new Pair<>(new ItemStack(Items.GRASS_BLOCK), 1.0));
      requiredItems.add(new Pair<>(new ItemStack(Items.DIRT), 1.0));
   
      passiveTickTime = 0;
      activeTickTime = 1000;
   }
   
   private static final Map<DyeColor, ItemConvertible> DROPS = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
      map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      map.put(DyeColor.LIME, Blocks.LIME_WOOL);
      map.put(DyeColor.PINK, Blocks.PINK_WOOL);
      map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      map.put(DyeColor.RED, Blocks.RED_WOOL);
      map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   });
   
   @Override
   public ItemStack addTokenNbt(ItemStack item){
      NbtCompound nbt = item.getOrCreateNbt();
      NbtCompound tokenTag = new NbtCompound();
      NbtCompound display = new NbtCompound();
      tokenTag.putString("id",id);
      tokenTag.putString("UUID", UUID.randomUUID().toString());
      tokenTag.putInt("count",0);
      tokenTag.putInt("babyCount",0);
      tokenTag.putInt("growTimer",0);
      tokenTag.putInt("breedTimer",0);
      tokenTag.putInt("food",0);
      tokenTag.putInt("requiredItems",0);
      tokenTag.putInt("passiveTick",0);
      tokenTag.putInt("activeTick",0);
      tokenTag.putInt("output",0);
      NbtList colorIds = new NbtList();
      NbtList storage = new NbtList();
      NbtList tools = new NbtList();
      tokenTag.put("colors",colorIds);
      tokenTag.put("storage",storage);
      tokenTag.put("tools",tools);
      String json = Text.Serializer.toJson(displayName).replace("\"bold\":true","\"bold\":true,\"italic\":false");
      display.putString("Name",json);
      nbt.put("display",display);
      nbt.put("mobtokens",tokenTag);
      item.setNbt(nbt);
      return item;
   }
   
   @Override
   protected void doActiveTick(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      NbtList tools = tokenData.getList("tools", NbtElement.COMPOUND_TYPE);
      TokenInventory inv = new TokenInventory();
      for(NbtElement elem : tools){
         NbtCompound toolsNbt = (NbtCompound) elem;
         inv.setStack(toolsNbt.getByte("Slot"),ItemStack.fromNbt(toolsNbt));
      }
   
      List<Pair<DyeColor,Boolean>> sheepList = getStoredSheep(tokenBlock);
      int requiredItemCount = tokenData.getInt("requiredItems");
      int maxFromReqItems = getRequiredItems().size() > 0 ? (int) Math.round(requiredItemCount / getRequiredItems().get(0).getRight()) : Integer.MAX_VALUE;
      for(int i = 0; i < Math.min(maxFromReqItems,sheepList.size()); i++){
         boolean cont = true;
   
         Pair<DyeColor,Boolean> sheep = sheepList.get(i);
         if(sheep.getRight()) continue;
      
         for(Pair<ItemStack, ItemStack> pair : activeProducts){
            if(!cont) break;
            ItemStack toolRequired = pair.getLeft().copy();
            Item toolItem = toolRequired.getItem();
            if(toolItem.isDamageable()){
               for(int j = 0; j < inv.size(); j++){
                  ItemStack toolsStack = inv.getStack(j);
                  if(toolsStack.isEmpty()) continue;
                  if(toolsStack.isOf(toolItem)){
                     boolean broke = toolsStack.damage(1, Random.create(),null);
                     if(broke){
                        inv.setStack(j, ItemStack.EMPTY);
                     }

                     addToStorage(serverWorld,tokenBlock,new ItemStack(DROPS.get(sheep.getLeft()),(int)(Math.random()*3+1)));
                     
                     cont = false;
                     break;
                  }
               }
            }
         }
      }
   
      NbtList newTools = new NbtList();
      for(int i = 0; i < inv.size(); i++){
         ItemStack toolsStack = inv.getStack(i);
         if(toolsStack.isEmpty()) continue;
         NbtCompound stackNbt = toolsStack.writeNbt(new NbtCompound());
         stackNbt.putByte("Slot", (byte) i);
         newTools.add(stackNbt);
      }
      tokenData.put("tools",newTools);
   }
   
   @Override
   public boolean addMob(ItemStack token, MobEntity entity){
      if(!MobTokenUtils.isMobToken(token)) return false;
      try{
         NbtCompound tokenTag = token.getNbt().getCompound("mobtokens");
         int curCount = tokenTag.getInt("count");
         if(curCount >= MAX_MOBS){ return false;}
      
         int requiredItemCount = tokenTag.getInt("requiredItems");
         List<Pair<ItemStack, Double>> reqItemList = getRequiredItems();
         if(reqItemList.size() > 0  && curCount != 0){
            int maxFromReqItems = (int) Math.round(requiredItemCount / reqItemList.get(0).getRight());
            if(curCount+1 > maxFromReqItems) { return false; }
         }
      
         tokenTag.putInt("count",curCount+1);
         if(entity.isBaby()){
            tokenTag.putInt("babyCount",tokenTag.getInt("babyCount")+1);
            tokenTag.putInt("growTimer",10000);
         }
         return true;
      }catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   
   @Override
   protected void growBabies(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      tokenData.putInt("babyCount",0);
   
      NbtList colorIds = new NbtList();
      for(Pair<DyeColor,Boolean> sheep : getStoredSheep(tokenBlock)){
         NbtCompound sheepTag = new NbtCompound();
         sheepTag.putString("color",sheep.getLeft().getName());
         sheepTag.putBoolean("baby",false);
         colorIds.add(sheepTag);
      }
      tokenData.put("colors",colorIds);
   }
   
   public List<Pair<DyeColor,Boolean>> getStoredSheep(NbtCompound tokenData){
      NbtList colorIds = tokenData.getList("colors", NbtElement.COMPOUND_TYPE);
      List<Pair<DyeColor,Boolean>> colors = new ArrayList<>();
      
      for(NbtElement colorId : colorIds){
         NbtCompound colorTag = (NbtCompound) colorId;
         String colorName = colorTag.getString("color");
         boolean baby = colorTag.getBoolean("baby");
         colors.add(new Pair<>(DyeColor.byName(colorName,DyeColor.WHITE),baby));
      }
      return colors;
   }
   
   public void storeSheep(List<Pair<DyeColor,Boolean>> colors, NbtCompound tokenData){
      NbtList colorIds = new NbtList();
      
      for(Pair<DyeColor,Boolean> sheep : colors){
         NbtCompound sheepTag = new NbtCompound();
         sheepTag.putString("color",sheep.getLeft().getName());
         sheepTag.putBoolean("baby",sheep.getRight());
         colorIds.add(sheepTag);
      }
      tokenData.put("colors",colorIds);
   }
   
   public List<Pair<DyeColor,Boolean>> getStoredSheep(TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      NbtList colorIds = tokenData.getList("colors", NbtElement.COMPOUND_TYPE);
      List<Pair<DyeColor,Boolean>> colors = new ArrayList<>();
   
      for(NbtElement colorId : colorIds){
         NbtCompound colorTag = (NbtCompound) colorId;
         String colorName = colorTag.getString("color");
         boolean baby = colorTag.getBoolean("baby");
         colors.add(new Pair<>(DyeColor.byName(colorName,DyeColor.WHITE),baby));
      }
      return colors;
   }
   
   public void storeSheep(List<Pair<DyeColor,Boolean>> colors, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      NbtList colorIds = new NbtList();
   
      for(Pair<DyeColor,Boolean> sheep : colors){
         NbtCompound sheepTag = new NbtCompound();
         sheepTag.putString("color",sheep.getLeft().getName());
         sheepTag.putBoolean("baby",sheep.getRight());
         colorIds.add(sheepTag);
      }
      tokenData.put("colors",colorIds);
   }
}
