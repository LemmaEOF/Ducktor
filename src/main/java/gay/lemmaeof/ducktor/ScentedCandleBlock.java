package gay.lemmaeof.ducktor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.ToIntFunction;

public class ScentedCandleBlock extends AbstractCandleBlock {
	public static final MapCodec<ScentedCandleBlock> CODEC = createCodec(ScentedCandleBlock::new);
	private static final Iterable<Vec3d> PARTICLE_OFFSETS = ImmutableList.of(new Vec3d(0.5, 1.0, 0.5));
	public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.get(LIT) ? 10 : 0;
	public static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 6, 11);

	@Override
	protected MapCodec<ScentedCandleBlock> getCodec() {
		return CODEC;
	}

	public ScentedCandleBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIT);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE)) {
			return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		} else if (stack.isEmpty() && state.get(LIT)) {
			extinguish(player, state, world, pos);
			return ItemActionResult.success(world.isClient);
		} else {
			return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
		}
	}

	@Override
	protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
		return PARTICLE_OFFSETS;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		if (state.get(LIT)) {
			AreaEffectCloudEntity rejuvenation = new AreaEffectCloudEntity(world, pos.getX()+0.5, pos.getY()+6/16D, pos.getZ()+0.5);
			rejuvenation.setPotionContents(new PotionContentsComponent(Ducktor.REJUVENATION_POTION));
			rejuvenation.setRadius(1);
			rejuvenation.setRadiusOnUse(-1f);
			rejuvenation.setWaitTime(10);
			rejuvenation.setRadiusGrowth(-rejuvenation.getRadius() / (float) rejuvenation.getDuration());
			world.spawnEntity(rejuvenation);
		}
	}
}
