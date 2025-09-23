package com.legobmw99.oldobsidian.matchers;

import com.legobmw99.oldobsidian.PlaintextId;
import net.minecraft.block.state.IBlockState;

import java.util.regex.Pattern;

public class RegexMatcher implements IBlockStateMatcher {
	public final Pattern recipeObject;

	public RegexMatcher(String recipeObj){
		recipeObj = recipeObj.replaceAll("\\*", ".*");
		this.recipeObject = Pattern.compile(recipeObj);
	}

	@Override
	public boolean matches(IBlockState state) {
		return recipeObject.matcher(PlaintextId.getBlockId(state)).matches();
	}
}
