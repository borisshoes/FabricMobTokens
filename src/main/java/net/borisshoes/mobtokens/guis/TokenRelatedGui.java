package net.borisshoes.mobtokens.guis;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.borisshoes.mobtokens.cardinalcomponents.TokenBlock;

public interface TokenRelatedGui {
   public TokenBlock getTokenBlock();
   
   public void close();
   
   public SimpleGui getGui();
   
}
