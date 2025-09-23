package com.legobmw99.oldobsidian;

import com.google.gson.stream.JsonReader;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION
)
public class OldObsidian {
	private static Set<ConversionDescription> CONVERSIONS;
	public static Logger LOGGER;
	public static BlockPos dustPos;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
		MinecraftForge.EVENT_BUS.register(this);
		loadConversions();
	}

	@SubscribeEvent
	public void onNotify(NeighborNotifyEvent event) {
		IBlockState liquid1 = event.getState();
		if(!(liquid1.getBlock() instanceof BlockLiquid)){
			return;
		}
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		CONVERSIONS.stream().filter(conversion -> {
			if(!conversion.liquid1.matches(liquid1)){
				return false;
			}
			boolean foundDust = false;
			for(EnumFacing facing : event.getNotifiedSides()){
				dustPos = pos.offset(facing);
				if(conversion.dust.matches(world.getBlockState(dustPos))){
					foundDust = true;
					break;
				}
			}
			if(!foundDust){
				return false;
			}
			for (EnumFacing facing : EnumFacing.HORIZONTALS){
				if(conversion.liquid2.matches(world.getBlockState(dustPos.offset(facing)))){
					return true;
				}
			}
			return false;
		}).findAny().ifPresent(conversion -> {
			world.setBlockState(dustPos, conversion.result, 2);
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

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandBase() {
			@Override
			public String getName() {
				return Tags.MOD_ID + ":conversion_reload";
			}

			@Override
			public String getUsage(ICommandSender sender) {
				return "/"+getName()+" - Reloads conversions from Json";
			}

			@Override
			public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
				OldObsidian.loadConversions();
			}
		});
	}

	public static void loadConversions(){
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
}
