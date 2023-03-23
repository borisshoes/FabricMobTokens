package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.*;

import static net.borisshoes.mobtokens.Mobtokens.MAX_MOBS;

public class VillagerToken extends MobToken{
   public final int restockTime = 6000;
   
   public VillagerToken(){
      name = "Villager Token";
      id = "villager";
      entityId = "minecraft:villager";
      displayName = Text.literal("Villager Token").formatted(Formatting.BOLD,Formatting.GREEN);
      mobHead = MobHeadUtils.UNEMPLOYED_VILLAGER;
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.BREAD));
      foods.add(new ItemStack(Items.CARROT));
      foods.add(new ItemStack(Items.POTATO));
   
      passiveProducts = new ArrayList<>();
      passiveProducts.add(new ItemStack(Items.IRON_INGOT));
      activeProducts = new ArrayList<>();
      requiredItems = new ArrayList<>();
      Optional<RegistryEntryList.Named<Item>> opt = Registries.ITEM.getEntryList(ItemTags.BEDS);
      opt.ifPresent(named -> named.stream().forEach(registryEntry -> requiredItems.add(new Pair<>(new ItemStack(registryEntry.value()),1.0))));
   
      passiveTickTime = 1200;
      activeTickTime = 0;
   }
   
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
      tokenTag.putBoolean("hasZombie",false);
      tokenTag.putBoolean("hasIllager",false);
      tokenTag.putInt("ironVillagers",0);
      tokenTag.putInt("restockTimer",0);
      tokenTag.putInt("sort",0); // 0 is by XP, 1 is by profession
      NbtList villagers = new NbtList();
      NbtList storage = new NbtList();
      NbtList tools = new NbtList();
      tokenTag.put("villagers",villagers);
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
   protected void doPassiveTick(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      int count = tokenData.getInt("ironVillagers");
      boolean hasIllager = tokenData.getBoolean("hasIllager");
      if(!hasIllager) return;
      int requiredItemCount = tokenData.getInt("requiredItems");
      int maxFromReqItems = getRequiredItems().size() > 0 ? (int) Math.round(requiredItemCount / getRequiredItems().get(0).getRight()) : Integer.MAX_VALUE;
      for(int i = 0; i < Math.min(maxFromReqItems,count/3); i++){
         for(ItemStack passiveProduct : passiveProducts){
            addToStorage(serverWorld,tokenBlock,passiveProduct.copy());
         }
      }
   }
   
   @Override
   protected void growBabies(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      tokenData.putInt("babyCount",0);
      List<NbtCompound> villagers = getVillagerTags(tokenData);
   
      for(NbtCompound villager : villagers){
         villager.putBoolean("baby",false);
      }
   
      storeVillagerTags(tokenData,villagers);
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
         if(reqItemList.size() > 0 && curCount != 0){
            int maxFromReqItems = (int) Math.round(requiredItemCount / reqItemList.get(0).getRight());
            if(curCount+1 > maxFromReqItems) { return false; }
         }
         
         tokenTag.putInt("count",curCount+1);
         if(entity.isBaby()){
            tokenTag.putInt("babyCount",tokenTag.getInt("babyCount")+1);
            tokenTag.putInt("growTimer",10000);
         }
         
         if(entity instanceof VillagerEntity villager){
            NbtList villagerTags = tokenTag.getList("villagers", NbtElement.COMPOUND_TYPE);
            NbtCompound villagerTag = new NbtCompound();
            villager.writeCustomDataToNbt(villagerTag);
            NbtCompound simpleTag = new NbtCompound();
            simpleTag.put("Offers",villagerTag.getCompound("Offers"));
            simpleTag.put("VillagerData",villagerTag.getCompound("VillagerData"));
            simpleTag.putInt("gossip",0);
            simpleTag.putInt("Xp",villagerTag.getInt("Xp"));
            simpleTag.putBoolean("zombie",false);
            simpleTag.putBoolean("baby", entity.isBaby());
            simpleTag.putBoolean("favorite",false);
            simpleTag.putInt("cureTime",0);
            simpleTag.putString("name",villager.hasCustomName() ? villager.getCustomName().getString() : "");
            villagerTags.add(simpleTag);
         }
         return true;
      }catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   public static NbtCompound makeBabyTag(){
      NbtCompound simpleTag = new NbtCompound();
      NbtCompound dataTag = new NbtCompound();
      dataTag.putString("type","minecraft:plains");
      dataTag.putInt("level",1);
      dataTag.putString("profession","minecraft:none");
      NbtCompound emptyOffers = new NbtCompound();
      NbtList emptyRecipes = new NbtList();
      emptyOffers.put("Recipes",emptyRecipes);
      simpleTag.put("Offers",emptyOffers);
      simpleTag.put("VillagerData",dataTag);
      simpleTag.putInt("gossip",0);
      simpleTag.putInt("Xp",0);
      simpleTag.putBoolean("zombie",false);
      simpleTag.putBoolean("baby", true);
      simpleTag.putBoolean("favorite",false);
      simpleTag.putInt("cureTime",0);
      simpleTag.putString("name","");
      return simpleTag;
   }
   
   @Override
   public void tick(ServerWorld serverWorld, TokenBlock tokenBlock){
      super.tick(serverWorld,tokenBlock);
      NbtCompound tokenData = tokenBlock.getData();
      int restockTimer = tokenData.getInt("restockTimer");
      if(restockTimer > 0){
         tokenData.putInt("restockTimer",--restockTimer);
      }
      if(restockTimer == 0){
         restock(serverWorld,tokenBlock);
         tokenData.putInt("restockTimer",restockTime);
      }
   
      List<NbtCompound> villagers = getVillagerTags(tokenData);
      for(NbtCompound villager : villagers){
         int cureTimer = villager.getInt("cureTime");
         if(cureTimer != 0){
            villager.putInt("cureTime",--cureTimer);
            if(cureTimer == 0){
               villager.putBoolean("zombie",false);
               villager.putInt("gossip",villager.getInt("gossip")+100);
            }
         }
      }
      storeVillagerTags(tokenData,villagers);
   }
   
   private void restock(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      List<NbtCompound> villagers = getVillagerTags(tokenData);
   
      for(NbtCompound villager : villagers){
         TradeOfferList offers = new TradeOfferList(villager.getCompound("Offers"));
         for(TradeOffer offer : offers){
            offer.resetUses();
         }
         villager.put("Offers",offers.toNbt());
      }
      
      storeVillagerTags(tokenData,villagers);
   }
   
   public List<NbtCompound> getVillagerTags(NbtCompound tokenData){
      NbtList villagerTags = tokenData.getList("villagers", NbtElement.COMPOUND_TYPE);
      int sortType = tokenData.getInt("sortType"); // 0 xp, 1 profession
      List<NbtCompound> villagers = new ArrayList<>();
      
      for(NbtElement ele : villagerTags){
         NbtCompound villager = (NbtCompound) ele;
         villagers.add(villager);
      }
   
      Comparator<NbtCompound> villagerComparator = (NbtCompound v1, NbtCompound v2) -> {
         int sum1 = 0,sum2 = 0;
         if(sortType == 0){
            sum1 = v1.getInt("Xp"); sum2 = v2.getInt("Xp");
         }else if(sortType == 1){
            sum1 = v1.getString("profession").compareTo(v2.getString("profession"));
         }
         if(v1.getBoolean("favorite")) sum1 += 100000;
         if(v2.getBoolean("favorite")) sum2 += 100000;
         if(v1.getBoolean("baby")) sum1 -= 10000;
         if(v2.getBoolean("baby")) sum2 -= 10000;
         if(v1.getBoolean("zombie")) sum1 -= 1000;
         if(v2.getBoolean("zombie")) sum2 -= 1000;
         
         if(sum1 == sum2){
            return v1.getString("name").compareTo(v2.getString("name"));
         }else{
            return sum2 - sum1;
         }
      };
      villagers.sort(villagerComparator);
      return villagers;
   }
   
   public void storeVillagerTags(NbtCompound tokenData, List<NbtCompound> villagers){
      NbtList villagerTags = new NbtList();
      villagerTags.addAll(villagers);
      tokenData.put("villagers",villagerTags);
   }
}
