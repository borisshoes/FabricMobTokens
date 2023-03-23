package net.borisshoes.mobtokens.guis;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;
import net.borisshoes.mobtokens.tokens.MobToken;
import net.borisshoes.mobtokens.tokens.VillagerToken;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class RenameGui extends AnvilInputGui implements TokenRelatedGui{
   private final MobToken token;
   private final TokenBlock tokenBlock;
   private final NbtCompound guiData;
   private final ItemStack item;
   private String newName;
   
   /**
    * Constructs a new input gui for the provided player.
    * @param player                the player to serve this gui to
    *                              will be treated as slots of this gui
    */
   public RenameGui(ServerPlayerEntity player, MobToken token, TokenBlock tokenBlock, NbtCompound guiData, ItemStack item){
      super(player, false);
      this.token = token;
      this.guiData = guiData;
      this.newName = guiData.getString("name");
      this.tokenBlock = tokenBlock;
      this.item = item;
   
      setDefaultInputValue(guiData.getString("name"));
      GuiElementBuilder headItem = GuiElementBuilder.from(item);
      headItem.setName(Text.literal(guiData.getString("name")));
      setSlot(0, headItem);
      
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
   
   @Override
   public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
      if(index == 2){
         this.close();
      }
      return true;
   }
   
   @Override
   public void onInput(String input) {
      GuiElementBuilder headItem = GuiElementBuilder.from(item);
      headItem.setName(Text.literal(guiData.getString("name")));
      setSlot(0, headItem);
      
      ItemStack newItem = item.copy();
      if(!input.equals(newName) && !input.isEmpty()){
         newName = input;
         setSlot(2, GuiElementBuilder.from(newItem).setName(Text.literal(newName)));
      }
      
   }
   
   @Override
   public void onClose(){
      if(token != null){
         guiData.putString("name",newName);
         if(tokenBlock.getData() == guiData){
            token.openGui(player,tokenBlock, TokenGui.TokenGuiMode.MAIN_MENU);
         }else{
            token.openVillagerGui(player,tokenBlock,guiData);
         }
      }
   }
}