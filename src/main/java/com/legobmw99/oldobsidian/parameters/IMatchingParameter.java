package com.legobmw99.oldobsidian.parameters;

import net.minecraft.block.state.IBlockState;

public interface IMatchingParameter {
	boolean matches(IBlockState state);
}
