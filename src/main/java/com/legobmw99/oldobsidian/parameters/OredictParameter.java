package com.legobmw99.oldobsidian.parameters;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

public class OredictParameter implements IMatchingParameter{
	int entryId;

	public OredictParameter(String entry){
		entryId = OreDictionary.getOreID(entry);
	}

	@Override
	public boolean matches(IBlockState state) {
		return ArrayUtils.contains(OreDictionary.getOreIDs(Item.getItemFromBlock(state.getBlock()).getDefaultInstance()), entryId);
	}
}
