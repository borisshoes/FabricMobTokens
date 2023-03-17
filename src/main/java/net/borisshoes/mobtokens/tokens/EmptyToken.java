package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class EmptyToken extends MobToken{
   public EmptyToken(){
      name = "Empty Token";
      id = "empty";
      entityId = "";
      displayName = Text.literal("Empty Mob Token").formatted(Formatting.BOLD,Formatting.GRAY);
      mobHead = MobHeadUtils.EMPTY_TOKEN;
      foods = new ArrayList<>();
      passiveProducts = new ArrayList<>();
      activeProducts = new ArrayList<>();
      requiredItems = new ArrayList<>();
      passiveTickTime = 0;
      activeTickTime = 0;
   }
}
