package com.legobmw99.oldobsidian.conversions;

import com.legobmw99.oldobsidian.matchers.IBlockStateMatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class ConversionDescription implements IConversion{
	public IBlockStateMatcher liquid1;
	public IBlockStateMatcher liquid2;
	public IBlockStateMatcher dust;
	public IBlockState result;

	@Override
	public IBlockStateMatcher getLiquid1(){
		return liquid1;
	}

	@Override
	public boolean checkAndPerformConversion(BlockEvent.NeighborNotifyEvent event){
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockPos dustPos = pos;

		boolean foundDust = false;
		for(EnumFacing facing : event.getNotifiedSides()){
			dustPos = pos.offset(facing);
			if(dust.matches(world.getBlockState(dustPos))){
				foundDust = true;
				break;
			}
		}
		if(!foundDust){
			return false;
		}
		for (EnumFacing facing : EnumFacing.HORIZONTALS){
			if(liquid2.matches(world.getBlockState(dustPos.offset(facing)))){
				world.setBlockState(dustPos, result, 2);
				world.playSound(
					null,
					dustPos,
					SoundEvents.BLOCK_LAVA_EXTINGUISH,
					SoundCategory.BLOCKS,
					0.5F,
					2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
				);
				return true;
			}
		}
		return false;
	}
}
