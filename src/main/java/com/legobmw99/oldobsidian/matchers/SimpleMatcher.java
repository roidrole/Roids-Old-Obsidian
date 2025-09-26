package com.legobmw99.oldobsidian.matchers;

public abstract class SimpleMatcher implements IBlockStateMatcher {
	public abstract Object getInternal();

	@Override
	public String toString(){
		return this.getClass().getSimpleName()+":"+getInternal().toString();
	}
}
