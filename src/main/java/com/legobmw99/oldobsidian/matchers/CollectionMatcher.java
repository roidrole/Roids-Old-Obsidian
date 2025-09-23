package com.legobmw99.oldobsidian.matchers;

import net.minecraft.block.state.IBlockState;

import java.util.Collection;

public class CollectionMatcher implements IBlockStateMatcher {
	public final Collection<IBlockStateMatcher> internal;

	public CollectionMatcher(Collection<IBlockStateMatcher> internal){
		this.internal = internal;
	}

	@Override
	public boolean matches(IBlockState state){
		for (IBlockStateMatcher parameter : internal) {
			if (parameter.matches(state)) {
				return true;
			}
		}
		return false;
	}
}
