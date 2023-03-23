package net.borisshoes.mobtokens.guis;

import com.google.common.collect.Sets;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.MerchantGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.utils.MobHeadUtils;
import net.borisshoes.mobtokens.utils.TradeGenerationUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;

import java.util.HashSet;
import java.util.Map;

public class TradingGui extends MerchantGui implements TokenRelatedGui{
   
   private final MobToken token;
   private final TokenBlock tokenBlock;
   private final NbtCompound guiData;
   private boolean tradeReady = false;
   private boolean shift = false;
   private boolean allow = false;
   
   /**
    * Constructs a new MerchantGui for the supplied player.
    *
    * @param player                the player to serve this gui to
    *                              will be treated as slots of this gui
    */
   public TradingGui(ServerPlayerEntity player, MobToken token, TokenBlock tokenBlock, NbtCompound guiData){
      super(player, false);
      this.token = token;
      this.guiData = guiData;
      this.tokenBlock = tokenBlock;
   
      buildGui();
   }
   
   public TokenBlock getTokenBlock(){
      return tokenBlock;
   }
   
   public void close(){
      super.close();
   }
   
   public SimpleGui getGui(){
      return this;
   }
   
   private void buildGui(){
      this.merchant.getOffers().clear();
      NbtCompound villagerData = guiData.getCompound("VillagerData");
      String profession = villagerData.getString("profession").replace("minecraft:","");
      String nameString = guiData.getString("name");
      String profKey = profession.equals("none") ? "entity.minecraft.villager" : "entity.minecraft.villager."+profession;
      MutableText jobText = Text.translatable(profKey);
      String prefix = guiData.getBoolean("favorite") ? "✩ " : "";
      MutableText name = Text.literal(prefix + (nameString.isEmpty() ? jobText.getString() : nameString)).formatted(Formatting.BOLD);
      setTitle(name);
      int xp = guiData.getInt("Xp");
      setExperience(xp);
      setLevel(VillagerLevel.fromXp(xp));
      setIsLeveled(true);
      int reputation = guiData.getInt("gossip");
   
      NbtCompound offersComp = guiData.getCompound("Offers");
      for(TradeOffer tradeOffer : new TradeOfferList(offersComp)){
         tradeOffer.clearSpecialPrice();
         if(reputation != 0) tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)reputation * tradeOffer.getPriceMultiplier()));
         if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
            int j = statusEffectInstance.getAmplifier();
            double d = 0.3 + 0.0625 * (double)j;
            int k = (int)Math.floor(d * (double)tradeOffer.getOriginalFirstBuyItem().getCount());
            tradeOffer.increaseSpecialPrice(-Math.max(k, 1));
         }
         
         addTrade(tradeOffer);
      }
   
      
      
   }
   
   @Override
   public void onSelectTrade(TradeOffer offer) {
      //this.player.sendMessage(Text.literal("Selected Trade: " + this.getOfferIndex(offer)), false);
   }
   
   @Override
   public boolean onAnyClick(int index, ClickType type, SlotActionType action){
      //System.out.println("Click! "+type.name()+" "+action.name()+" "+index);
      
      if(index == 2 && !getSlotRedirect(2).getStack().isEmpty()){
         if(action == SlotActionType.PICKUP || type == ClickType.DROP){
            tradeReady = true;
            allow = true;
            shift = false;
         }else if(action == SlotActionType.QUICK_MOVE || type == ClickType.CTRL_DROP){
            tradeReady = true;
            allow = true;
            shift = true;
         }else{
            allow = false;
         }
      }
      return true;
   }
   
   @Override
   public boolean onTrade(TradeOffer offer) {
      if(tradeReady) {
         int times = 1;
         if(shift){
            ItemStack b1 = getSlotRedirect(0).getStack();
            ItemStack b2 = getSlotRedirect(1).getStack();
            ItemStack s1 = getSlotRedirect(2).getStack();
            int t1 = b1.getCount() / offer.getAdjustedFirstBuyItem().getCount();
            times = Math.min(offer.getMaxUses()-offer.getUses(),t1);
            if(!offer.getSecondBuyItem().isEmpty()){
               int t2 = b2.getCount() / offer.getSecondBuyItem().getCount();
               times = Math.min(times,t2);
            }
         }
         
         //System.out.println("Buying "+times);
         
         NbtCompound offersComp = guiData.getCompound("Offers");
         TradeOfferList offerList = new TradeOfferList(offersComp);
         for(TradeOffer tradeOffer : offerList){
            boolean usesMatch = offer.getMaxUses() == tradeOffer.getMaxUses();
            boolean xpMatch = offer.getMerchantExperience() == tradeOffer.getMerchantExperience();
            boolean stack1Match = ItemStack.areEqual(offer.getSellItem(), tradeOffer.getSellItem());
            boolean stack2Match = ItemStack.areEqual(offer.getOriginalFirstBuyItem(), tradeOffer.getOriginalFirstBuyItem());
            boolean stack3Match = ItemStack.areEqual(offer.getSecondBuyItem(), tradeOffer.getSecondBuyItem());
            
            if(usesMatch && xpMatch && stack3Match && stack2Match && stack1Match){
               //System.out.println("Uses: "+tradeOffer.getUses()+" Max: "+tradeOffer.getMaxUses());
               for(int i = 0; i < times; i++){
                  tradeOffer.use();
                  player.addExperience(3+(int)(Math.random()*5));
               }
               guiData.putInt("Xp",guiData.getInt("Xp")+times*tradeOffer.getMerchantExperience());
               if(TradeGenerationUtils.canLevelUp(guiData)){
                  TradeGenerationUtils.levelUp(player.getWorld(),guiData,offerList);
                  buildGui();
               }
               //System.out.println("Uses: "+tradeOffer.getUses()+" Max: "+tradeOffer.getMaxUses());
               break;
            }
         }
   
         guiData.put("Offers",offerList.toNbt());
         tradeReady = false;
         shift = false;
      }
      return allow;
   }
   
   @Override
   public void onSuggestSell(TradeOffer offer) {
   
   }
   
   @Override
   public void onClose(){
      if(token != null){
         token.openGui(player,tokenBlock, TokenGui.TokenGuiMode.TRADING_MENU);
      }
   }
}
