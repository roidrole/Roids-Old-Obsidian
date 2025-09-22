package com.legobmw99.oldobsidian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legobmw99.oldobsidian.description.IConversion;
import net.minecraft.block.state.IBlockState;
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
import org.apache.logging.log4j.Logger;
import com.legobmw99.oldobsidian.description.ConversionDescription;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION
)
public class OldObsidian {
	private static Set<IConversion> CONVERSIONS;
	public static Logger LOGGER;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
		MinecraftForge.EVENT_BUS.register(this);

		Gson gson = new GsonBuilder().create();
		try {
			CONVERSIONS = Files.walk(Paths.get("oldobsidian"))
				.map(Path::toFile)
				.filter(File::isFile)
				.flatMap(file -> {
					try {
						//Make logWarn
						return Arrays.stream(gson.fromJson(new FileReader(file), ConversionDescription[].class));
					} catch (Exception e) {
						OldObsidian.LOGGER.error("An error has occured when loading json file: {}", file, e);
					}
					return java.util.stream.Stream.empty();
				})
				.collect(Collectors.toSet())
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
			if(!conversion.matchesLiquid1(liquid1)){
				return false;
			}
			BlockPos dustPos = pos.offset(EnumFacing.DOWN);
			if(!conversion.matchesDust(world.getBlockState(dustPos))){
				return false;
			}
			System.out.println(pos);
			for (EnumFacing facing : EnumFacing.HORIZONTALS){
				if(conversion.matchesLiquid2(world.getBlockState(dustPos.offset(facing)))){
					return true;
				}
			}
			return false;
		}).findAny().ifPresent(conversion -> {
			world.setBlockState(pos.offset(EnumFacing.DOWN), conversion.getResult(), 2);
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
}
