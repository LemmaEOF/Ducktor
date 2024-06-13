package gay.lemmaeof.ducktor.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.lemmaeof.ducktor.Ducktor;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class MixinInGameHud {
	@Shadow
	private PlayerEntity getCameraPlayer() {
		throw new IllegalStateException("illegal");
	}

	@ModifyExpressionValue(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
	private boolean injectRejuvinationEffect(boolean original) {
		if (getCameraPlayer().hasStatusEffect(Ducktor.REJUVINATION)) {
			return true;
		}
		return original;
	}
}
