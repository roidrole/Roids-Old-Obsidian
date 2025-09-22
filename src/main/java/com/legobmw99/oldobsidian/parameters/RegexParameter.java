package com.legobmw99.oldobsidian.parameters;

import com.legobmw99.oldobsidian.mcutil.PlaintextId;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.oredict.OreDictionary;

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
		String gameObject = PlaintextId.getBlockId(state);

		if (recipeObject.pattern().startsWith("ore:")) {
			int[] x = OreDictionary.getOreIDs(PlaintextId.createItemStackFrom(gameObject, 1));
			for (int i : x) {
				if (recipeObject.matcher("ore:" + OreDictionary.getOreName(i) + ":0").matches()){
					return true;
				}
			}
			return false;
		}
		return recipeObject.matcher(gameObject).matches();
	}
}
