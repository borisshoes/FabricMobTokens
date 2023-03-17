package net.borisshoes.mobtokens.mixins;

import net.borisshoes.mobtokens.tokens.MobTokens;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin {
   
   @Inject(method="craft(Lnet/minecraft/inventory/CraftingInventory;)Lnet/minecraft/item/ItemStack;", at= @At("RETURN"), cancellable = true)
   public void mobtokens_tokenCraft(CraftingInventory craftingInventory, CallbackInfoReturnable<ItemStack> cir){
      ShapedRecipe recipe = (ShapedRecipe) (Object) this;
      if(recipe.getId().toString().equals("mobtokens:mob_token")){
         ItemStack emptyToken = MobHeadUtils.EMPTY_TOKEN.copy();
         emptyToken = MobTokens.EMPTY_TOKEN.addTokenNbt(emptyToken);
         cir.setReturnValue(emptyToken);
      }
   }
   
   @Inject(method="getOutput", at= @At("RETURN"), cancellable = true)
   public void mobtokens_tokenOutput(CallbackInfoReturnable<ItemStack> cir){
      ShapedRecipe recipe = (ShapedRecipe) (Object) this;
      if(recipe.getId().toString().equals("mobtokens:mob_token")){
         ItemStack emptyToken = MobHeadUtils.EMPTY_TOKEN.copy();
         emptyToken = MobTokens.EMPTY_TOKEN.addTokenNbt(emptyToken);
         cir.setReturnValue(emptyToken);
      }
   }
}
