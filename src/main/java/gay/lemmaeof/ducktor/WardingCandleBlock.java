package gay.lemmaeof.ducktor;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;

import java.util.Optional;

public class WardingCandleBlock extends ScentedCandleBlock {
	public WardingCandleBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(LIT)) {
			world.createExplosion(
					null,
					world.getDamageSources().explosion(null, null),
					new AdvancedExplosionBehavior(false, true, Optional.empty(), Optional.empty()),
					new Vec3d(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5),
					2,
					false,
					World.ExplosionSourceType.BLOCK
				);
		}
	}
}
