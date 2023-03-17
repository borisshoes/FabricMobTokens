package net.borisshoes.mobtokens.utils;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;

import java.util.HashSet;
import java.util.Map;

public class TradeGenerationUtils {
   
   public static boolean canLevelUp(NbtCompound data) {
      NbtCompound villagerData = data.getCompound("VillagerData");
      int i = villagerData.getInt("level");
      return VillagerData.canLevelUp(i) && data.getInt("Xp") >= VillagerData.getUpperLevelExperience(i);
   }
   
   public static void levelUp(ServerWorld world, NbtCompound data,TradeOfferList offerList) {
      NbtCompound villagerData = data.getCompound("VillagerData");
      villagerData.putInt("level",villagerData.getInt("level")+1);
      fillRecipes(world,data,offerList);
   }
   
   public static void fillRecipes(ServerWorld world, NbtCompound data,TradeOfferList offerList) {
      NbtCompound villagerData = data.getCompound("VillagerData");
      int lvl = villagerData.getInt("level");
      String prof = villagerData.getString("profession").replace("minecraft:","");
      VillagerProfession profession = null;
      
      for(Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> e : Registries.VILLAGER_PROFESSION.getEntrySet()){
         VillagerProfession p = e.getValue();
         if(p.id().equals(prof)){
            profession = p;
            break;
         }
      }
      
      Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);
      if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
         return;
      }
      TradeOffers.Factory[] factorys = (TradeOffers.Factory[])int2ObjectMap.get(lvl);
      if (factorys == null) {
         return;
      }
      fillRecipesFromPool(world,offerList, factorys, 2);
      data.put("Offers",offerList.toNbt());
   }
   
   public static void fillRecipesFromPool(ServerWorld world, TradeOfferList recipeList, TradeOffers.Factory[] pool, int count) {
      VillagerEntity entity = EntityType.VILLAGER.create(world);
      Random random = Random.create();
      HashSet<Integer> set = Sets.newHashSet();
      if (pool.length > count) {
         while (set.size() < count) {
            set.add(random.nextInt(pool.length));
         }
      } else {
         for (int i = 0; i < pool.length; ++i) {
            set.add(i);
         }
      }
      for (Integer integer : set) {
         TradeOffers.Factory factory = pool[integer];
         TradeOffer tradeOffer = factory.create(entity, random);
         if (tradeOffer == null) continue;
         recipeList.add(tradeOffer);
      }
   }
}
