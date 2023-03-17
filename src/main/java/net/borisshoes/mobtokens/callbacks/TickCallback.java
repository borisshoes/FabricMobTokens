package net.borisshoes.mobtokens.callbacks;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.virtual.inventory.VirtualScreenHandler;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenGui;
import net.borisshoes.mobtokens.guis.TokenRelatedGui;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.borisshoes.mobtokens.Mobtokens.OPEN_GUIS;

public class TickCallback {
   
   public static void onTick(MinecraftServer server){
      try{
         List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
         for(ServerPlayerEntity player : players){
            PlayerInventory inv = player.getInventory();
            for(int i=0; i<inv.size();i++){
               ItemStack item = inv.getStack(i);
               if(item.isEmpty()){
                  if(item.getNbt() != null){
                     item.setNbt(null);
                  }else{
                     continue;
                  }
               }
            }
         }
      }catch(Exception e){
         e.printStackTrace();
      }
   }
}
