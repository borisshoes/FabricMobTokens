package net.borisshoes.mobtokens.callbacks;

import net.borisshoes.mobtokens.Mobtokens;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenGui;
import net.borisshoes.mobtokens.tokens.EmptyToken;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.MobTokens;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

import static net.borisshoes.mobtokens.cardinalcomponents.WorldDataComponentInitializer.TOKEN_BLOCK_COMPONENT;

public class BlockUseCallback {
   public static ActionResult useBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult){
      ItemStack item = playerEntity.getStackInHand(hand);
      //System.out.println(hand+" "+item);
      ActionResult result = ActionResult.PASS;
      try{
         MobToken token = MobTokenUtils.identifyMobToken(item);
         if(token != null){
            if(token instanceof EmptyToken){
               result = ActionResult.SUCCESS;
            }else{
               //Mobtokens.log(1,"Placing With Token");
   
               Direction side = blockHitResult.getSide();
               BlockPos placePos = blockHitResult.getBlockPos().add(side.getVector());
               List<LivingEntity> entitiesInBlock = world.getNonSpectatingEntities(LivingEntity.class, new Box(placePos));
               boolean placeable = world.getBlockState(placePos).canReplace(new ItemPlacementContext(playerEntity, hand, item, blockHitResult)) && entitiesInBlock.size() == 0;
               
               if(!(placeable && playerEntity instanceof ServerPlayerEntity player && token.placeToken(world,placePos,item))){
                  playerEntity.sendMessage(Text.literal("The Mob Token cannot be placed here.").formatted(Formatting.RED,Formatting.ITALIC),true);
                  result = ActionResult.SUCCESS;
               }
               
            }
         }
         
         if(!playerEntity.isSneaking()){
            // Magic Block check
            List<TokenBlock> blocks = TOKEN_BLOCK_COMPONENT.get(world).getBlocks();
            for(TokenBlock tokenBlock : blocks){
               if(tokenBlock.getPos().equals(blockHitResult.getBlockPos())){
                  NbtCompound blockData = tokenBlock.getData();
                  if(blockData.contains("id")){
                     String id = blockData.getString("id");
                     token = MobTokens.registry.get(id);
                     //Mobtokens.log(1,"Interacting With Placed Token");
                     if(playerEntity instanceof ServerPlayerEntity player){
                        if(hand == Hand.MAIN_HAND){
                           if(!tokenBlock.isGuiOpen()){
                              token.openGui(player,tokenBlock, TokenGui.TokenGuiMode.MAIN_MENU);
                           }else{
                              playerEntity.sendMessage(Text.literal("Someone else is using the token").formatted(Formatting.RED,Formatting.ITALIC),true);
                           }
                        }
                     }
                     result = ActionResult.SUCCESS;
                  }
               }
            }
         }
         
         if(result != ActionResult.PASS){
            if(playerEntity instanceof ServerPlayerEntity player){
               player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40, item));
            }
         }
         return result;
      }catch(Exception e){
         e.printStackTrace();
         return ActionResult.PASS;
      }
   }
}
