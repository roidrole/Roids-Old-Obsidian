package com.legobmw99.oldobsidian.matchers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockMatcher extends SimpleMatcher {
	public Block block;

	public BlockMatcher(Block block){
		this.block = block;
	}

	@Override
	public boolean matches(IBlockState state) {
		return state.getBlock() == block;
	}

	@Override
	public Object getInternal() {
		return block;
	}
}
