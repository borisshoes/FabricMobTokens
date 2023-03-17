package net.borisshoes.mobtokens.tokens;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobTokens {
   public static final HashMap<String,MobToken> registry = new HashMap<>();
   
   //public static final MobToken _TOKEN = MobTokens.register("", new());
   public static final MobToken BEE_TOKEN = MobTokens.register("bee", new BeeToken());
   public static final MobToken CHICKEN_TOKEN = MobTokens.register("chicken", new ChickenToken());
   public static final MobToken COW_TOKEN = MobTokens.register("cow", new CowToken());
   public static final MobToken RED_MOOSHROOM_TOKEN = MobTokens.register("red_mooshroom", new RedMooshroomToken());
   public static final MobToken BROWN_MOOSHROOM_TOKEN = MobTokens.register("brown_mooshroom", new BrownMooshroomToken());
   public static final MobToken PIG_TOKEN = MobTokens.register("pig", new PigToken());
   public static final MobToken RABBIT_TOKEN = MobTokens.register("rabbit", new RabbitToken());
   public static final MobToken SHEEP_TOKEN = MobTokens.register("sheep", new SheepToken());
   public static final MobToken TURTLE_TOKEN = MobTokens.register("turtle", new TurtleToken());
   public static final MobToken PIGLIN_TOKEN = MobTokens.register("piglin", new PiglinToken());
   public static final MobToken VILLAGER_TOKEN = MobTokens.register("villager", new VillagerToken());
   public static final MobToken EMPTY_TOKEN = MobTokens.register("empty", new EmptyToken());
   
   private static MobToken register(String id, MobToken item){
      registry.put(id,item);
      return item;
   }
   
   public static List<String> getTokenEntities(){
      List<String> entityList = new ArrayList<>();
      for(MobToken token : registry.values()){
         entityList.add(token.getEntityId());
      }
      
      return entityList;
   }
}
