package gay.lemmaeof.ducktor.mixin;

import gay.lemmaeof.ducktor.Ducktor;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
	@Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "heal", at = @At("HEAD"), cancellable = true)
	private void cancelHealing(float amount, CallbackInfo info) {
		if (this.hasStatusEffect(Ducktor.REJUVENATION)) {
			info.cancel();
		}
	}

}