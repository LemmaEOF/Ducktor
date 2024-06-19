package gay.lemmaeof.ducktor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.function.ToIntFunction;

public class ScentedCandleBlock extends AbstractCandleBlock implements Waterloggable {
	public static final MapCodec<ScentedCandleBlock> CODEC = createCodec(ScentedCandleBlock::new);
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final Iterable<Vec3d> PARTICLE_OFFSETS = ImmutableList.of(new Vec3d(0.5, 9/16f, 0.5));
	public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.get(LIT) ? 10 : 0;
	public static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 7, 11);

	@Override
	protected MapCodec<ScentedCandleBlock> getCodec() {
		return CODEC;
	}

	public ScentedCandleBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false).with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIT, WATERLOGGED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean bl = fluidState.getFluid() == Fluids.WATER;
		return super.getPlacementState(ctx).with(WATERLOGGED, bl);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			BlockState blockState = state.with(WATERLOGGED, Boolean.valueOf(true));
			if (state.get(LIT)) {
				extinguish(null, blockState, world, pos);
			} else {
				world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
			}

			world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
		return PARTICLE_OFFSETS;
	}

	@Override
	protected boolean isNotLit(BlockState state) {
		return !state.get(WATERLOGGED) && super.isNotLit(state);
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
