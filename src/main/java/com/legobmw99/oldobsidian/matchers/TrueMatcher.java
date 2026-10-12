package com.legobmw99.oldobsidian.matchers;

import net.minecraft.block.state.IBlockState;

public class TrueMatcher implements IBlockStateMatcher {
	public static final TrueMatcher INSTANCE = new TrueMatcher();
	@Override
	public boolean matches(IBlockState state) {
		return true;
	}
}
