package net.borisshoes.mobtokens.cardinalcomponents;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenBlockComponent implements ITokenBlockComponent{
   public final List<TokenBlock> blocks = new ArrayList<>();
   
   
   @Override
   public List<TokenBlock> getBlocks(){
      return blocks;
   }
   
   @Override
   public boolean addBlock(TokenBlock block){
      if (blocks.contains(block)) return false;
      return blocks.add(block);
   }
   
   @Override
   public boolean removeBlock(TokenBlock block){
      if (!blocks.contains(block)) return false;
      return blocks.remove(block);
   }
   
   @Override
   public void readFromNbt(NbtCompound tag){
      try{
         blocks.clear();
         NbtList blocksTag = tag.getList("TokenBlocks", NbtElement.COMPOUND_TYPE);
         for (NbtElement e : blocksTag) {
            NbtCompound blockTag = (NbtCompound) e;
            NbtList pos = blockTag.getList("pos",NbtElement.INT_TYPE);
            blocks.add(new TokenBlock(pos.getInt(0),pos.getInt(1),pos.getInt(2),blockTag.getCompound("data"), UUID.fromString(blockTag.getString("UUID")),blockTag.getString("id")));
         }
      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
   @Override
   public void writeToNbt(NbtCompound tag){
      try{
         NbtList blocksTag = new NbtList();
         for(TokenBlock block : blocks){
            NbtCompound blockTag = new NbtCompound();
            NbtList pos = new NbtList();
            pos.add(0, NbtInt.of(block.getPos().getX()));
            pos.add(1, NbtInt.of(block.getPos().getY()));
            pos.add(2, NbtInt.of(block.getPos().getZ()));
            blockTag.put("pos",pos);
            blockTag.putString("UUID",block.getUuid().toString());
            blockTag.putString("id", block.getId());
            blockTag.put("data",block.getData());
            blocksTag.add(blockTag);
         }
         tag.put("TokenBlocks",blocksTag);
      }catch(Exception e){
         e.printStackTrace();
      }
   }
}