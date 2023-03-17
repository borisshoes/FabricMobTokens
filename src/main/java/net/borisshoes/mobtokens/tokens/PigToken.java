package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
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

public class PigToken extends MobToken{
   public PigToken(){
      name = "Pig Token";
      id = "pig";
      entityId = "minecraft:pig";
      displayName = Text.literal("Pig Token").formatted(Formatting.BOLD,Formatting.LIGHT_PURPLE);
      mobHead = MobHeadUtils.PIG;
   
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.CARROT));
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      requiredItems = new ArrayList<>();
      passiveTickTime = 0;
      activeTickTime = 0;
   }
}
