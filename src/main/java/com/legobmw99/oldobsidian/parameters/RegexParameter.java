package com.legobmw99.oldobsidian.parameters;

import com.legobmw99.oldobsidian.mcutil.PlaintextId;
import net.minecraft.block.state.IBlockState;

import java.util.regex.Pattern;

public class RegexParameter implements IMatchingParameter{
	public final Pattern recipeObject;

	public RegexParameter(String recipeObj){
		recipeObj = recipeObj.replaceAll("\\*", ".*");
		recipeObj = PlaintextId.fixMetadata(recipeObj, PlaintextId.IdType.CHECK);
		this.recipeObject = Pattern.compile(recipeObj);
	}

	@Override
	public boolean matches(IBlockState state) {
		return recipeObject.matcher(PlaintextId.getBlockId(state)).matches();
	}
}
