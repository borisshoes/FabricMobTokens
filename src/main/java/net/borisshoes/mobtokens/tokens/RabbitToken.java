package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class RabbitToken extends MobToken{
   public RabbitToken(){
      name = "Rabbit Token";
      id = "rabbit";
      entityId = "minecraft:rabbit";
      displayName = Text.literal("Rabbit Token").formatted(Formatting.BOLD,Formatting.AQUA);
      mobHead = MobHeadUtils.RABBIT;
   
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.CARROT));
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      requiredItems = new ArrayList<>();
      passiveTickTime = 0;
      activeTickTime = 0;
   }
}
