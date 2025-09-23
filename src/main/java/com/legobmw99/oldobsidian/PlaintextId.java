package com.legobmw99.oldobsidian;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * A util class for turning game objects into ID strings.
 *
 * @author Tim
 *
 */
public class PlaintextId {
	/**
	 * Gets a block ID from a block state.
	 *
	 * @param blockState
	 *            The block state
	 * @return The block ID
	 */
	public static String getBlockId(IBlockState blockState) {
		Block block = blockState.getBlock();
		return block.getRegistryName().toString() + ":" + block.getMetaFromState(blockState);
	}

	/**
	 * Gets a block state from the given block ID.
	 *
	 * @param blockId
	 *            The block ID.
	 * @return The block state.
	 */
	@SuppressWarnings("deprecation")
	public static IBlockState getBlockStateFrom(String blockId) {
		String[] splitIntoBlockId = blockId.split(":");
		if(splitIntoBlockId.length == 2) {
			return Block
				.getBlockFromName(splitIntoBlockId[0] + ":" + splitIntoBlockId[1])
				.getStateFromMeta(0);
		} else {
			return Block
				.getBlockFromName(splitIntoBlockId[0] + ":" + splitIntoBlockId[1])
				.getStateFromMeta(Integer.parseInt(splitIntoBlockId[2]));
		}
	}
}
