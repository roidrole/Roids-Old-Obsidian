package com.legobmw99.oldobsidian.crafttweaker;

import com.legobmw99.oldobsidian.ConversionParser;
import com.legobmw99.oldobsidian.conversions.ConversionDescription;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.oldobsidian.OldObsidian")
@ZenRegister
public class JsonedOldObsidian {
	@ZenMethod
	public static void addConversion(ICTStateMatcher fluid1, ICTStateMatcher dust, ICTStateMatcher fluid2, IBlockState output){
		ConversionParser.add(new ConversionDescription(
			fluid1.internal,
			fluid2.internal,
			dust.internal,
			CraftTweakerMC.getBlockState(output)
		));
	}
}
