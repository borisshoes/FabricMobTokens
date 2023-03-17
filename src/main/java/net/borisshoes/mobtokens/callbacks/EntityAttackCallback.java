package net.borisshoes.mobtokens.callbacks;

import net.borisshoes.mobtokens.Mobtokens;
import net.borisshoes.mobtokens.tokens.*;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityAttackCallback {
   public static ActionResult attackEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult){
      ItemStack item = playerEntity.getStackInHand(hand);
      try{
         if(entity instanceof MobEntity mobEntity){
            MobToken token = MobTokenUtils.identifyMobToken(item);
            if(token != null){
               //Mobtokens.log(1,"Attacking With Token: "+token.getClass().toString());
               String entityTypeId = EntityType.getId(mobEntity.getType()).toString();
               
               if(token instanceof EmptyToken){
                  if(!MobTokens.getTokenEntities().contains(entityTypeId)) return ActionResult.PASS;
                  MobToken newToken = MobTokenUtils.getTokenFromEntityId(entityTypeId);
                  if(mobEntity instanceof MooshroomEntity mooshroom){
                     if(mooshroom.getVariant() == MooshroomEntity.Type.BROWN){
                        newToken = MobTokens.BROWN_MOOSHROOM_TOKEN;
                     }else{
                        newToken = MobTokens.RED_MOOSHROOM_TOKEN;
                     }
                  }
                  
                  if(newToken == null) return ActionResult.PASS;
                  item.setNbt(newToken.getNewToken().getNbt());
                  if(newToken.addMob(item,mobEntity)){
                     if(newToken instanceof SheepToken sheepToken && mobEntity instanceof SheepEntity sheepEntity){
                        NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
                        List<Pair<DyeColor,Boolean>> sheep = sheepToken.getStoredSheep(tokenTag);
                        sheep.add(new Pair<>(sheepEntity.getColor(),mobEntity.isBaby()));
                        sheepToken.storeSheep(sheep,tokenTag);
                     }
                     
                     mobEntity.discard();
                     String entityTypeName = EntityType.get(entityTypeId).get().getName().getString();
                     //Mobtokens.log(1,"New Token Created: "+entityTypeId);
                     playerEntity.sendMessage(Text.literal("The Mob Token adapts to "+entityTypeName+"s").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                  }
               }else if(token instanceof VillagerToken){
                  //Mobtokens.log(1,"Villager");
                  if(entityTypeId.equals(token.getEntityId())){
                     if(token.addMob(item,mobEntity)){
                        mobEntity.discard();
                        //Mobtokens.log(1,"Adding Mob To Token: "+entityTypeId);
                        playerEntity.sendMessage(Text.literal("The Mob is digitized into the Token").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                     }else{
                        playerEntity.sendMessage(Text.literal("The Mob Token is full or needs more required items").formatted(Formatting.RED,Formatting.ITALIC),true);
                     }
                  }else if(mobEntity instanceof ZombieEntity){
                     NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
                     if(tokenTag.getBoolean("hasZombie")){
                        playerEntity.sendMessage(Text.literal("The Token already contains a Zombie").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                     }else{
                        mobEntity.discard();
                        tokenTag.putBoolean("hasZombie",true);
                        //Mobtokens.log(1,"Adding Mob To Token: "+entityTypeId);
                        playerEntity.sendMessage(Text.literal("The Zombie is digitized into the Token. Curing is now available").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                     }
                  }else if(mobEntity instanceof IllagerEntity){
                     NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
                     if(tokenTag.getBoolean("hasIllager")){
                        playerEntity.sendMessage(Text.literal("The Token already contains an Illager").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                     }else{
                        mobEntity.discard();
                        tokenTag.putBoolean("hasIllager",true);
                        //Mobtokens.log(1,"Adding Mob To Token: "+entityTypeId);
                        playerEntity.sendMessage(Text.literal("The Illager is digitized into the Token. The Iron Farm is now available").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                     }
                  }
               }else{
                  boolean mooshroomCheck = true;
                  if(mobEntity instanceof MooshroomEntity mooshroom){
                     if(mooshroom.getVariant() == MooshroomEntity.Type.BROWN){
                        mooshroomCheck = token instanceof BrownMooshroomToken;
                     }else{
                        mooshroomCheck = token instanceof RedMooshroomToken;
                     }
                  }
                  
                  if(entityTypeId.equals(token.getEntityId()) && mooshroomCheck){
                     if(token.addMob(item,mobEntity)){
                        if(token instanceof SheepToken sheepToken && mobEntity instanceof SheepEntity sheepEntity){
                           NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
                           List<Pair<DyeColor,Boolean>> sheep = sheepToken.getStoredSheep(tokenTag);
                           sheep.add(new Pair<>(sheepEntity.getColor(),mobEntity.isBaby()));
                           sheepToken.storeSheep(sheep,tokenTag);
                        }
                        
                        //Mobtokens.log(1,"Adding Mob To Token: "+entityTypeId);
                        playerEntity.sendMessage(Text.literal("The Mob is digitized into the Token").formatted(Formatting.GOLD,Formatting.ITALIC),true);
                        mobEntity.discard();
                     }else{
                        playerEntity.sendMessage(Text.literal("The Mob Token is full or needs more required items").formatted(Formatting.RED,Formatting.ITALIC),true);
                     }
                  }
               }
      
            }
         }
         
         
         return ActionResult.PASS;
      }catch(Exception e){
         e.printStackTrace();
         return ActionResult.PASS;
      }
   }
}
