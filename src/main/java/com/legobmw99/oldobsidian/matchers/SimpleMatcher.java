package com.legobmw99.oldobsidian.matchers;

import com.legobmw99.oldobsidian.conversions.IConversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Matcher only used for hashing. Do not extend unless you know what you are doing
public abstract class SimpleMatcher<T> implements IBlockStateMatcher {
	public abstract T getInternal();

	public abstract Map<T, Collection<IConversion>> getMap();

	public void addToMap(IConversion conversion){
		this.getMap().computeIfAbsent(
			this.getInternal(),
			key -> new ArrayList<>(1)
		).add(conversion);
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName()+":"+getInternal().toString();
	}
}
