package net.borisshoes.mobtokens.cardinalcomponents;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

import java.util.List;

public interface ITokenBlockComponent extends ComponentV3 {
   List<TokenBlock> getBlocks();
   boolean addBlock(TokenBlock block);
   boolean removeBlock(TokenBlock block);
}

