package com.legobmw99.oldobsidian;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import us.timinc.jsonedoldobsidian.DescriptionLoader;
import us.timinc.jsonedoldobsidian.ModInfo;
import us.timinc.jsonedoldobsidian.description.ConversionDescription;
import us.timinc.mcutil.PlaintextId;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION)
public class OldObsidian {
	private static DescriptionLoader CONVERSIONS;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CONVERSIONS = new DescriptionLoader();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onNotify(NeighborNotifyEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		for (ConversionDescription conversion : CONVERSIONS.getConversions()) {
			if (conversion.matchesLiquid1(world.getBlockState(pos))) {
				// Make sure block updating is liquid1
				for (EnumFacing side1 : EnumFacing.VALUES) {
					// Iterate through relevant sides
					if (conversion.matchesDust(world.getBlockState(pos.offset(side1)))) {
						for (EnumFacing side2 : EnumFacing.HORIZONTALS) {
							// Iterate through the horizontal sides of the
							// dust
							// Check if liquid2 is present
							if (conversion
									.matchesLiquid2(world.getBlockState(e.getPos().offset(side1).offset(side2)))) {
								// Set the block to result and play an effect
								world.setBlockState(e.getPos().offset(side1),
										PlaintextId.getBlockStateFrom(conversion.result), 2);
								world.playSound((EntityPlayer) null, e.getPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH,
										SoundCategory.BLOCKS, 0.5F,
										2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
								// No need to keep checking this block
								break;
							}
						}
					}
				}
			}
		}
	}
}
