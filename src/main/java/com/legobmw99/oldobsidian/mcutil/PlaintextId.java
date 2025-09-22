package com.legobmw99.oldobsidian.mcutil;

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
		blockId = fixMetadata(blockId, IdType.OBJ);
		String[] splitIntoBlockId = blockId.split(":");
		return Block.getBlockFromName(splitIntoBlockId[0] + ":" + splitIntoBlockId[1])
				.getStateFromMeta(Integer.parseInt(splitIntoBlockId[2]));
	}

	public enum IdType {
		CHECK, OBJ
	}

	public static String fixMetadata(String itemId, IdType idType) {
		String[] splitMetadata = itemId.split(":");
		if (splitMetadata.length == 2) {
			switch (idType) {
			case CHECK:
				return itemId + ":*";
			case OBJ:
				return itemId + ":0";
			default:
				return itemId;
			}
		}
		return itemId;
	}
}
