package com.legobmw99.oldobsidian.matchers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

public class OredictMatcher implements IBlockStateMatcher {
	int entryId;

	public OredictMatcher(String entry){
		entryId = OreDictionary.getOreID(entry);
	}

	@Override
	public boolean matches(IBlockState state) {
		try {
			ItemStack stack = state.getBlock().getPickBlock(state, null, null, null, null);
			int[] oreIDs = OreDictionary.getOreIDs(stack);
			return ArrayUtils.contains(oreIDs, entryId);
		} catch (Exception e) {
			return false;
		}
	}
}
