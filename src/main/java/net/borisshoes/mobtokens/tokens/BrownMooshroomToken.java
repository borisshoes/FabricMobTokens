package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenInventory;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class BrownMooshroomToken extends MobToken{
   public BrownMooshroomToken(){
      name = "Brown Mooshroom Token";
      id = "brown_mooshroom";
      entityId = "minecraft:mooshroom";
      displayName = Text.literal("Brown Mooshroom Token").formatted(Formatting.BOLD,Formatting.GRAY);
      mobHead = MobHeadUtils.BROWN_MOOSHROOM;
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.WHEAT));
      
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      activeProducts.add(new Pair<>(new ItemStack(Items.BUCKET),new ItemStack(Items.MILK_BUCKET)));
      activeProducts.add(new Pair<>(new ItemStack(Items.BOWL),new ItemStack(Items.SUSPICIOUS_STEW)));
      requiredItems = new ArrayList<>();
      
      passiveTickTime = 0;
      activeTickTime = 20;
   
   }
   
   protected void doActiveTick(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      NbtList tools = tokenData.getList("tools", NbtElement.COMPOUND_TYPE);
      TokenInventory inv = new TokenInventory();
      for(NbtElement elem : tools){
         NbtCompound toolsNbt = (NbtCompound) elem;
         inv.setStack(toolsNbt.getByte("Slot"),ItemStack.fromNbt(toolsNbt));
      }
      
      int count = tokenData.getInt("count");
      int babyCount = tokenData.getInt("babyCount");
      int requiredItemCount = tokenData.getInt("requiredItems");
      int maxFromReqItems = getRequiredItems().size() > 0 ? (int) Math.round(requiredItemCount / getRequiredItems().get(0).getRight()) : Integer.MAX_VALUE;
      for(int i = 0; i < Math.min(maxFromReqItems,count-babyCount); i++){
         boolean cont = true;
         
         for(Pair<ItemStack, ItemStack> pair : activeProducts){
            if(!cont) break;
            ItemStack toolRequired = pair.getLeft().copy();
            Item toolItem = toolRequired.getItem();
            if(inv.count(toolItem) >= toolRequired.getCount()){
               ItemStack result = pair.getRight().copy();
               if(result.isOf(Items.SUSPICIOUS_STEW)){
                  List<SuspiciousStewIngredient> allStews = SuspiciousStewIngredient.getAll();
                  SuspiciousStewIngredient randomStew = allStews.get((int) (Math.random()*allStews.size()));
                  SuspiciousStewItem.addEffectToStew(result, randomStew.getEffectInStew(), randomStew.getEffectInStewDuration());
               }
               
               inv.removeItem(toolItem,toolRequired.getCount());
               addToStorage(serverWorld,tokenBlock,result);
               break;
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
}
