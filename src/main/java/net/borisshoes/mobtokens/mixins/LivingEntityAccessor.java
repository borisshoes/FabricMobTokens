package net.borisshoes.mobtokens.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
   
   @Invoker
   public LootContext.Builder invokeGetLootContextBuilder(boolean causedByPlayer, DamageSource source);
}
