package com.legobmw99.oldobsidian.matchers;

import com.legobmw99.oldobsidian.conversions.IConversion;
import net.minecraft.block.state.IBlockState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StateMatcher extends SimpleMatcher<IBlockState> {
	public IBlockState state;
	public static Map<IBlockState, Collection<IConversion>> stateMatchers = new HashMap<>();

	public StateMatcher(IBlockState state){
		this.state = state;
	}

	@Override
	public boolean matches(IBlockState state) {
		return this.state == state;
	}

	@Override
	public IBlockState getInternal() {
		return state;
	}

	@Override
	public Map<IBlockState, Collection<IConversion>> getMap() {
		return stateMatchers;
	}
}
