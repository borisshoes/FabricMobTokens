package net.borisshoes.mobtokens.guis;

import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;

import static net.borisshoes.mobtokens.Mobtokens.MAX_MOBS;

public class TokenInventoryListener implements InventoryChangedListener {
   
   private final TokenBlock tokenBlock;
   private final MobToken token;
   private final TokenGui gui;
   private boolean updating = false;
   
   public TokenInventoryListener(TokenBlock tokenBlock, MobToken token, TokenGui gui){
      this.token = token;
      this.tokenBlock = tokenBlock;
      this.gui = gui;
   }
   
   @Override
   public void onInventoryChanged(Inventory inv){
      if(updating) return;
      NbtCompound tokenData = tokenBlock.getData();
      if(gui.getMode() == TokenGui.TokenGuiMode.BREEDING){
         ItemStack stack = inv.getStack(0);
         int food = tokenData.getInt("food");
         int reqItems = tokenData.getInt("requiredItems");
         
         if(!stack.isEmpty()){
            for(ItemStack foodItem : token.getFoods()){
               if(stack.isOf(foodItem.getItem())){
                  tokenData.putInt("food",food+stack.getCount());
                  inv.setStack(0,ItemStack.EMPTY);
                  break;
               }
            }
            for(Pair<ItemStack, Double> pair : token.getRequiredItems()){
               ItemStack reqStack = pair.getLeft();
               if(stack.isOf(reqStack.getItem())){
                  if(reqStack.isOf(Items.BEE_NEST) || reqStack.isOf(Items.BEEHIVE)){
                     NbtCompound nestNbt = stack.getNbt();
                     if(nestNbt != null && nestNbt.contains("BlockEntityTag")){
                        NbtCompound bet = nestNbt.getCompound("BlockEntityTag");
                        if(bet.contains("Bees")){
                           NbtList bees = bet.getList("Bees", NbtElement.COMPOUND_TYPE);
                           int curCount = tokenData.getInt("count");
                           tokenData.putInt("count",Math.min(MAX_MOBS,curCount+bees.size()*stack.getCount()));
                        }
                     }
                  }
                  tokenData.putInt("requiredItems",reqItems+stack.getCount());
                  inv.setStack(0,ItemStack.EMPTY);
                  break;
               }
            }
         }
      }else if(gui.getMode() == TokenGui.TokenGuiMode.TOOLS){
         NbtList tools = new NbtList();
         for(int i = 0; i < inv.size(); i++){
            ItemStack stack = inv.getStack(i);
            if(stack.isEmpty()) continue;
            NbtCompound stackNbt = stack.writeNbt(new NbtCompound());
            stackNbt.putByte("Slot", (byte) i);
            tools.add(stackNbt);
         }
         tokenData.put("tools",tools);
      }else if(gui.getMode() == TokenGui.TokenGuiMode.STORAGE){
         NbtList storage = new NbtList();
         for(int i = 0; i < inv.size(); i++){
            ItemStack stack = inv.getStack(i);
            if(stack.isEmpty()) continue;
            NbtCompound stackNbt = stack.writeNbt(new NbtCompound());
            stackNbt.putByte("Slot", (byte) i);
            storage.add(stackNbt);
         }
         tokenData.put("storage",storage);
      }
      gui.rebuildGui();
   }
   
   public void finishUpdate(){
      updating = false;
   }
   public void setUpdating(){
      updating = true;
   }
}
