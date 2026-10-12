package com.legobmw99.oldobsidian.matchers;

import com.legobmw99.oldobsidian.conversions.IConversion;

import java.util.*;

// Matcher only used for hashing. Do not extend unless you know what you are doing
public abstract class SimpleMatcher<T> implements IBlockStateMatcher {
	public abstract T getInternal();

	public abstract Map<T, Collection<IConversion>> getMap();

	public void addToMap(IConversion conversion){
		this.getMap().compute(
			this.getInternal(),
			(key, oldValue) -> {
				if(oldValue == null){
					return Collections.singletonList(conversion);
				} else if (oldValue.size() == 1){
					List<IConversion> list = new ArrayList<>(2);
					list.add(oldValue.iterator().next());
					list.add(conversion);
					return list;
				} else {
					oldValue.add(conversion);
					return oldValue;
				}
			}
		);
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName()+":"+getInternal().toString();
	}
}
