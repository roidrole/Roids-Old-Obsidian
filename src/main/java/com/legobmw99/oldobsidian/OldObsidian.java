package com.legobmw99.oldobsidian;

import com.google.gson.stream.JsonReader;
import com.legobmw99.oldobsidian.conversions.IConversion;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION
)
public class OldObsidian {
	public static Logger LOGGER;

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
		ConversionsSet.INSTANCE.handle(event);
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

	@SuppressWarnings("unchecked")
	public static void loadConversions(){
		ConversionTypeAdapter adapter = new ConversionTypeAdapter();
		try (Stream<Path> paths = Files.walk(Paths.get("oldobsidian"))){paths
			.map(Path::toFile)
			.filter(File::isFile)
			.map(file -> {
				try {
					return adapter.read(new JsonReader(new FileReader(file)));
				} catch (Exception e) {
					OldObsidian.LOGGER.warn("An error has occured when loading json file: {}", file, e);
				}
				return (Set<IConversion>)Collections.EMPTY_SET;
			})
			.forEach(set -> ConversionsSet.INSTANCE.addAll(set));
		} catch (IOException e) {
			OldObsidian.LOGGER.error("An unexpected error has occured when loading json files", e);
		}
	}
}
