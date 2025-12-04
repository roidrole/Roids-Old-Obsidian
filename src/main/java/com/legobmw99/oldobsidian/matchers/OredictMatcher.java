package com.legobmw99.oldobsidian.matchers;

import com.legobmw99.oldobsidian.conversions.IConversion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OredictMatcher extends SimpleMatcher<Integer> {
	public int entryId;
	public static Map<Integer, Collection<IConversion>> oreMatchers = new HashMap<>();

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

	@Override
	public Integer getInternal() {
		return entryId;
	}

	@Override
	public Map<Integer, Collection<IConversion>> getMap() {
		return oreMatchers;
	}
}
