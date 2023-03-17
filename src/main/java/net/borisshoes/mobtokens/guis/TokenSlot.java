package net.borisshoes.mobtokens.guis;

import net.borisshoes.mobtokens.tokens.MobToken;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;

public class TokenSlot extends Slot {
   
   private final MobToken token;
   private final TokenGui.TokenGuiMode mode;
   
   public TokenSlot(MobToken token, TokenGui.TokenGuiMode mode, Inventory inventory, int index, int x, int y){
      super(inventory, index, x, y);
      this.token = token;
      this.mode = mode;
   }
   
   @Override
   public boolean canInsert(ItemStack stack){
      if(mode == TokenGui.TokenGuiMode.STORAGE){
         return false;
      }else if(mode == TokenGui.TokenGuiMode.BREEDING){
         for(ItemStack food : token.getFoods()){
            if(stack.isOf(food.getItem())) return true;
         }
         for(Pair<ItemStack, Double> pair : token.getRequiredItems()){
            if(stack.isOf(pair.getLeft().getItem())) return true;
         }
      }else if(mode == TokenGui.TokenGuiMode.TOOLS){
         for(Pair<ItemStack, ItemStack> pair : token.getActiveProducts()){
            if(stack.isOf(pair.getLeft().getItem())) return true;
         }
      }
      
      return false;
   }
}
