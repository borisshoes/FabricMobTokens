package net.borisshoes.mobtokens.callbacks;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.borisshoes.mobtokens.Mobtokens;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenGui;
import net.borisshoes.mobtokens.guis.TokenRelatedGui;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.MobTokens;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

import static net.borisshoes.mobtokens.Mobtokens.OPEN_GUIS;
import static net.borisshoes.mobtokens.cardinalcomponents.WorldDataComponentInitializer.TOKEN_BLOCK_COMPONENT;

public class WorldTickCallback {
   public static void onWorldTick(ServerWorld serverWorld){
      try{
         // Token Block Tick
         List<TokenBlock> blocks = TOKEN_BLOCK_COMPONENT.get(serverWorld).getBlocks();
         Iterator<TokenBlock> iter = blocks.iterator();
         while(iter.hasNext()){
            TokenBlock tokenBlock = iter.next();
            BlockPos pos = tokenBlock.getPos();
            long chunkPosL = ChunkPos.toLong(pos);
            ChunkPos chunkPos = new ChunkPos(pos);
            
            //System.out.println(serverWorld.isChunkLoaded(chunkPos.x,chunkPos.z));
            if(serverWorld.shouldTickBlocksInChunk(chunkPosL)){ // Only tick blocks in loaded chunks
               NbtCompound blockData = tokenBlock.getData();
               if(blockData.contains("id")){
                  String id = blockData.getString("id");
                  BlockState state = serverWorld.getBlockState(pos);
                  if(!blockData.contains("UUID")){
                     blockData.putString("UUID", UUID.randomUUID().toString());
                  }
   
                  if(state.getBlock().asItem() == Items.PLAYER_HEAD){ // First check that the block is still there
                     MobToken token = MobTokens.registry.get(id);
                     token.tick(serverWorld, tokenBlock);
                  }else{ // If block is no longer there remove it from the blocklist.
                     iter.remove();
                  }
               }
            }
         }
   
         Iterator<Map.Entry<ServerPlayerEntity, TokenRelatedGui>> iter2 = OPEN_GUIS.entrySet().iterator();
         while(iter2.hasNext()){
            Map.Entry<ServerPlayerEntity, TokenRelatedGui> openGui = iter2.next();
            ServerPlayerEntity player = openGui.getKey();
            SimpleGui gui = openGui.getValue().getGui();
            TokenBlock block = openGui.getValue().getTokenBlock();
            
            BlockState state = player.getWorld().getBlockState(block.getPos());
            
            if(serverWorld.getRegistryKey().getValue().equals(player.getWorld().getRegistryKey().getValue())){
//               System.out.println("Open Gui: ");
//               System.out.println(openGui.getValue().getTokenBlock().getPos());
//               System.out.println(state.getBlock()+" "+player.getWorld().getRegistryKey().getValue());
               
               if(state.getBlock().asItem() != Items.PLAYER_HEAD){
                  openGui.getValue().close();
               }
               if(player.currentScreenHandler == player.playerScreenHandler){
                  block.setGuiOpen(false);
                  iter2.remove();
               }else{
                  //System.out.println("Rebuilding");
                  if(gui instanceof TokenGui tokenGui){
                     tokenGui.rebuildGui();
                  }
               }
   
            }
         }
         
         
      }catch(Exception e){
         e.printStackTrace();
      }
   }
}
