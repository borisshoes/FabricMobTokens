package net.borisshoes.mobtokens.guis;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.AnimatedGuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.borisshoes.mobtokens.Mobtokens;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.mixins.LivingEntityAccessor;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.SheepToken;
import net.borisshoes.mobtokens.tokens.VillagerToken;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.borisshoes.mobtokens.utils.TradeGenerationUtils;
import net.fabricmc.fabric.mixin.transfer.HopperBlockEntityMixin;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.borisshoes.mobtokens.Mobtokens.MAX_MOBS;

public class TokenGui extends SimpleGui implements TokenRelatedGui{
   
   private final TokenGuiMode mode;
   private final MobToken token;
   private final TokenBlock tokenBlock;
   private TokenInventory inv;
   private TokenInventoryListener listener;
   private int page;
   private final int[][] dynamicSlots = {{},{3},{1,5},{1,3,5},{0,2,4,6},{1,2,3,4,5},{0,1,2,4,5,6},{0,1,2,3,4,5,6}};
   private NbtCompound guiData;
   
   /**
    * Constructs a new simple container gui for the supplied player.
    *
    * @param type                  the screen handler that the client should display
    * @param player                the player to server this gui to
    *                              will be treated as slots of this gui
    * @param mode                  the mode of the gui
    */
   public TokenGui(ScreenHandlerType<?> type, ServerPlayerEntity player, TokenBlock tokenBlock, MobToken token, TokenGuiMode mode){
      super(type, player, false);
      this.mode = mode;
      this.tokenBlock = tokenBlock;
      this.token = token;
      this.page = 1;
      this.guiData = new NbtCompound();
      rebuildGui();
   }
   
   public TokenGui(ScreenHandlerType<?> type, ServerPlayerEntity player, TokenBlock tokenBlock, MobToken token, TokenGuiMode mode, NbtCompound guiData){
      super(type, player, false);
      this.mode = mode;
      this.tokenBlock = tokenBlock;
      this.token = token;
      this.page = 1;
      this.guiData = guiData;
      rebuildGui();
   }
   
   public void close(){
      super.close();
   }
   
   public TokenGuiMode getMode(){
      return mode;
   }
   
   public MobToken getToken(){
      return token;
   }
   
   public TokenBlock getTokenBlock(){
      return tokenBlock;
   }
   
   public SimpleGui getGui(){
      return this;
   }
   
