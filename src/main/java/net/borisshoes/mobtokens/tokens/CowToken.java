package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class CowToken extends MobToken{
   public CowToken(){
      name = "Cow Token";
      id = "cow";
      entityId = "minecraft:cow";
      displayName = Text.literal("Cow Token").formatted(Formatting.BOLD,Formatting.DARK_GRAY);
      mobHead = MobHeadUtils.COW;
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.WHEAT));
   
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      activeProducts.add(new Pair<>(new ItemStack(Items.BUCKET),new ItemStack(Items.MILK_BUCKET)));
      requiredItems = new ArrayList<>();
   
      passiveTickTime = 0;
      activeTickTime = 20;
   }
}
