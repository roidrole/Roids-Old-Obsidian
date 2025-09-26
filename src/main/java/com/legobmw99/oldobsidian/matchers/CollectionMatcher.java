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

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder("CollectionMatcher: [");
		boolean first = true;
		for (IBlockStateMatcher matcher : internal){
			if(first){
				first = false;
			} else {
				output.append(", ");
			}
			output.append(matcher.toString());
		}
		output.append(']');
		return output.toString();
	}
}
