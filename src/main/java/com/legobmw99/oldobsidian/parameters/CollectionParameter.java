package com.legobmw99.oldobsidian.parameters;

import net.minecraft.block.state.IBlockState;

import java.util.Collection;

public class CollectionParameter implements IMatchingParameter {
	public final Collection<IMatchingParameter> internal;

	public CollectionParameter(Collection<IMatchingParameter> internal){
		this.internal = internal;
	}

	@Override
	public boolean matches(IBlockState state){
		for (IMatchingParameter parameter : internal) {
			if (parameter.matches(state)) {
				return true;
			}
		}
		return false;
	}
}
