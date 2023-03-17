package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class TurtleToken extends MobToken{
   public TurtleToken(){
      name = "Turtle Token";
      id = "turtle";
      entityId = "minecraft:turtle";
      displayName = Text.literal("Turtle Token").formatted(Formatting.BOLD,Formatting.DARK_GREEN);
      mobHead = MobHeadUtils.TURTLE;
   
      foods = new ArrayList<>();
      foods.add(new ItemStack(Items.SEAGRASS));
      passiveProducts = new ArrayList<>();
      passiveProducts.add(new ItemStack(Items.TURTLE_EGG));
      activeProducts = new ArrayList<>();
      requiredItems = new ArrayList<>();
      requiredItems.add(new Pair<>(new ItemStack(Items.SAND), 1.0));
      
      passiveTickTime = 16000;
      activeTickTime = 0;
   }
   
   @Override
   public void growBabies(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      tokenData.putInt("babyCount",0);
      int babyCount = tokenData.getInt("babyCount");
      addToStorage(serverWorld,tokenBlock,new ItemStack(Items.SCUTE,babyCount));
   }
}
