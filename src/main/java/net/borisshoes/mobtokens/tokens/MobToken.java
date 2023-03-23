package net.borisshoes.mobtokens.tokens;

import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.guis.RenameGui;
import net.borisshoes.mobtokens.guis.TokenGui;
import net.borisshoes.mobtokens.guis.TokenInventory;
import net.borisshoes.mobtokens.guis.TradingGui;
import net.borisshoes.mobtokens.utils.MobTokenUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static net.borisshoes.mobtokens.Mobtokens.MAX_MOBS;
import static net.borisshoes.mobtokens.Mobtokens.OPEN_GUIS;
import static net.borisshoes.mobtokens.cardinalcomponents.WorldDataComponentInitializer.TOKEN_BLOCK_COMPONENT;

public abstract class MobToken {
   protected String name;
   protected String id;
   protected String entityId;
   protected Text displayName;
   protected ItemStack mobHead;
   protected List<ItemStack> foods;
   protected List<ItemStack> passiveProducts;
   protected List<Pair<ItemStack,ItemStack>> activeProducts;
   protected List<Pair<ItemStack,Double>> requiredItems;
   protected int passiveTickTime;
   protected int activeTickTime;
   
   public String getName(){
      return name;
   }
   
   public String getId(){
      return id;
   }
   
   public String getEntityId(){
      return entityId;
   }
   
   public Text getDisplayName(){
      return displayName;
   }
   
   public ItemStack getMobHead(){
      return mobHead;
   }
   
   public List<ItemStack> getFoods(){
      return foods;
   }
   
   public List<ItemStack> getPassiveProducts(){
      return passiveProducts;
   }
   
   public List<Pair<ItemStack, ItemStack>> getActiveProducts(){
      return activeProducts;
   }
   
   public List<Pair<ItemStack, Double>> getRequiredItems(){
      return requiredItems;
   }
   
   public int getPassiveTickTime(){
      return passiveTickTime;
   }
   
   public int getActiveTickTime(){
      return activeTickTime;
   }
   
   public String getUUID(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return "";
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getString("UUID");
   }
   
   public int getCount(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("count");
   }
   
   public int getBabyCount(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("babyCount");
   }
   
   public int getGrowTimer(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("growTimer");
   }
   
   public int getBreedTimer(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("breedTimer");
   }
   
   public int getFood(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("food");
   }
   
   public int getActiveTick(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("activeTick");
   }
   
   public int getPassiveTick(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("passiveTick");
   }
   
   public int getOutputMode(ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return -1;
      NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
      return tokenTag.getInt("output");
   }
   
   public ItemStack addTokenNbt(ItemStack item){
      NbtCompound nbt = item.getOrCreateNbt();
      NbtCompound tokenTag = new NbtCompound();
      NbtCompound display = new NbtCompound();
      tokenTag.putString("name",name);
      tokenTag.putString("id",id);
      tokenTag.putString("UUID", UUID.randomUUID().toString());
      tokenTag.putInt("count",0);
      tokenTag.putInt("babyCount",0);
      tokenTag.putInt("growTimer",0);
      tokenTag.putInt("breedTimer",0);
      tokenTag.putInt("food",0);
      tokenTag.putInt("requiredItems",0);
      tokenTag.putInt("passiveTick",0);
      tokenTag.putInt("activeTick",0);
      tokenTag.putInt("output",0);
      NbtList storage = new NbtList();
      NbtList tools = new NbtList();
      tokenTag.put("storage",storage);
      tokenTag.put("tools",tools);
      String json = Text.Serializer.toJson(displayName).replace("\"bold\":true","\"bold\":true,\"italic\":false");
      display.putString("Name",json);
      nbt.put("display",display);
      nbt.put("mobtokens",tokenTag);
      item.setNbt(nbt);
      return item;
   }
   
   public ItemStack getNewToken(){
      return addTokenNbt(mobHead.copy());
   }
   
