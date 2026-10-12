package com.legobmw99.oldobsidian.crafttweaker;

import com.legobmw99.oldobsidian.OldObsidian;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.api.reload.ScriptReloadEvent;

public class ScriptReloadListener {

	@SubscribeEvent
	public static void onRecipeReload(ScriptReloadEvent.Pre event){
		OldObsidian.loadConversions();
	}
}
