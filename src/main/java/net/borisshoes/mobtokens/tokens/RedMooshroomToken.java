package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class RedMooshroomToken extends MobToken{
   public RedMooshroomToken(){
      name = "Red Mooshroom Token";
      id = "red_mooshroom";
      entityId = "minecraft:mooshroom";
      displayName = Text.literal("Red Mooshroom Token").formatted(Formatting.BOLD,Formatting.RED);
      mobHead = MobHeadUtils.RED_MOOSHROOM;
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.WHEAT));
   
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      activeProducts.add(new Pair<>(new ItemStack(Items.BUCKET),new ItemStack(Items.MILK_BUCKET)));
      activeProducts.add(new Pair<>(new ItemStack(Items.BOWL),new ItemStack(Items.MUSHROOM_STEW)));
      requiredItems = new ArrayList<>();
   
      passiveTickTime = 0;
      activeTickTime = 20;
   }
}
