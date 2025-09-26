package com.legobmw99.oldobsidian;

import com.legobmw99.oldobsidian.matchers.IBlockStateMatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class ConversionDescription {
	//If extending the class, you only need liquid1 to be set.
	//The other parameters are public only so I can create them via Json easier.
	//They are still used in the default checkAndPerformConversion, however.
	public IBlockStateMatcher liquid1;
	public IBlockStateMatcher liquid2;
	public IBlockStateMatcher dust;
	public IBlockState result;

	//If extending the class, you don't need to match liquid1, as that one is compared in ConversionSer already with hashing.
	//To circumvent this, use a matcher that is always true and check however you want here.
	//Be wary, however, we are listening to every block update, so exit-early whenever possible
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
