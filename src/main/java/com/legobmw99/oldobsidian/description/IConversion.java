package com.legobmw99.oldobsidian.description;

import net.minecraft.block.state.IBlockState;

public interface IConversion {
	boolean matchesLiquid1(IBlockState state);

	boolean matchesLiquid2(IBlockState state);

	boolean matchesDust(IBlockState state);

	IBlockState getResult();
}
