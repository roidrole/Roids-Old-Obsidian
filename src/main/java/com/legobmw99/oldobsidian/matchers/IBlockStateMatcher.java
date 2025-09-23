package com.legobmw99.oldobsidian.matchers;

import net.minecraft.block.state.IBlockState;

public interface IBlockStateMatcher {
	boolean matches(IBlockState state);
}
