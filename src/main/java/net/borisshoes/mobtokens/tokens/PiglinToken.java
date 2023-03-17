package net.borisshoes.mobtokens.tokens;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenInventory;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.entity.mob.PiglinBrain.BARTERING_ITEM;

public class PiglinToken extends MobToken{
   public PiglinToken(){
      name = "Piglin Token";
      id = "piglin";
      entityId = "minecraft:piglin";
      displayName = Text.literal("Piglin Token").formatted(Formatting.BOLD,Formatting.GOLD);
      mobHead = MobHeadUtils.PIGLIN;
      foods = new ArrayList<>();
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      activeProducts.add(new Pair<>(new ItemStack(BARTERING_ITEM),new ItemStack(BARTERING_ITEM)));
      requiredItems = new ArrayList<>();
   
      passiveTickTime = 0;
      activeTickTime = 120;
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
               if(toolRequired.isOf(BARTERING_ITEM)){
                  PiglinEntity piglin = EntityType.PIGLIN.create(serverWorld);
                  LootTable lootTable = serverWorld.getServer().getLootManager().getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
                  ObjectArrayList<ItemStack> list = lootTable.generateLoot(new LootContext.Builder(serverWorld).parameter(LootContextParameters.THIS_ENTITY, piglin).random(serverWorld.random).build(LootContextTypes.BARTER));
                  
                  for(ItemStack itemStack : list){
                     addToStorage(serverWorld,tokenBlock,itemStack);
                  }
                  inv.removeItem(toolItem,toolRequired.getCount());
                  break;
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
}
