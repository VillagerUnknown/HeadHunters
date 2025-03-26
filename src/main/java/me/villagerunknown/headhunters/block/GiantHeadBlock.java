package me.villagerunknown.headhunters.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class GiantHeadBlock extends HorizontalFacingBlock {
	
	public static final MapCodec<GiantHeadBlock> CODEC = createCodec(GiantHeadBlock::new);
	
	public MapCodec<GiantHeadBlock> getCodec() {
		return CODEC;
	}
	
	public GiantHeadBlock(AbstractBlock.Settings settings) {
		super(settings);
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING});
	}
	
}
