package com.legobmw99.oldobsidian.matchers;

// Matcher only used for hashing. Do not extend unless you know what you are doing
public abstract class SimpleMatcher implements IBlockStateMatcher {
	public abstract Object getInternal();

	@Override
	public String toString(){
		return this.getClass().getSimpleName()+":"+getInternal().toString();
	}
}
