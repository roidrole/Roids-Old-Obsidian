package com.legobmw99.oldobsidian.matchers;

import net.minecraft.block.state.IBlockState;

public class StateMatcher extends SimpleMatcher {
	public IBlockState state;

	public StateMatcher(IBlockState state){
		this.state = state;
	}

	@Override
	public boolean matches(IBlockState state) {
		return this.state == state;
	}

	@Override
	public Object getInternal() {
		return state;
	}
}
