package com.legobmw99.oldobsidian;

import com.google.gson.stream.JsonReader;
import com.legobmw99.oldobsidian.conversions.IConversion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static com.legobmw99.oldobsidian.matchers.BlockMatcher.blockMatchers;
import static com.legobmw99.oldobsidian.matchers.OredictMatcher.oreMatchers;
import static com.legobmw99.oldobsidian.matchers.StateMatcher.stateMatchers;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION
)
public class OldObsidian {
	public static Logger LOGGER;
	public static Collection<IConversion> EMPTY = Collections.emptyList();
	public static Collection<IConversion> miscMatchers = new ArrayList<>();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		loadConversions();
	}

	@SubscribeEvent
	public void onNotify(NeighborNotifyEvent event) {
		IBlockState state = event.getState();
		for (IConversion desc : stateMatchers.getOrDefault(state, EMPTY)){
			if(desc.checkAndPerformConversion(event)){
				return;
			}
		}
		for (IConversion desc : blockMatchers.getOrDefault(state.getBlock(), EMPTY)){
			if(desc.checkAndPerformConversion(event)){
				return;
			}
		}

		if(!oreMatchers.isEmpty()) {
			//Consider adding to stateMatchers on match/non-match
			ItemStack stack = state.getBlock().getPickBlock(state, null, null, null, null);
			if(!stack.isEmpty()) {
				int[] oreIDs = OreDictionary.getOreIDs(stack);
				for (int id : oreIDs) {
					for (IConversion desc : oreMatchers.getOrDefault(id, EMPTY)){
						if(desc.checkAndPerformConversion(event)){
							return;
						}
					}
				}
			}
		}

		for(IConversion desc : miscMatchers){
			if(desc.getLiquid1().matches(state)) {
				if(desc.checkAndPerformConversion(event)){
					return;
				}
			}
		}
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
		ConversionParser parser = new ConversionParser();
		try (Stream<Path> paths = Files.walk(Paths.get("oldobsidian"))){paths
			.map(Path::toFile)
			.filter(File::isFile)
			.forEach(file -> {
				try {
					parser.read(new JsonReader(new FileReader(file)));
				} catch (Exception e) {
					OldObsidian.LOGGER.warn("An error has occured when loading json file: {}", file, e);
				}
			});
		} catch (IOException e) {
			OldObsidian.LOGGER.error("An unexpected error has occured when loading json files", e);
		}
	}
}
