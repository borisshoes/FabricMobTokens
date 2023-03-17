package net.borisshoes.mobtokens;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.borisshoes.mobtokens.callbacks.BlockUseCallback;
import net.borisshoes.mobtokens.callbacks.EntityAttackCallback;
import net.borisshoes.mobtokens.callbacks.TickCallback;
import net.borisshoes.mobtokens.callbacks.WorldTickCallback;
import net.borisshoes.mobtokens.guis.TokenGui;
import net.borisshoes.mobtokens.guis.TokenRelatedGui;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Mobtokens implements ModInitializer {
   
   public static final Logger logger = LogManager.getLogger("Mob Tokens");
   public static final int MAX_MOBS = 64;
   public static final HashMap<ServerPlayerEntity, TokenRelatedGui> OPEN_GUIS = new HashMap<>();
   
   @Override
   public void onInitialize(){
   
      ServerTickEvents.END_WORLD_TICK.register(WorldTickCallback::onWorldTick);
      UseBlockCallback.EVENT.register(BlockUseCallback::useBlock);
      AttackEntityCallback.EVENT.register(EntityAttackCallback::attackEntity);
      ServerTickEvents.END_SERVER_TICK.register(TickCallback::onTick);
   
      logger.info("Tokenizing Mobs");
   }
   
   
   /**
    * Uses built in logger to log a message
    * @param level 0 - Info | 1 - Warn | 2 - Error | 3 - Fatal | Else - Debug
    * @param msg  The {@code String} to be printed.
    */
   public static void log(int level, String msg){
      switch(level){
         case 0 -> logger.info(msg);
         case 1 -> logger.warn(msg);
         case 2 -> logger.error(msg);
         case 3 -> logger.fatal(msg);
         default -> logger.debug(msg);
      }
   }
}
