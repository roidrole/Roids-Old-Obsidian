package com.legobmw99.oldobsidian;

import com.google.gson.stream.JsonReader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractCollection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION
)
public class OldObsidian {
	private static Set<ConversionDescription> CONVERSIONS;
	public static Logger LOGGER;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
		MinecraftForge.EVENT_BUS.register(this);

		//TODO : only on the server, NeighborNotifyEvent isn't called on the client
		ConversionTypeAdapter adapter = new ConversionTypeAdapter();
		try {
			CONVERSIONS = Files.walk(Paths.get("oldobsidian"))
				.map(Path::toFile)
				.filter(File::isFile)
				.map(file -> {
					try {
						return adapter.read(new JsonReader(new FileReader(file)));
					} catch (Exception e) {
						OldObsidian.LOGGER.warn("An error has occured when loading json file: {}", file, e);
					}
					return Collections.EMPTY_SET;
				})
				.collect(Collector.of(
					HashSet::new,
					AbstractCollection::addAll,
					(left, right) -> {
						right.addAll(left);
						return right;
					}
				))
			;
		} catch (IOException e) {
			OldObsidian.LOGGER.error("An unexpected error has occured when loading json files", e);
		}
	}

	@SubscribeEvent
	public void onNotify(NeighborNotifyEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		IBlockState liquid1 = world.getBlockState(pos);
		CONVERSIONS.stream().filter(conversion -> {
			if(!conversion.liquid1.matches(liquid1)){
				return false;
			}
			BlockPos dustPos = pos.offset(EnumFacing.DOWN);
			if(!conversion.dust.matches(world.getBlockState(dustPos))){
				return false;
			}
			for (EnumFacing facing : EnumFacing.HORIZONTALS){
				if(conversion.liquid2.matches(world.getBlockState(dustPos.offset(facing)))){
					return true;
				}
			}
			return false;
		}).findAny().ifPresent(conversion -> {
			world.setBlockState(pos.offset(EnumFacing.DOWN), conversion.result, 2);
			world.playSound(
				null,
				pos,
				SoundEvents.BLOCK_LAVA_EXTINGUISH,
				SoundCategory.BLOCKS,
				0.5F,
				2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
			);
		});
	}

	//Remove this when not benchmarking
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof EntityPlayer) {
			onNotifyBenchmark(event.getWorld(), new BlockPos(0, 0, 0));
		}
	}

	public void onNotifyBenchmark(World world, BlockPos pos) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			IBlockState liquid1 = world.getBlockState(pos);
			CONVERSIONS.stream().filter(conversion -> {
				if (!conversion.liquid1.matches(liquid1)) {
					return false;
				}
				BlockPos dustPos = pos.offset(EnumFacing.DOWN);
				if (!conversion.dust.matches(world.getBlockState(dustPos))) {
					return false;
				}
				System.out.println(pos);
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					if (conversion.liquid2.matches(world.getBlockState(dustPos.offset(facing)))) {
						return true;
					}
				}
				return false;
			}).findAny().ifPresent(conversion -> {
				world.setBlockState(pos.offset(EnumFacing.DOWN), conversion.result, 2);
				world.playSound(
					null,
					pos,
					SoundEvents.BLOCK_LAVA_EXTINGUISH,
					SoundCategory.BLOCKS,
					0.5F,
					2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
				);
			});
		}
		LOGGER.info("10000 onNotify took {} ms", System.currentTimeMillis() - time);
	}

	/*
	start :
	10000 onNotify took 4020 ms
	10000 onNotify took 4522 ms
	10000 onNotify took 869 ms
	10000 onNotify took 631 ms


	 */
}