   @Override
   public boolean onAnyClick(int index, ClickType type, SlotActionType action){
      NbtCompound tokenData = tokenBlock.getData();
      String name = tokenData.getString("name");
      int count = tokenData.getInt("count");
      int babyCount = tokenData.getInt("babyCount");
      int growTimer = tokenData.getInt("growTimer");
      int breedTimer = tokenData.getInt("breedTimer");
      int food = tokenData.getInt("food");
      int requiredItemCount = tokenData.getInt("requiredItems");
      int passiveTick = tokenData.getInt("passiveTick");
      int activeTick = tokenData.getInt("activeTick");
      int outputMode = tokenData.getInt("output");
      
      if(mode == TokenGuiMode.MAIN_MENU){
         if(index == 10){
            token.openGui(player, tokenBlock, TokenGuiMode.STORAGE);
         }else if(index == 12){
            if(!token.getFoods().isEmpty()){
               token.openGui(player, tokenBlock, TokenGuiMode.BREEDING);
            }else{
               player.sendMessage(Text.literal("This creature cannot be bred").formatted(Formatting.RED,Formatting.ITALIC));
            }
         }else if(index == 14){
            if(token instanceof SheepToken){
               token.openGui(player, tokenBlock, TokenGuiMode.SHEEP_MENU);
            }else if(token instanceof VillagerToken villagerToken){
               boolean hasIllager = tokenData.getBoolean("hasIllager");
               if(hasIllager){
                  token.openGui(player, tokenBlock, TokenGuiMode.IRON_MENU);
               }else{
                  player.sendMessage(Text.literal("Capture an Illager to unlock the Iron Farm").formatted(Formatting.RED,Formatting.ITALIC));
               }
            }else{
               token.openGui(player, tokenBlock, TokenGuiMode.KILLING);
            }
         }else if(index == 16){
            if(!token.getActiveProducts().isEmpty()){
               token.openGui(player, tokenBlock, TokenGuiMode.TOOLS);
            }else if(token instanceof VillagerToken villagerToken){
               token.openGui(player, tokenBlock, TokenGuiMode.TRADING_MENU);
            }else{
               player.sendMessage(Text.literal("This creature does not require any tools").formatted(Formatting.RED,Formatting.ITALIC));
            }
         }else if(index == 8){
            returnItems();
            token.openRenameGui(player, tokenBlock, tokenBlock.getData(), getSlot(4).getItemStack());
         }
      }else if(mode == TokenGuiMode.BREEDING){
         int breedCount = 0;
         if(index == 4){
            returnItems();
            token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
         }else if(index == 10){
            breedCount = 1;
         }else if(index == 11){
            breedCount = 5;
         }else if(index == 15){
            breedCount = 10;
         }else if(index == 16){
            breedCount = food/2;
         }
         
         if(breedCount > 0 && breedTimer > 0){
            player.sendMessage(Text.literal("The adults are not ready to breed yet").formatted(Formatting.RED,Formatting.ITALIC));
            breedCount = 0;
         }
         
         if(breedCount > 0 && count-babyCount < 2){
            player.sendMessage(Text.literal("You do not have enough creatures to breed them").formatted(Formatting.RED,Formatting.ITALIC));
            breedCount = 0;
         }
         int foodReq = token instanceof VillagerToken ? 32 : 2;
         if(breedCount > 0){
            breedCount = Math.min(food / foodReq, breedCount); // Cap number based on food
            breedCount = Math.min(MAX_MOBS - count, breedCount); // Cap number based on available space
            breedCount = Math.min((count - babyCount) / 2, breedCount); // Cap number based on available parents
   
            List<Pair<ItemStack, Double>> reqItemList = token.getRequiredItems();
            if(reqItemList.size() > 0){
               int maxFromReqItems = (int) Math.round(requiredItemCount / reqItemList.get(0).getRight());
               breedCount = Math.min(breedCount,maxFromReqItems-count); // Cap number based on available required items
            }
         }
         
         if(breedCount > 0){
            //System.out.println("Breeding: "+breedCount);
            tokenData.putInt("count",count+breedCount);
            tokenData.putInt("babyCount",breedCount);
            tokenData.putInt("growTimer",10000);
            tokenData.putInt("breedTimer",12000);
            tokenData.putInt("food",food-breedCount*foodReq);
   
            if(token instanceof VillagerToken villagerToken){
               List<NbtCompound> villagers = villagerToken.getVillagerTags(tokenData);
               for(int i = 0; i < breedCount; i++){
                  villagers.add(VillagerToken.makeBabyTag());
               }
               villagerToken.storeVillagerTags(tokenData,villagers);
            }else if(token instanceof SheepToken sheepToken){
               List<Pair<DyeColor,Boolean>> sheep = sheepToken.getStoredSheep(tokenData);
               for(int i = 0; i < breedCount; i++){
                  sheep.add(new Pair<>(DyeColor.WHITE, true));
               }
               sheepToken.storeSheep(sheep,tokenData);
            }
            rebuildGui();
         }
         
      }else if(mode == TokenGuiMode.KILLING){
         int killCount = 0;
         if(index == 4){
            returnItems();
            token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
         }else if(index == 10){
            killCount = 1;
         }else if(index == 12){
            killCount = 5;
         }else if(index == 14){
            killCount = 10;
         }else if(index == 16){
            killCount = count-babyCount;
         }
   
         killCount = Math.min(killCount,count-babyCount);
         if(killCount > 0){
            tokenData.putInt("count",count-killCount);
            //System.out.println("Killing: "+killCount+" "+tokenData.getInt("count")+" remain...");
   
            for(int i = 0; i < killCount; i ++){
               EntityType<?> entityType = EntityType.get(token.getEntityId()).get();
               Identifier identifier = entityType.getLootTableId();
               LootTable lootTable = player.getServer().getLootManager().getTable(identifier);
               LivingEntity entity = (LivingEntity) entityType.create(player.getWorld());
               entity.setAttacker(player);
               entity.setAttacking(player);
               DamageSource dmgSource = DamageSource.player(player);
               if(EnchantmentHelper.getFireAspect(player) >= 1){
                  dmgSource = dmgSource.setFire();
                  entity.setOnFire(true);
                  entity.setOnFireFor(4);
               }
               LootContext.Builder builder = ((LivingEntityAccessor) entity).invokeGetLootContextBuilder(true, dmgSource);
               lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), newItemStack -> token.addToStorage(player.getWorld(),tokenBlock,newItemStack));
            }
         }
         rebuildGui();
      }else if(mode == TokenGuiMode.STORAGE){
         if(index == 4){
            returnItems();
            token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
         }else if(index == 49){
            tokenData.putInt("output",(outputMode+1)%4);
            rebuildGui();
         }
      }else if(mode == TokenGuiMode.TOOLS){
         if(index == 4){
            returnItems();
            token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
         }
      }else if(mode == TokenGuiMode.SHEEP_MENU){
         if(token instanceof SheepToken sheepToken){
            List<Pair<DyeColor,Boolean>> colors = sheepToken.getStoredSheep(tokenBlock);
            int numPages = (int) Math.ceil((float) colors.size() / 28.0);
            GuiElementInterface clickedSlot = getSlot(index);
            ItemStack clickedItem = clickedSlot != null ? clickedSlot.getItemStack() : ItemStack.EMPTY;
   
            if(index == 4){
               returnItems();
               token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
            }if(index > 9 && index < 45 && index % 9 != 0 && index % 9 != 8 && !clickedItem.isEmpty()){
               int sheepInd = 28*(page-1)+7*((index/9)-1)+((index % 9)-1);
               Pair<DyeColor,Boolean> sheep = colors.get(sheepInd);
               DyeColor color = sheep.getLeft();
               
               if(type == ClickType.MOUSE_RIGHT){
                  boolean mainDye = player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof DyeItem;
                  boolean offDye = player.getStackInHand(Hand.OFF_HAND).getItem() instanceof DyeItem;
                  if(mainDye || offDye){
                     ItemStack dyeStack;
                     if(mainDye){
                        dyeStack = player.getStackInHand(Hand.MAIN_HAND);
                     }else{
                        dyeStack = player.getStackInHand(Hand.OFF_HAND);
                     }
                     DyeItem dyeItem = (DyeItem) dyeStack.getItem();
                     DyeColor newColor = dyeItem.getColor();
                     
                     if(color == newColor){
                        player.sendMessage(Text.literal("That Sheep is already that color").formatted(Formatting.RED,Formatting.ITALIC));
                     }else{
                        colors.set(sheepInd,new Pair<>(dyeItem.getColor(),sheep.getRight()));
                        sheepToken.storeSheep(colors,tokenBlock);
                        dyeStack.decrement(1);
                     }
                  }else{
                     player.sendMessage(Text.literal("You must hold a valid dye to color the Sheep").formatted(Formatting.RED,Formatting.ITALIC));
                  }
               }else{
                  if(!sheep.getRight()){
                     tokenData.putInt("count",count-1);
                     //System.out.println("Killing: 1 "+tokenData.getInt("count")+" remain...");
   
                     EntityType<?> entityType = EntityType.get(token.getEntityId()).get();
                     Identifier identifier = entityType.getLootTableId();
                     LootTable lootTable = player.getServer().getLootManager().getTable(identifier);
                     LivingEntity entity = (LivingEntity) entityType.create(player.getWorld());
                     entity.setAttacker(player);
                     entity.setAttacking(player);
                     DamageSource dmgSource = DamageSource.player(player);
                     if(EnchantmentHelper.getFireAspect(player) >= 1){
                        dmgSource = dmgSource.setFire();
                        entity.setOnFire(true);
                        entity.setOnFireFor(4);
                     }
                     LootContext.Builder builder = ((LivingEntityAccessor) entity).invokeGetLootContextBuilder(true, dmgSource);
                     lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), newItemStack -> token.addToStorage(player.getWorld(),tokenBlock,newItemStack));
   
                     colors.remove(sheepInd);
                     sheepToken.storeSheep(colors,tokenBlock);
                  }else{
                     player.sendMessage(Text.literal("You cant kill a baby Sheep").formatted(Formatting.RED,Formatting.ITALIC));
                  }
               }
            }else if(index == 45){
               if(page > 1){
                  page--;
               }
            }else if(index == 53){
               if(page < numPages){
                  page++;
               }
            }
            rebuildGui();
         }
      }else if(mode == TokenGuiMode.IRON_MENU){
         if(token instanceof VillagerToken villagerToken){
            int killCount = 0;
            int ironCount = tokenData.getInt("ironVillagers");
            if(index == 4){
               returnItems();
               token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
            }else if(index == 10){
               killCount = 1;
            }else if(index == 12){
               killCount = 5;
            }else if(index == 14){
               killCount = 10;
            }else if(index == 16){
               killCount = ironCount;
            }
   
            killCount = Math.min(killCount, ironCount);
            if(killCount > 0){
               tokenData.putInt("ironVillagers", ironCount - killCount);
               //System.out.println("Killing: "+killCount+" "+tokenData.getInt("count")+" remain...");
      
               for(int i = 0; i < killCount; i++){
                  EntityType<?> entityType = EntityType.get(token.getEntityId()).get();
                  Identifier identifier = entityType.getLootTableId();
                  LootTable lootTable = player.getServer().getLootManager().getTable(identifier);
                  LivingEntity entity = (LivingEntity) entityType.create(player.getWorld());
                  entity.setAttacker(player);
                  entity.setAttacking(player);
                  DamageSource dmgSource = DamageSource.player(player);
                  if(EnchantmentHelper.getFireAspect(player) >= 1){
                     dmgSource = dmgSource.setFire();
                     entity.setOnFire(true);
                     entity.setOnFireFor(4);
                  }
                  LootContext.Builder builder = ((LivingEntityAccessor) entity).invokeGetLootContextBuilder(true, dmgSource);
                  lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), newItemStack -> token.addToStorage(player.getWorld(), tokenBlock, newItemStack));
               }
            }
            rebuildGui();
         }
      }else if(mode == TokenGuiMode.VILLAGER_MENU){
         if(token instanceof VillagerToken villagerToken){
            List<NbtCompound> villagers = villagerToken.getVillagerTags(tokenData);
            if(index == 4){
               returnItems();
               token.openGui(player, tokenBlock, TokenGuiMode.TRADING_MENU);
            }else if(index == 10){
               if(!guiData.getBoolean("baby")){
                  returnItems();
                  token.openTradingGui(player, tokenBlock, guiData);
               }else{
                  player.sendMessage(Text.literal("You cant trade with a baby Villager").formatted(Formatting.RED,Formatting.ITALIC));
               }
            }else if(index == 12){
               returnItems();
               token.openRenameGui(player, tokenBlock, guiData, getSlot(4).getItemStack());
            }else if(index == 14){
               NbtCompound villager = guiData;
               NbtCompound villagerData = villager.getCompound("VillagerData");
               int xp = villager.getInt("Xp");
               int level = villagerData.getInt("level");
               boolean baby = villager.getBoolean("baby");
               boolean check = !baby && xp == 0;
               
               if(check){
                  ItemStack workItem = ItemStack.EMPTY;
                  VillagerProfession prof = null;
                  if(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BlockItem m){
                     BlockState mainBlock = m.getBlock().getDefaultState();
                     Optional<RegistryEntry<PointOfInterestType>> opt = PointOfInterestTypes.getTypeForState(mainBlock);
                     if(opt.isPresent()){
                        RegistryEntry<PointOfInterestType> poiType = opt.get();
                        boolean valid = VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test(poiType);
                        if(valid){
                           workItem = player.getStackInHand(Hand.MAIN_HAND);
                           for(Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> e : Registries.VILLAGER_PROFESSION.getEntrySet()){
                              RegistryKey<VillagerProfession> key = e.getKey();
                              VillagerProfession p = e.getValue();
                              if(p.acquirableWorkstation().test(poiType) && !p.id().replace("minecraft:", "").equals("none")){
                                 prof = p;
                                 break;
                              }
                           }
                        }
                     }
                  }else if(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof BlockItem m){
                     BlockState mainBlock = m.getBlock().getDefaultState();
                     Optional<RegistryEntry<PointOfInterestType>> opt = PointOfInterestTypes.getTypeForState(mainBlock);
                     if(opt.isPresent()){
                        RegistryEntry<PointOfInterestType> poiType = opt.get();
                        boolean valid = VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test(poiType);
                        if(valid){
                           workItem = player.getStackInHand(Hand.MAIN_HAND);
                           for(Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> e : Registries.VILLAGER_PROFESSION.getEntrySet()){
                              RegistryKey<VillagerProfession> key = e.getKey();
                              VillagerProfession p = e.getValue();
                              if(p.acquirableWorkstation().test(poiType) && !p.id().replace("minecraft:", "").equals("none")){
                                 prof = p;
                                 break;
                              }
                           }
                        }
                     }
                  }
                  
                  if(!workItem.isEmpty() && prof != null){
                     String curProfession = villagerData.getString("profession").replace("minecraft:", "");
                     String professionName = prof.id().replace("minecraft:", "");
                     
                     if(!curProfession.equals(professionName)){ // Consume item
                        workItem.decrement(1);
                     }else{
                        player.sendMessage(Text.literal("Re-Rolled Villager Trades").formatted(Formatting.AQUA,Formatting.ITALIC),true);
                     }
                     
                     NbtCompound offersTag = new NbtCompound();
                     NbtList emptyRecipes = new NbtList();
                     offersTag.put("Recipes",emptyRecipes); // Prep empty list
                     
                     villagerData.putString("profession",prof.id()); // Set Profession
                     TradeOfferList offers = new TradeOfferList(offersTag);
                     TradeGenerationUtils.fillRecipes(player.getWorld(),villager,offers); // Send offer list to be filled
                     villager.put("Offers",offers.toNbt()); // Give villager new offer list
   
                     rebuildGui();
                  }
               }else{
                  if(xp != 0){
                     player.sendMessage(Text.literal("This Villager's trades are locked").formatted(Formatting.RED,Formatting.ITALIC));
                  }else if(baby){
                     player.sendMessage(Text.literal("You cannot give a baby a workstation").formatted(Formatting.RED,Formatting.ITALIC));
                  }
               }
            }else if(index == 16){
               if(!guiData.getBoolean("baby")){
                  tokenData.putInt("count",count-1);
      
                  EntityType<?> entityType = EntityType.get(token.getEntityId()).get();
                  Identifier identifier = entityType.getLootTableId();
                  LootTable lootTable = player.getServer().getLootManager().getTable(identifier);
                  LivingEntity entity = (LivingEntity) entityType.create(player.getWorld());
                  entity.readCustomDataFromNbt(guiData);
                  entity.setAttacker(player);
                  entity.setAttacking(player);
                  DamageSource dmgSource = DamageSource.player(player);
                  if(EnchantmentHelper.getFireAspect(player) >= 1){
                     dmgSource = dmgSource.setFire();
                     entity.setOnFire(true);
                     entity.setOnFireFor(4);
                  }
                  LootContext.Builder builder = ((LivingEntityAccessor) entity).invokeGetLootContextBuilder(true, dmgSource);
                  lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), newItemStack -> token.addToStorage(player.getWorld(),tokenBlock,newItemStack));
   
                  villagers.remove(guiData);
                  villagerToken.storeVillagerTags(tokenData,villagers);
                  returnItems();
                  token.openGui(player, tokenBlock, TokenGuiMode.TRADING_MENU);
               }else{
                  player.sendMessage(Text.literal("You cant kill a baby Villager").formatted(Formatting.RED,Formatting.ITALIC));
               }
            }else if(index == 20){
               guiData.putBoolean("favorite",!guiData.getBoolean("favorite"));
            }else if(index == 22){
               boolean hasZombie = tokenData.getBoolean("hasZombie");
               if(hasZombie){
                  if(!guiData.getBoolean("baby")){
                     ItemStack gapple = ItemStack.EMPTY;
                     ItemStack weakness = ItemStack.EMPTY;
   
                     PlayerInventory inv = player.getInventory();
                     for(int i = 0; i < inv.size(); i++){
                        ItemStack stack = inv.getStack(i);
                        if(stack.isOf(Items.GOLDEN_APPLE)){
                           gapple = stack;
                        }
                        List<StatusEffectInstance> effects = PotionUtil.getPotion(stack).getEffects();
                        for(StatusEffectInstance effect : effects){
                           if(effect.getEffectType() == StatusEffects.WEAKNESS){
                              weakness = stack;
                              break;
                           }
                        }
                     }
                     if(!gapple.isEmpty() && !weakness.isEmpty()){
                        gapple.decrement(1);
                        weakness.decrement(1);
                        guiData.putBoolean("zombie",true);
                        guiData.putInt("cureTime",6000);
                        
                        returnItems();
                        token.openGui(player, tokenBlock, TokenGuiMode.TRADING_MENU);
                     }else{
                        player.sendMessage(Text.literal("You need a Golden Apple and Weakness Potion in your inventory").formatted(Formatting.RED,Formatting.ITALIC));
                     }
                  }else{
                     player.sendMessage(Text.literal("You cannot infect and cure a baby").formatted(Formatting.RED,Formatting.ITALIC));
                  }
               }else{
                  player.sendMessage(Text.literal("Capture a Zombie to unlock the Curing option").formatted(Formatting.RED,Formatting.ITALIC));
               }
            }else if(index == 24){
               boolean hasIllager = tokenData.getBoolean("hasIllager");
               if(hasIllager){
                  if(!guiData.getBoolean("baby")){
                     tokenData.putInt("ironVillagers",tokenData.getInt("ironVillagers")+1);
                     
                     villagers.remove(guiData);
                     villagerToken.storeVillagerTags(tokenData,villagers);
                     returnItems();
                     token.openGui(player, tokenBlock, TokenGuiMode.TRADING_MENU);
                  }else{
                     player.sendMessage(Text.literal("You cant send a baby Villager to the Iron Farm").formatted(Formatting.RED,Formatting.ITALIC));
                  }
               }else{
                  player.sendMessage(Text.literal("Capture an Illager to unlock the Iron Farm").formatted(Formatting.RED,Formatting.ITALIC));
               }
            }
         }
      }else if(mode == TokenGuiMode.TRADING_MENU){
         if(token instanceof VillagerToken villagerToken){
            List<NbtCompound> villagers = villagerToken.getVillagerTags(tokenData);
            int numPages = (int) Math.ceil((float) villagers.size() / 28.0);
            GuiElementInterface clickedSlot = getSlot(index);
            ItemStack clickedItem = clickedSlot != null ? clickedSlot.getItemStack() : ItemStack.EMPTY;
      
            if(index == 0){
               int sortType = tokenData.getInt("sortType");
               tokenData.putInt("sortType",sortType == 1 ? 0 : 1);
            }if(index == 4){
               returnItems();
               token.openGui(player, tokenBlock, TokenGuiMode.MAIN_MENU);
            }if(index > 9 && index < 45 && index % 9 != 0 && index % 9 != 8 && !clickedItem.isEmpty()){
               int villagerInd = 28*(page-1)+7*((index/9)-1)+((index % 9)-1);
               NbtCompound villager = villagers.get(villagerInd);
               
               boolean zombie = villager.getBoolean("zombie");
               if(!zombie){
                  if(type == ClickType.MOUSE_RIGHT){
                     returnItems();
                     token.openVillagerGui(player, tokenBlock, villager);
                  }else{
                     if(!villager.getBoolean("baby")){
                        returnItems();
                        token.openTradingGui(player, tokenBlock, villager);
                     }else{
                        player.sendMessage(Text.literal("You cant trade with a baby Villager").formatted(Formatting.RED,Formatting.ITALIC));
                     }
                  }
               }else{
                  player.sendMessage(Text.literal("You must wait for this Villager to be Cured").formatted(Formatting.RED,Formatting.ITALIC));
               }
            }else if(index == 45){
               if(page > 1){
                  page--;
               }
            }else if(index == 53){
               if(page < numPages){
                  page++;
               }
            }
            rebuildGui();
         }
      }
      
      //if(index > getSize()) rebuildGui();
      return true;
   }
   
   private void initInv(){
      inv = new TokenInventory();
      listener = new TokenInventoryListener(tokenBlock,token,this);
      inv.addListener(listener);
   }
   
   public void rebuildGui(){
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
      NbtList storage = tokenData.getList("storage", NbtElement.COMPOUND_TYPE);
      NbtList tools = tokenData.getList("tools", NbtElement.COMPOUND_TYPE);
      
      if(inv == null){
         initInv();
      }
      listener.setUpdating();
      
      GuiElementBuilder tokenHead = GuiElementBuilder.from(token.getNewToken());
      tokenHead.setCount(Math.max(1,count));
      tokenHead.addLoreLine(Text.literal("---------------").formatted(Formatting.DARK_PURPLE));
      tokenHead.addLoreLine(Text.literal("Token contains "+count+" creatures").formatted(Formatting.DARK_PURPLE));
      tokenHead.addLoreLine(Text.literal("Food Available: "+food).formatted(Formatting.DARK_PURPLE));
      if(!token.getRequiredItems().isEmpty()){
         tokenHead.addLoreLine(Text.literal("Required Items Available: "+requiredItemCount).formatted(Formatting.DARK_PURPLE));
      }
      tokenHead.addLoreLine(Text.literal("---------------").formatted(Formatting.DARK_PURPLE));
      if(babyCount > 0){
         tokenHead.addLoreLine(Text.literal("Token contains "+babyCount+" babies").formatted(Formatting.DARK_PURPLE));
         tokenHead.addLoreLine(Text.literal("Time till babies grow: "+(growTimer/20)+" seconds").formatted(Formatting.DARK_PURPLE));
      }
      if(breedTimer > 0){
         tokenHead.addLoreLine(Text.literal("Adults can breed in: "+(breedTimer/20)+" seconds").formatted(Formatting.DARK_PURPLE));
      }
      
      for(int i = 0; i < getSize(); i++){
         clearSlot(i);
         setSlot(i,new GuiElementBuilder(Items.CYAN_STAINED_GLASS_PANE).setName(Text.empty()));
      }
      
      // 10,11,12,13,14,15,16
      
      if(mode == TokenGuiMode.MAIN_MENU){
         setSlot(10,new GuiElementBuilder(Items.CHEST).setName(Text.literal("Output Storage").formatted(Formatting.GOLD)).hideFlags());
   
         setSlot(12,new GuiElementBuilder(Items.WHEAT).setName(Text.literal("Breed Creatures").formatted(Formatting.AQUA)).hideFlags());
   
         if(token instanceof VillagerToken villagerToken){
            setSlot(14,new GuiElementBuilder(Items.IRON_INGOT).setName(Text.literal("Iron Farm").formatted(Formatting.GRAY)).hideFlags());
   
            setSlot(16,new GuiElementBuilder(Items.EMERALD).setName(Text.literal("Villagers").formatted(Formatting.GREEN)).hideFlags());
         }else{
            setSlot(14,new GuiElementBuilder(Items.DIAMOND_SWORD).setName(Text.literal("Kill Creatures").formatted(Formatting.RED)).hideFlags());
   
            setSlot(16,new GuiElementBuilder(Items.SHEARS).setName(Text.literal("Tool Storage").formatted(Formatting.GRAY)).hideFlags());
         }
   
         setSlot(8,new GuiElementBuilder(Items.NAME_TAG).setName(Text.literal("Rename Token").formatted(Formatting.LIGHT_PURPLE)).hideFlags());
         
      }else if(mode == TokenGuiMode.KILLING){
         setSlot(10,new GuiElementBuilder(Items.WOODEN_SWORD).setName(Text.literal("Kill 1").formatted(Formatting.YELLOW)).hideFlags());
         setSlot(12,new GuiElementBuilder(Items.STONE_SWORD).setName(Text.literal("Kill 5").formatted(Formatting.GOLD)).hideFlags());
         setSlot(14,new GuiElementBuilder(Items.IRON_SWORD).setName(Text.literal("Kill 10").formatted(Formatting.RED)).hideFlags());
         setSlot(16,new GuiElementBuilder(Items.DIAMOND_SWORD).setName(Text.literal("Kill All").formatted(Formatting.DARK_RED)).hideFlags());
         
         int lootingLvl = EnchantmentHelper.getLooting(player);
         if(lootingLvl > 0){
            setSlot(22,new GuiElementBuilder(Items.ENCHANTED_BOOK).setName(Text.literal("Hold a Weapon to Apply Looting").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("The level of looting on the weapon will be applied to drops").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("Looting Level: "+lootingLvl).formatted(Formatting.AQUA)));
         }else{
            setSlot(22,new GuiElementBuilder(Items.BOOK).setName(Text.literal("Hold a Weapon to Apply Looting").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("The level of looting on the weapon will be applied to drops").formatted(Formatting.DARK_PURPLE)));
         }
      }else if(mode == TokenGuiMode.IRON_MENU){
         setSlot(10,new GuiElementBuilder(Items.WOODEN_SWORD).setName(Text.literal("Retire 1").formatted(Formatting.YELLOW)).hideFlags());
         setSlot(12,new GuiElementBuilder(Items.STONE_SWORD).setName(Text.literal("Retire 5").formatted(Formatting.GOLD)).hideFlags());
         setSlot(14,new GuiElementBuilder(Items.IRON_SWORD).setName(Text.literal("Retire 10").formatted(Formatting.RED)).hideFlags());
         setSlot(16,new GuiElementBuilder(Items.DIAMOND_SWORD).setName(Text.literal("Retire All").formatted(Formatting.DARK_RED)).hideFlags());
   
         int ironVillagers = tokenData.getInt("ironVillagers");
         int ironMin = ironVillagers/3;
         GuiElementBuilder ironItem = new GuiElementBuilder(Items.IRON_INGOT);
         ironItem.setCount(Math.max(1,ironMin)).setName(Text.literal("Generating "+ironMin+" Ingots per Minute").formatted(Formatting.GRAY,Formatting.BOLD)).hideFlags()
               .addLoreLine(Text.literal(ironVillagers+" Villagers working in the Iron Farm").formatted(Formatting.GRAY))
               .addLoreLine(Text.literal("---------------").formatted(Formatting.DARK_GRAY))
               .addLoreLine(Text.literal("It takes 3 Villagers to generate 1 ingot per minute").formatted(Formatting.DARK_PURPLE))
               .addLoreLine(Text.literal("Add Villagers to the Iron Farm from the Villager Menu").formatted(Formatting.DARK_PURPLE))
               .addLoreLine(Text.literal("---------------").formatted(Formatting.DARK_GRAY))
               .addLoreLine(Text.literal("Retiring Villagers deletes them from the token").formatted(Formatting.RED));
         setSlot(22,ironItem);
      }else if(mode == TokenGuiMode.SHEEP_MENU){
         if(token instanceof SheepToken sheepToken){
            List<Pair<DyeColor,Boolean>> colors = sheepToken.getStoredSheep(tokenBlock);
            List<Pair<DyeColor,Boolean>> pageItems = listToPage(colors, page);
            int numPages = (int) Math.ceil((float)colors.size()/28.0);
   
            int k = 0;
            for(int i = 0; i < 4; i++){
               for(int j = 0; j < 7; j++){
                  clearSlot((i*9+10)+j);
                  if(k < pageItems.size()){
                     Pair<DyeColor,Boolean> sheep = pageItems.get(k);
                     ItemStack headStack = MobHeadUtils.getColoredSheepHead(sheep.getLeft());
                     GuiElementBuilder sheepHead = GuiElementBuilder.from(headStack);
                     if(sheep.getRight()) sheepHead.setName(headStack.getName().copy().append(" - Baby"));
                     sheepHead.addLoreLine(Text.literal("Left Click to kill the Sheep").formatted(Formatting.RED));
                     sheepHead.addLoreLine(Text.literal("Right Click to dye the Sheep").formatted(Formatting.AQUA));
                     setSlot((i*9+10)+j,sheepHead);
                  }else{
                     setSlot((i*9+10)+j,new GuiElementBuilder(Items.AIR));
                  }
                  k++;
               }
            }
            
            GuiElementBuilder nextPage = new GuiElementBuilder(Items.ARROW).setName(Text.literal("Next Page").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("Click to go to the next page").formatted(Formatting.DARK_PURPLE));
            setSlot(53,nextPage);
   
            GuiElementBuilder prevPage = new GuiElementBuilder(Items.ARROW).setName(Text.literal("Previous Page").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("Click to go to the previous page").formatted(Formatting.DARK_PURPLE));
            setSlot(45,prevPage);
   
            int lootingLvl = EnchantmentHelper.getLooting(player);
            boolean mainDye = player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof DyeItem;
            boolean offDye = player.getStackInHand(Hand.OFF_HAND).getItem() instanceof DyeItem;
            if(lootingLvl > 0){
               setSlot(49,new GuiElementBuilder(Items.ENCHANTED_BOOK).setName(Text.literal("Hold a Weapon to Apply Looting / Hold a Dye to Color Sheep").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                     .addLoreLine(Text.literal("The level of looting on the weapon will be applied to drops").formatted(Formatting.DARK_PURPLE))
                     .addLoreLine(Text.literal("").formatted(Formatting.DARK_PURPLE))
                     .addLoreLine(Text.literal("Looting Level: "+lootingLvl).formatted(Formatting.AQUA)));
            }else if(mainDye || offDye){
               ItemStack dyeStack;
               if(mainDye){
                  dyeStack = player.getStackInHand(Hand.MAIN_HAND);
               }else{
                  dyeStack = player.getStackInHand(Hand.OFF_HAND);
               }
               DyeItem dyeItem = (DyeItem) dyeStack.getItem();
   
               setSlot(49,new GuiElementBuilder(dyeItem).setName(Text.literal("Hold a Weapon to Apply Looting / Hold a Dye to Color Sheep").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                     .addLoreLine(Text.literal("The color of dye held will be applied to the Sheep").formatted(Formatting.DARK_PURPLE))
                     .addLoreLine(Text.literal("").formatted(Formatting.DARK_PURPLE))
                     .addLoreLine(Text.literal("")
                           .append(Text.literal("Current Color: ")).formatted(Formatting.AQUA)
                           .append(Text.translatable(dyeItem.getTranslationKey()).formatted(Formatting.AQUA))));
            }else{
               setSlot(49,new GuiElementBuilder(Items.BOOK).setName(Text.literal("Hold a Weapon to Apply Looting / Hold a Dye to Color Sheep").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                     .addLoreLine(Text.literal("The level of looting on the weapon will be applied to drops").formatted(Formatting.DARK_PURPLE))
                     .addLoreLine(Text.literal("The color of dye held will be applied to the Sheep").formatted(Formatting.DARK_PURPLE)));
            }
         }
      }else if(mode == TokenGuiMode.BREEDING){
         setSlot(10,new GuiElementBuilder(Items.WOODEN_HOE).setName(Text.literal("Breed 1").formatted(Formatting.AQUA)).hideFlags());
         setSlot(11,new GuiElementBuilder(Items.STONE_HOE).setName(Text.literal("Breed 5").formatted(Formatting.DARK_AQUA)).hideFlags());
         setSlot(15,new GuiElementBuilder(Items.IRON_HOE).setName(Text.literal("Breed 10").formatted(Formatting.BLUE)).hideFlags());
         setSlot(16,new GuiElementBuilder(Items.DIAMOND_HOE).setName(Text.literal("Breed All").formatted(Formatting.LIGHT_PURPLE)));
   
         clearSlot(13);
         setSlotRedirect(13, new TokenSlot(token,mode,inv,0,0,0));
         
         List<Pair<ItemStack,Double>> reqItemList = token.getRequiredItems();
         List<ItemStack> foodItemList = token.getFoods();
         ArrayList<ItemStack> cycleItemList = new ArrayList<>(foodItemList);
         for(int i = 0; i < reqItemList.size(); i++){
            cycleItemList.add(reqItemList.get(i).getLeft());
         }
         
         int foodReq = token instanceof VillagerToken ? 32 : 2;
   
         
         int ticks = player.getServer().getTicks();
         int interval = 10;
         int itemInd = (ticks % (cycleItemList.size() * interval)) / interval;
         ItemStack cycleItem = cycleItemList.get(itemInd);
   
         GuiElementBuilder breedItem = new GuiElementBuilder();
         breedItem.setItem(cycleItem.getItem()).hideFlags().setName(Text.literal("Place an Appropriate Food/Required Item Above").formatted(Formatting.DARK_GREEN))
               .addLoreLine(Text.literal("It takes "+foodReq+" food to breed one creature").formatted(Formatting.GREEN));
         if(reqItemList.size() > 0){
            breedItem.addLoreLine(Text.literal("It takes 1 required item to support "+Math.round(1/reqItemList.get(0).getRight())+" creatures").formatted(Formatting.GREEN));
         }
         breedItem.addLoreLine(Text.literal(""))
               .addLoreLine(Text.literal("Appropriate Foods:").formatted(Formatting.DARK_GREEN));
         int loopMax = Math.min(5,foodItemList.size());
         for(int i = 0; i < loopMax; i++){
            breedItem.addLoreLine(Text.translatable(foodItemList.get(i).getItem().getTranslationKey()).formatted(Formatting.GREEN));
            if(foodItemList.size() > 5 && i == loopMax-1){
               breedItem.addLoreLine(Text.literal("...").formatted(Formatting.GREEN));
            }
         }
         if(reqItemList.size() > 0){
            breedItem.addLoreLine(Text.literal(""));
            breedItem.addLoreLine(Text.literal("Valid Required Items:").formatted(Formatting.DARK_GREEN));
            loopMax = Math.min(5,reqItemList.size());
            for(int i = 0; i < loopMax; i++){
               breedItem.addLoreLine(Text.translatable(reqItemList.get(i).getLeft().getItem().getTranslationKey()).formatted(Formatting.GREEN));
               if(reqItemList.size() > 5 && i == loopMax-1){
                  breedItem.addLoreLine(Text.literal("...").formatted(Formatting.GREEN));
               }
            }
         }
         
         setSlot(22,breedItem);
      }else if(mode == TokenGuiMode.TOOLS){
         for(int i = 10; i <= 16; i++){
            clearSlot(i);
            setSlotRedirect(i, new TokenSlot(token,mode,inv,i,0,0));
         }
   
         
         for(int i = 0; i < inv.size(); i++){
            inv.setStack(i,ItemStack.EMPTY);
         }
         for(NbtElement elem : tools){
            NbtCompound toolNbt = (NbtCompound) elem;
            inv.setStack(toolNbt.getByte("Slot"),ItemStack.fromNbt(toolNbt));
         }
   
         List<Pair<ItemStack, ItemStack>> activePairs = token.getActiveProducts();
         int[] toolShowSlots = dynamicSlots[activePairs.size()];
         for(int i = 0; i < toolShowSlots.length; i++){
            setSlot(19+toolShowSlots[i],new GuiElementBuilder(activePairs.get(i).getRight().getItem()).setName(Text.literal("Add Appropriate Tool to Receive this Drop").formatted(Formatting.DARK_GRAY))
                  .addLoreLine(Text.translatable("Requires: ").formatted(Formatting.GRAY).append(Text.translatable(activePairs.get(i).getLeft().getItem().getTranslationKey()))));
         }
      }else if(mode == TokenGuiMode.STORAGE){
         int k = 0;
         for(int i = 0; i < 4; i++){
            for(int j = 0; j < 7; j++){
               clearSlot((i*9+10)+j);
               setSlotRedirect((i*9+10)+j,new TokenSlot(token, mode, inv,k,j,i));
               k++;
            }
         }
   
         for(int i = 0; i < inv.size(); i++){
            inv.setStack(i,ItemStack.EMPTY);
         }
         for(NbtElement elem : storage){
            NbtCompound storageNbt = (NbtCompound) elem;
            inv.setStack(storageNbt.getByte("Slot"),ItemStack.fromNbt(storageNbt));
         }
   
         GuiElementBuilder outputItem = new GuiElementBuilder(Items.HOPPER).setName(Text.literal("Output Mode").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
               .addLoreLine(Text.literal("Click to cycle what happens to produced items").formatted(Formatting.DARK_PURPLE))
               .addLoreLine(Text.literal(""));
         
         if(outputMode == 0){
            outputItem.addLoreLine(Text.literal("Storing Items Here & Deleting Excess").formatted(Formatting.GOLD));
         }else if(outputMode == 1){
            outputItem.addLoreLine(Text.literal("Storing Items Here & Dropping Excess").formatted(Formatting.AQUA));
         }else if(outputMode == 2){
            outputItem.addLoreLine(Text.literal("Dropping Items").formatted(Formatting.GREEN));
         }else if(outputMode == 3){
            outputItem.addLoreLine(Text.literal("Deleting Produce").formatted(Formatting.RED));
         }
         
         setSlot(49,outputItem);
      }else if(mode == TokenGuiMode.TRADING_MENU){
         if(token instanceof VillagerToken villagerToken){
            List<NbtCompound> villagers = villagerToken.getVillagerTags(tokenData);
            List<NbtCompound> pageItems = listToPage(villagers, page);
            int numPages = (int) Math.ceil((float)villagers.size()/28.0);
      
            int k = 0;
            for(int i = 0; i < 4; i++){
               for(int j = 0; j < 7; j++){
                  clearSlot((i*9+10)+j);
                  if(k < pageItems.size()){
                     NbtCompound villager = pageItems.get(k);
                     NbtCompound villagerData = villager.getCompound("VillagerData");
                     String profession = villagerData.getString("profession").replace("minecraft:","");
                     boolean zombie = villager.getBoolean("zombie");
                     ItemStack headStack = MobHeadUtils.getProfessionHead(profession,zombie);
                     GuiElementBuilder villagerHead = GuiElementBuilder.from(headStack);
                     String nameString = villager.getString("name");
                     String profKey = profession.equals("none") ? "entity.minecraft.villager" : "entity.minecraft.villager."+profession;
                     MutableText jobText = Text.translatable(profKey);
                     String prefix = villager.getBoolean("favorite") ? "✩ " : "";
                     MutableText name = Text.literal(prefix + (nameString.isEmpty() ? jobText.getString() : nameString)).formatted(Formatting.GREEN,Formatting.BOLD);
                     if(villager.getBoolean("baby")){
                        villagerHead.setName(name.append(" - Baby"));
                     }else{
                        villagerHead.setName(name);
                     }
                     if(!zombie){
                        villagerHead.addLoreLine(Text.literal("Left Click to trade with the Villager").formatted(Formatting.DARK_GREEN));
                        villagerHead.addLoreLine(Text.literal("Right Click to edit the Villager").formatted(Formatting.AQUA));
                     }else{
                        int cureTime = villager.getInt("cureTime");
                        villagerHead.addLoreLine(Text.literal("Villager will be Cured in "+(cureTime/20)+" seconds").formatted(Formatting.DARK_GREEN));
                     }
                     
                     setSlot((i*9+10)+j,villagerHead);
                  }else{
                     setSlot((i*9+10)+j,new GuiElementBuilder(Items.AIR));
                  }
                  k++;
               }
            }
      
            GuiElementBuilder nextPage = new GuiElementBuilder(Items.ARROW).setName(Text.literal("Next Page").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("Click to go to the next page").formatted(Formatting.DARK_PURPLE));
            setSlot(53,nextPage);
      
            GuiElementBuilder prevPage = new GuiElementBuilder(Items.ARROW).setName(Text.literal("Previous Page").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("Click to go to the previous page").formatted(Formatting.DARK_PURPLE));
            setSlot(45,prevPage);
   
            int restockTimer = tokenData.getInt("restockTimer");
            GuiElementBuilder clock = new GuiElementBuilder(Items.CLOCK).setName(Text.literal("Time Till Next Restock").formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal((restockTimer/20)+" Seconds").formatted(Formatting.DARK_PURPLE));
            setSlot(8,clock);
   
            String sortStr = "";
            int sortType = tokenData.getInt("sortType");
            if(sortType == 0){
               sortStr = "XP";
            }else if(sortType == 1){
               sortStr = "Profession";
            }
            GuiElementBuilder hopper = new GuiElementBuilder(Items.HOPPER).setName(Text.literal("Sort By: "+sortStr).formatted(Formatting.LIGHT_PURPLE)).hideFlags()
                  .addLoreLine(Text.literal("Click to Cycle Sorting Type").formatted(Formatting.DARK_PURPLE));
            setSlot(0,hopper);
         }
      }else if(mode == TokenGuiMode.VILLAGER_MENU){
         if(token instanceof VillagerToken villagerToken){
            ItemStack workItem = ItemStack.EMPTY;
            VillagerProfession prof = null;
            if(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BlockItem m){
               BlockState mainBlock = m.getBlock().getDefaultState();
               Optional<RegistryEntry<PointOfInterestType>> opt = PointOfInterestTypes.getTypeForState(mainBlock);
               if(opt.isPresent()){
                  RegistryEntry<PointOfInterestType> type = opt.get();
                  boolean valid = VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test(type);
                  if(valid){
                     workItem = player.getStackInHand(Hand.MAIN_HAND);
                     for(Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> e : Registries.VILLAGER_PROFESSION.getEntrySet()){
                        RegistryKey<VillagerProfession> key = e.getKey();
                        VillagerProfession p = e.getValue();
                        if(p.acquirableWorkstation().test(type) && !p.id().replace("minecraft:", "").equals("none")){
                           prof = p;
                           break;
                        }
                     }
                  }
               }
            }else if(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof BlockItem m){
               BlockState mainBlock = m.getBlock().getDefaultState();
               Optional<RegistryEntry<PointOfInterestType>> opt = PointOfInterestTypes.getTypeForState(mainBlock);
               if(opt.isPresent()){
                  RegistryEntry<PointOfInterestType> type = opt.get();
                  boolean valid = VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test(type);
                  if(valid){
                     workItem = player.getStackInHand(Hand.MAIN_HAND);
                     for(Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> e : Registries.VILLAGER_PROFESSION.getEntrySet()){
                        RegistryKey<VillagerProfession> key = e.getKey();
                        VillagerProfession p = e.getValue();
                        if(p.acquirableWorkstation().test(type) && !p.id().replace("minecraft:", "").equals("none")){
                           prof = p;
                           break;
                        }
                     }
                  }
               }
            }
            
            String workstationName = "-";
            String jobString = "-";
            Item workstationItem = Items.CRAFTING_TABLE;
            if(!workItem.isEmpty() && prof != null){
               String professionName = prof.id().replace("minecraft:","");
               MutableText jobName = Text.translatable(professionName.equals("none") ? "entity.minecraft.villager" : "entity.minecraft.villager."+professionName);
               jobString = jobName.getString();
               workstationName = Text.translatable(workItem.getItem().getTranslationKey()).getString() + " ("+jobString+")";
               workstationItem = workItem.getItem();
            }
            
            NbtCompound villager = guiData;
            NbtCompound villagerData = villager.getCompound("VillagerData");
            String profession = villagerData.getString("profession").replace("minecraft:","");
            ItemStack headStack = MobHeadUtils.getProfessionHead(profession,villager.getBoolean("zombie"));
            tokenHead = GuiElementBuilder.from(headStack);
            String nameString = villager.getString("name");
            String profKey = profession.equals("none") ? "entity.minecraft.villager" : "entity.minecraft.villager."+profession;
            MutableText jobText = Text.translatable(profKey);
            String prefix = villager.getBoolean("favorite") ? "✩ " : "";
            MutableText name = Text.literal(prefix + (nameString.isEmpty() ? jobText.getString() : nameString)).formatted(Formatting.GREEN,Formatting.BOLD);
            if(villager.getBoolean("baby")){
               tokenHead.setName(name.append(" - Baby"));
            }else{
               tokenHead.setName(name);
            }
            
            int xp = villager.getInt("Xp");
            int level = villagerData.getInt("level");
            tokenHead.addLoreLine(Text.literal("Profession: "+jobText.getString()).formatted(Formatting.DARK_PURPLE));
            tokenHead.addLoreLine(Text.literal("Xp: "+xp+" (Level "+level+")").formatted(Formatting.DARK_PURPLE));
            
            setSlot(10,new GuiElementBuilder(Items.EMERALD).setName(Text.literal("Trade").formatted(Formatting.GREEN)).hideFlags()
                  .addLoreLine(Text.literal("Click to trade with the Villager").formatted(Formatting.DARK_PURPLE)));
   
            setSlot(12,new GuiElementBuilder(Items.NAME_TAG).setName(Text.literal("Rename").formatted(Formatting.AQUA)).hideFlags()
                  .addLoreLine(Text.literal("Click to rename the Villager").formatted(Formatting.DARK_PURPLE)));
   
            setSlot(14,new GuiElementBuilder(workstationItem).setName(Text.literal("Give Workstation / Re-roll Trades").formatted(Formatting.GOLD)).hideFlags()
                  .addLoreLine(Text.literal("Click to give the Villager a workstation (if unemployed or at 0 XP)").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("This will change their profession or re-roll their trades").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("Changing profession consumes the workstation").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("Workstation in hand: "+workstationName).formatted(Formatting.LIGHT_PURPLE)));
   
            setSlot(16,new GuiElementBuilder(Items.DIAMOND_SWORD).setName(Text.literal("Retire").formatted(Formatting.RED)).hideFlags()
                  .addLoreLine(Text.literal("Click to remove the Villager from the token (Permanent!)").formatted(Formatting.DARK_RED)));
   
            boolean favorite = villager.getBoolean("favorite");
            setSlot(20,new GuiElementBuilder(Items.NETHER_STAR).setName(Text.literal(favorite ? "Un-Favorite" : "Favorite").formatted(Formatting.AQUA)).hideFlags()
                  .addLoreLine(Text.literal("Click to add/remove the Villager from your favorites list").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("Favorited Villagers appear earlier in the menu").formatted(Formatting.DARK_PURPLE)));
   
            setSlot(22,new GuiElementBuilder(Items.GOLDEN_APPLE).setName(Text.literal("Convert and Cure").formatted(Formatting.GOLD)).hideFlags()
                  .addLoreLine(Text.literal("Converts the Villager to a Zombie and cures it after 5 minutes").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("Requires a captured Zombie to use").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("Consumes a weakness potion and golden apple").formatted(Formatting.DARK_PURPLE))
                  .addLoreLine(Text.literal("The trading bonus is applied to everyone using the token").formatted(Formatting.DARK_PURPLE)));
   
            setSlot(24,new GuiElementBuilder(Items.IRON_INGOT).setName(Text.literal("Send to Iron Farm").formatted(Formatting.GRAY)).hideFlags()
                  .addLoreLine(Text.literal("Click to send the Villager to the Iron Farm (Permanent!)").formatted(Formatting.DARK_RED)));
         }
      }
   
      setSlot(4,tokenHead);
      listener.finishUpdate();
   }
   
   public static <T> List<T> listToPage(List<T> items, int page){
      if(page <= 0){
         return items;
      }else if(28*(page-1) >= items.size()){
         return new ArrayList<>();
      }else{
         return items.subList(28*(page-1), Math.min(items.size(), 28*page));
      }
   }
   
   public void returnItems(){
      List<ItemStack> items = new ArrayList<>();
      if(mode == TokenGuiMode.BREEDING){
         items.add(inv.getStack(0));
      }
   
      for(ItemStack stack : items){
         if(!stack.isEmpty()){
         
            ItemEntity itemEntity;
            boolean bl = player.getInventory().insertStack(stack);
            if (!bl || !stack.isEmpty()) {
               itemEntity = player.dropItem(stack, false);
               if (itemEntity == null) return;
               itemEntity.resetPickupDelay();
               itemEntity.setOwner(player.getUuid());
               return;
            }
            stack.setCount(1);
            itemEntity = player.dropItem(stack, false);
            if (itemEntity != null) {
               itemEntity.setDespawnImmediately();
            }
         }
      }
   }
   
   @Override
   public void onClose(){ // Return items
      returnItems();
      tokenBlock.setGuiOpen(false);
      
      if(mode == TokenGuiMode.VILLAGER_MENU){
         token.openGui(player,tokenBlock,TokenGuiMode.TRADING_MENU);
      }
   }
   
   public enum TokenGuiMode{
      MAIN_MENU,
      BREEDING,
      KILLING,
      STORAGE,
      TOOLS,
      TRADING_MENU,
      IRON_MENU,
      SHEEP_MENU,
      VILLAGER_MENU
   }
}


