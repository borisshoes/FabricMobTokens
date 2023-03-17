package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.entity.passive.BeeEntity;
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

public class BeeToken extends MobToken {
   public BeeToken(){
      name = "Bee Token";
      id = "bee";
      entityId = "minecraft:bee";
      displayName = Text.literal("Bee Token").formatted(Formatting.BOLD,Formatting.YELLOW);
      mobHead = MobHeadUtils.BEE;
      
      foods = new ArrayList<>();
      Optional<RegistryEntryList.Named<Item>> opt = Registries.ITEM.getEntryList(ItemTags.FLOWERS);
      opt.ifPresent(named -> named.stream().forEach(registryEntry -> foods.add(new ItemStack(registryEntry.value()))));
      
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      activeProducts.add(new Pair<>(new ItemStack(Items.SHEARS),new ItemStack(Items.HONEYCOMB,3)));
      activeProducts.add(new Pair<>(new ItemStack(Items.GLASS_BOTTLE),new ItemStack(Items.HONEY_BOTTLE)));
      requiredItems = new ArrayList<>();
      requiredItems.add(new Pair<>(new ItemStack(Items.BEE_NEST), 0.3334));
      requiredItems.add(new Pair<>(new ItemStack(Items.BEEHIVE), 0.3334));
      
      passiveTickTime = 0;
      activeTickTime = 2400;
   }
}
