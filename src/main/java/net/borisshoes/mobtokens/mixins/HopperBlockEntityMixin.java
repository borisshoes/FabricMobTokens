package net.borisshoes.mobtokens.mixins;

import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.TokenInventory;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.MobTokens;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static net.borisshoes.mobtokens.cardinalcomponents.WorldDataComponentInitializer.TOKEN_BLOCK_COMPONENT;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
   
   @Shadow
   private static native Inventory getOutputInventory(World world, BlockPos pos, BlockState state);
   
   @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getOutputInventory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/inventory/Inventory;"),
         method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z",
         locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
   private static void hookInsert(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
      // Let vanilla handle the transfer if it found an inventory.
      if (getOutputInventory(world, pos, state) != null) return;
      
      // Otherwise inject our transfer logic.
      Direction direction = state.get(HopperBlock.FACING);
      BlockPos targetPos = pos.offset(direction);
   
      if(world instanceof ServerWorld serverWorld){
         List<TokenBlock> blocks = TOKEN_BLOCK_COMPONENT.get(serverWorld).getBlocks();
         for(TokenBlock tokenBlock : blocks){
            BlockPos tokenPos = tokenBlock.getPos();
            
            if(tokenPos.equals(targetPos)){
               NbtCompound blockData = tokenBlock.getData();
               if(blockData.contains("id")){
                  String id = blockData.getString("id");
                  BlockState tokenState = serverWorld.getBlockState(tokenPos);
      
                  if(tokenState.getBlock().asItem() == Items.PLAYER_HEAD){
                     MobToken token = MobTokens.registry.get(id);
                     NbtList tools = blockData.getList("tools", NbtElement.COMPOUND_TYPE);
                     
                     SimpleInventory inv = new SimpleInventory(7);
                     for(int i = 0; i < inv.size(); i++){
                        inv.setStack(i, ItemStack.EMPTY);
                     }
                     for(NbtElement elem : tools){
                        NbtCompound toolNbt = (NbtCompound) elem;
                        ItemStack stack = ItemStack.fromNbt(toolNbt);
                        inv.setStack(MathHelper.clamp(toolNbt.getByte("Slot")-10,0,6),stack);
                     }
                     
                     long moved = StorageUtil.move(
                           InventoryStorage.of(inventory, direction),
                           InventoryStorage.of(inv, null),
                           iv -> {
                              for(Pair<ItemStack, ItemStack> pair : token.getActiveProducts()){
                                 if(iv.isOf(pair.getLeft().getItem())){
                                    return true;
                                 }
                              }
                              return false;
                           },
                           1,
                           null
                     );
                     boolean success = moved == 1;
                     
                     if(success){
                        NbtList newTools = new NbtList();
                        for(int i = 0; i < inv.size(); i++){
                           ItemStack stack = inv.getStack(i);
                           if(stack.isEmpty()) continue;
                           NbtCompound stackNbt = stack.writeNbt(new NbtCompound());
                           stackNbt.putByte("Slot", (byte)MathHelper.clamp(i+10,10,16));
                           newTools.add(stackNbt);
                        }
                        blockData.put("tools",newTools);
                     }
                     
                     cir.setReturnValue(success);
                     break;
                  }
               }
            }
         }
      }
   }
}
