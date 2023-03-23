package net.borisshoes.mobtokens.cardinalcomponents;

import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.MobTokens;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class TokenBlock {
   private NbtCompound data;
   private BlockPos pos;
   private UUID uuid;
   private boolean guiOpen = false;
   private String id;
   
   public TokenBlock(BlockPos pos){
      this.pos = pos;
      this.data = new NbtCompound();
   }
   
   public TokenBlock(int x, int y, int z){
      this.pos = new BlockPos(x,y,z);
      this.data = new NbtCompound();
   }
   
   public TokenBlock(BlockPos pos, NbtCompound nbt){
      this.pos = pos;
      this.data = nbt;
   }
   
   public TokenBlock(int x, int y, int z, NbtCompound nbt, UUID uuid, String id){
      this.pos = new BlockPos(x,y,z);
      this.data = nbt;
      this.uuid = uuid;
      this.id = id;
   }
   
   public TokenBlock(int x, int y, int z, NbtCompound nbt){
      this.pos = new BlockPos(x,y,z);
      this.data = nbt;
   }
   
   public boolean isGuiOpen(){
      return guiOpen;
   }
   
   public void setGuiOpen(boolean guiOpen){
      this.guiOpen = guiOpen;
   }
   
   public BlockPos getPos(){
      return pos;
   }
   
   public NbtCompound getData(){
      return data;
   }
   
   public void setData(NbtCompound data){
      this.data = data;
   }
   
   public void setPos(BlockPos pos){
      this.pos = pos;
   }
   
   public void setPos(int x, int y, int z){
      this.pos = new BlockPos(x,y,z);
   }
   
   public UUID getUuid(){
      return uuid;
   }
   
   public String getId(){
      return id;
   }
   
   public ItemStack convertToStack(){
      MobToken token = MobTokens.registry.get(id);
      if(token == null) return ItemStack.EMPTY;
      ItemStack stack = token.getNewToken();
      MutableText text = Text.literal(data.getString("name")).setStyle(token.getDisplayName().getStyle().withItalic(false));
      stack.getNbt().put("mobtokens",data);
      stack.setCustomName(text);
      return stack;
   }
}
