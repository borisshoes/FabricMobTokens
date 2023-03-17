package net.borisshoes.mobtokens.mixins;

import net.borisshoes.mobtokens.Mobtokens;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenGui;
import net.borisshoes.mobtokens.guis.TokenRelatedGui;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.borisshoes.mobtokens.Mobtokens.OPEN_GUIS;
import static net.borisshoes.mobtokens.cardinalcomponents.WorldDataComponentInitializer.TOKEN_BLOCK_COMPONENT;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
   
   @Inject(method = "getDroppedStacks",at = @At("HEAD"),cancellable = true)
   private void mobtokens_dropToken(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir){
      try{
         ServerWorld world = builder.getWorld();
         Vec3d pos = builder.get(LootContextParameters.ORIGIN);
      
         if(state.isOf(Blocks.PLAYER_HEAD) || state.isOf(Blocks.PLAYER_WALL_HEAD)){
            List<TokenBlock> blocks = TOKEN_BLOCK_COMPONENT.get(world).getBlocks();
            Iterator<TokenBlock> iter = blocks.iterator();
            while(iter.hasNext()){
               TokenBlock tokenBlock = iter.next();
               if(Vec3d.ofCenter(tokenBlock.getPos()).equals(pos)){
                  //Mobtokens.log(1,"Breaking Token");
                  List<ItemStack> stacks = new ArrayList<>();
                  stacks.add(tokenBlock.convertToStack());
                  
                  cir.setReturnValue(stacks);
                  iter.remove();
                  return;
               }
            }
         }
      }catch(Exception e){
         e.printStackTrace();
      }
   }
}
