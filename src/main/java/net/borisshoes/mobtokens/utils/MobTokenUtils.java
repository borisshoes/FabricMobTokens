package net.borisshoes.mobtokens.utils;

import net.borisshoes.mobtokens.Mobtokens;
import net.borisshoes.mobtokens.tokens.EmptyToken;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.MobTokens;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class MobTokenUtils {
   
   public static boolean isMobToken(ItemStack item){
      try{
         NbtCompound itemNbt = item.getNbt();
         if(itemNbt == null)
            return false;
      
         NbtCompound tokenTag = itemNbt.getCompound("mobtokens");
         if(tokenTag != null){
            // We know that the item is a token, verify its id
            String id = tokenTag.getString("id");
            if(id==null)
               return false;
            return (MobTokens.registry.get(id) != null);
         }
      
         return false;
      }catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   public static MobToken identifyMobToken(ItemStack item){
      try{
         NbtCompound itemNbt = item.getNbt();
         if(itemNbt == null)
            return null;
      
         NbtCompound tokenTag = itemNbt.getCompound("mobtokens");
         if(tokenTag != null){
            // We know that the item is a token, verify its id
            String id = tokenTag.getString("id");
            if(id==null)
               return null;
            return MobTokens.registry.get(id);
         }
      
         return null;
      }catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   public static boolean isEmptyToken(ItemStack item){
      return identifyMobToken(item) instanceof EmptyToken;
   }
   
   public static MobToken getTokenFromEntityId(String id){
      for(MobToken token : MobTokens.registry.values()){
         if(id.equals(token.getEntityId())) return token;
      }
      return null;
   }
}