   public boolean addMob(ItemStack token, MobEntity entity){
      if(!MobTokenUtils.isMobToken(token)) return false;
      try{
         NbtCompound tokenTag = token.getNbt().getCompound("mobtokens");
         int curCount = tokenTag.getInt("count");
         if(curCount >= MAX_MOBS){ return false;}
         
         int requiredItemCount = tokenTag.getInt("requiredItems");
         List<Pair<ItemStack, Double>> reqItemList = getRequiredItems();
         if(reqItemList.size() > 0 && curCount != 0){
            int maxFromReqItems = (int) Math.round(requiredItemCount / reqItemList.get(0).getRight());
            if(curCount+1 > maxFromReqItems) { return false; }
         }
         
         tokenTag.putInt("count",curCount+1);
         if(entity.isBaby()){
            tokenTag.putInt("babyCount",tokenTag.getInt("babyCount")+1);
            tokenTag.putInt("growTimer",10000);
         }
         return true;
      }catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   public void placeToken(World world, BlockPos pos, ItemStack item){
      if(!MobTokenUtils.isMobToken(item)) return;
      try{
         NbtCompound tokenTag = item.getNbt().getCompound("mobtokens");
         NbtCompound tokenData = tokenTag.copy();
         TokenBlock tokenBlock = new TokenBlock(pos.getX(),pos.getY(),pos.getZ(),tokenData,UUID.fromString(getUUID(item)),id);
         
         TOKEN_BLOCK_COMPONENT.get(world).addBlock(tokenBlock);

      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
   public void openTradingGui(ServerPlayerEntity player, TokenBlock tokenBlock, NbtCompound villager){
      TradingGui gui = new TradingGui(player,this,tokenBlock,villager);
      gui.open();
      tokenBlock.setGuiOpen(true);
      OPEN_GUIS.put(player,gui);
   }
   
   public void openRenameGui(ServerPlayerEntity player, TokenBlock tokenBlock, NbtCompound guiData, ItemStack item){
      RenameGui gui = new RenameGui(player,this,tokenBlock,guiData,item);
      gui.setTitle(Text.literal("Rename"));
      gui.open();
      tokenBlock.setGuiOpen(true);
      OPEN_GUIS.put(player,gui);
   }
   
   public void openVillagerGui(ServerPlayerEntity player, TokenBlock tokenBlock, NbtCompound villager){
      TokenGui gui = new TokenGui(ScreenHandlerType.GENERIC_9X4,player,tokenBlock,this, TokenGui.TokenGuiMode.VILLAGER_MENU, villager);
      MutableText text = Text.literal(tokenBlock.getData().getString("name")).setStyle(getDisplayName().getStyle());
      gui.setTitle(text);
      gui.rebuildGui();
      gui.open();
      tokenBlock.setGuiOpen(true);
      OPEN_GUIS.put(player,gui);
   }
   
   public void openGui(ServerPlayerEntity player, TokenBlock tokenBlock, TokenGui.TokenGuiMode mode){
      BlockPos pos = tokenBlock.getPos();
      BlockState state = player.getWorld().getBlockState(pos);
      if(state.getBlock().asItem() != Items.PLAYER_HEAD){
         return;
      }
      
      TokenGui gui;
      if(mode == TokenGui.TokenGuiMode.TRADING_MENU || mode == TokenGui.TokenGuiMode.STORAGE || mode == TokenGui.TokenGuiMode.SHEEP_MENU){
         gui = new TokenGui(ScreenHandlerType.GENERIC_9X6,player,tokenBlock,this, mode);
      }else{
         gui = new TokenGui(ScreenHandlerType.GENERIC_9X3,player,tokenBlock,this, mode);
      }
      MutableText text = Text.literal(tokenBlock.getData().getString("name")).setStyle(getDisplayName().getStyle());
      gui.setTitle(text);
      gui.rebuildGui();
      gui.open();
      tokenBlock.setGuiOpen(true);
      OPEN_GUIS.put(player,gui);
   }
   
   public void tick(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      int count = tokenData.getInt("count");
      int babyCount = tokenData.getInt("babyCount");
      int growTimer = tokenData.getInt("growTimer");
      int breedTimer = tokenData.getInt("breedTimer");
      int food = tokenData.getInt("food");
      int requiredItemCount = tokenData.getInt("requiredItems");
      int passiveTick = tokenData.getInt("passiveTick");
      int activeTick = tokenData.getInt("activeTick");
      int outputMode = tokenData.getInt("output");
      
      if(growTimer > 0){
         tokenData.putInt("growTimer",--growTimer);
      }
      if(growTimer == 0){
         growBabies(serverWorld, tokenBlock);
      }
   
      if(breedTimer > 0){
         tokenData.putInt("breedTimer",--breedTimer);
      }
   
      if(passiveTick > 0){
         tokenData.putInt("passiveTick",--passiveTick);
      }
      if(passiveTick == 0){
         doPassiveTick(serverWorld,tokenBlock);
         tokenData.putInt("passiveTick",passiveTickTime);
      }
   
      if(activeTick > 0){
         tokenData.putInt("activeTick",--activeTick);
      }
      if(activeTick == 0){
         doActiveTick(serverWorld,tokenBlock);
         tokenData.putInt("activeTick",activeTickTime);
      }
   }
   
   
   protected void growBabies(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      tokenData.putInt("babyCount",0);
   }
   
   protected void doPassiveTick(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      int count = tokenData.getInt("count");
      int babyCount = tokenData.getInt("babyCount");
      int requiredItemCount = tokenData.getInt("requiredItems");
      int maxFromReqItems = getRequiredItems().size() > 0 ? (int) Math.round(requiredItemCount / getRequiredItems().get(0).getRight()) : Integer.MAX_VALUE;
      for(int i = 0; i < Math.min(maxFromReqItems,count-babyCount); i++){
         for(ItemStack passiveProduct : passiveProducts){
            addToStorage(serverWorld,tokenBlock,passiveProduct.copy());
         }
      }
   }
   
   protected void doActiveTick(ServerWorld serverWorld, TokenBlock tokenBlock){
      NbtCompound tokenData = tokenBlock.getData();
      NbtList tools = tokenData.getList("tools", NbtElement.COMPOUND_TYPE);
      TokenInventory inv = new TokenInventory();
      for(NbtElement elem : tools){
         NbtCompound toolsNbt = (NbtCompound) elem;
         inv.setStack(toolsNbt.getByte("Slot"),ItemStack.fromNbt(toolsNbt));
      }
      
      int count = tokenData.getInt("count");
      int babyCount = tokenData.getInt("babyCount");
      int requiredItemCount = tokenData.getInt("requiredItems");
      int maxFromReqItems = getRequiredItems().size() > 0 ? (int) Math.round(requiredItemCount / getRequiredItems().get(0).getRight()) : Integer.MAX_VALUE;
      for(int i = 0; i < Math.min(maxFromReqItems,count-babyCount); i++){
         boolean cont = true;
   
         for(Pair<ItemStack, ItemStack> pair : activeProducts){
            if(!cont) break;
            ItemStack toolRequired = pair.getLeft().copy();
            Item toolItem = toolRequired.getItem();
            if(toolItem.isDamageable()){
               for(int j = 0; j < inv.size(); j++){
                  ItemStack toolsStack = inv.getStack(j);
                  if(toolsStack.isEmpty()) continue;
                  if(toolsStack.isOf(toolItem)){
                     boolean broke = toolsStack.damage(1, Random.create(),null);
                     if(broke){
                        inv.setStack(j, ItemStack.EMPTY);
                     }
                     addToStorage(serverWorld,tokenBlock,pair.getRight().copy());
                     cont = false;
                     break;
                  }
               }
            }else{
               if(inv.count(toolItem) >= toolRequired.getCount()){
                  inv.removeItem(toolItem,toolRequired.getCount());
                  addToStorage(serverWorld,tokenBlock,pair.getRight().copy());
                  break;
               }
            }
         }
      }
   
      NbtList newTools = new NbtList();
      for(int i = 0; i < inv.size(); i++){
         ItemStack toolsStack = inv.getStack(i);
         if(toolsStack.isEmpty()) continue;
         NbtCompound stackNbt = toolsStack.writeNbt(new NbtCompound());
         stackNbt.putByte("Slot", (byte) i);
         newTools.add(stackNbt);
      }
      tokenData.put("tools",newTools);
   }
   
   public void addToStorage(ServerWorld world, TokenBlock tokenBlock, ItemStack stack){
      NbtCompound tokenData = tokenBlock.getData();
      NbtList storage = tokenData.getList("storage", NbtElement.COMPOUND_TYPE);
      int outputMode = tokenData.getInt("output");
      stack = stack.copy();
   
      if(outputMode == 3) return; //Void all produce
   
      ItemStack leftover;
      if(outputMode == 2){ // Drop all produce
         leftover = stack;
      }else{
         TokenInventory inv = new TokenInventory();
         for(NbtElement elem : storage){
            NbtCompound storageNbt = (NbtCompound) elem;
            inv.setStack(storageNbt.getByte("Slot"),ItemStack.fromNbt(storageNbt));
         }
         leftover = inv.addStack(stack);
   
         NbtList newStorage = new NbtList();
         for(int i = 0; i < inv.size(); i++){
            ItemStack storageStack = inv.getStack(i);
            if(storageStack.isEmpty()) continue;
            NbtCompound stackNbt = storageStack.writeNbt(new NbtCompound());
            stackNbt.putByte("Slot", (byte) i);
            newStorage.add(stackNbt);
         }
         tokenData.put("storage",newStorage);
      }
   
      if(outputMode == 1 || outputMode == 2){
         Vec3d spawnPos = Vec3d.ofCenter(tokenBlock.getPos());
         ItemEntity itemEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY()-0.75, spawnPos.getZ(), leftover);
         itemEntity.setPickupDelay(40);
   
         float f = world.random.nextFloat() * 0.03F;
         float g = world.random.nextFloat() * 6.2831855F;
         itemEntity.setVelocity((double)(-MathHelper.sin(g) * f), 0.1, (double)(MathHelper.cos(g) * f));
         world.spawnEntity(itemEntity);
      }
   }
}
