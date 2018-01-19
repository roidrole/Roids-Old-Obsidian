package com.legobmw99.oldobsidian;


import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = OldObsidian.MODID, version = OldObsidian.VERSION)
public class OldObsidian {
	public static final String MODID = "oldobsidian";
	public static final String VERSION = "1.1";
	public static Block blockWetstone;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onNotify(NeighborNotifyEvent e) {
		World world = e.getWorld();
		if(world.getBlockState(e.getPos()).getMaterial() == Material.LAVA) { //Make sure block updating is lava
			for(EnumFacing side1: EnumFacing.VALUES) { //Iterate through relevant sides
				IBlockState state = world.getBlockState(e.getPos().offset(side1));
				if (state.getBlock() == Blocks.REDSTONE_WIRE 
					&& state.getValue(BlockRedstoneWire.POWER) == 0) { //See if they are unpowered redstone
					for(EnumFacing side2: EnumFacing.HORIZONTALS) { //Iterate through the horizontal sides of the redstone
						//Check if water is present
						if(world.getBlockState(e.getPos().offset(side1).offset(side2)).getMaterial() == Material.WATER) {
							//Set the block to obsidian and play an effect
							world.setBlockState(e.getPos().offset(side1), Blocks.OBSIDIAN.getDefaultState(), 2);
							world.playSound((EntityPlayer) null, e.getPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
									2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
							//No need to keep checking this block
							break;
						}
					}
				}
			}
		}
	}
}
