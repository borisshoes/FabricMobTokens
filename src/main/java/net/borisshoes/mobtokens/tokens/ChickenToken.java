package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

public class ChickenToken extends MobToken{
   public ChickenToken(){
      name = "Chicken Token";
      id = "chicken";
      entityId = "minecraft:chicken";
      displayName = Text.literal("Chicken Token").formatted(Formatting.BOLD,Formatting.WHITE);
      mobHead = MobHeadUtils.CHICKEN;
   
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.BEETROOT_SEEDS));
      foods.add(new ItemStack(Items.WHEAT_SEEDS));
      foods.add(new ItemStack(Items.PUMPKIN_SEEDS));
      foods.add(new ItemStack(Items.MELON_SEEDS));
      
      passiveProducts = new ArrayList<>();
      passiveProducts.add(new ItemStack(Items.EGG));
      activeProducts = new ArrayList<>();
      requiredItems = new ArrayList<>();
      
      passiveTickTime = 9000;
      activeTickTime = 0;
   }
}
