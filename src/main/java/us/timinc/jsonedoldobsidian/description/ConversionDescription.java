package us.timinc.jsonedoldobsidian.description;

import java.util.Arrays;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import us.timinc.mcutil.PlaintextId;

public class ConversionDescription {
	public String liquid1 = "";
	public String[] liquids1 = {};
	public String liquid2 = "";
	public String[] liquids2 = {};
	public String dust = "";
	public String[] dusts = {};
	public boolean checkDustPower = false;
	public String result = "";

	public boolean matchesLiquid1(IBlockState state) {
		return matchesArrayOrString(state, liquids1, liquid1);
	}

	public boolean matchesLiquid2(IBlockState state) {
		return matchesArrayOrString(state, liquids2, liquid2);
	}

	public boolean matchesDust(IBlockState state) {
		return matchesArrayOrString(state, dusts, dust)
				&& (checkDustPower && state.getValue(BlockRedstoneWire.POWER) == 0);
	}

	private boolean matchesArrayOrString(IBlockState state, String[] pool, String entry) {
		if (entry.isEmpty()) {
			return Arrays.stream(pool).anyMatch(e -> {
				return PlaintextId.matches(e, PlaintextId.getBlockId(state));
			});
		} else {
			return PlaintextId.matches(entry, PlaintextId.getBlockId(state));
		}
	}
}
