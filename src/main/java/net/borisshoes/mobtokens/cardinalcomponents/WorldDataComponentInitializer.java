package net.borisshoes.mobtokens.cardinalcomponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class WorldDataComponentInitializer implements WorldComponentInitializer {
   public static final ComponentKey<ITokenBlockComponent> TOKEN_BLOCK_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("mobtokens", "tokenblocks"), ITokenBlockComponent.class);
   
   @Override
   public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry){
      registry.register(TOKEN_BLOCK_COMPONENT, TokenBlockComponent.class, world -> new TokenBlockComponent());
   }
}
