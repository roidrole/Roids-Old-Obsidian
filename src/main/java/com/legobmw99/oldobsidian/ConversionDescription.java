package com.legobmw99.oldobsidian;

import com.legobmw99.oldobsidian.parameters.IMatchingParameter;
import net.minecraft.block.state.IBlockState;

public class ConversionDescription {
	public IMatchingParameter liquid1;
	public IMatchingParameter liquid2;
	public IMatchingParameter dust;
	public boolean checkDustPower = false;
	public IBlockState result;
}
