package com.legobmw99.oldobsidian.matchers;

import com.legobmw99.oldobsidian.conversions.IConversion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockMatcher extends SimpleMatcher<Block> {
	public Block block;
	public static Map<Block, Collection<IConversion>> blockMatchers = new HashMap<>();

	public BlockMatcher(Block block){
		this.block = block;
	}

	@Override
	public boolean matches(IBlockState state) {
		return state.getBlock() == block;
	}

	@Override
	public Block getInternal() {
		return block;
	}

	@Override
	public Map<Block, Collection<IConversion>> getMap() {
		return blockMatchers;
	}
}
