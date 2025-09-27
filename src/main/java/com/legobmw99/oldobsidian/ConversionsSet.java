package com.legobmw99.oldobsidian;

import com.legobmw99.oldobsidian.conversions.IConversion;
import com.legobmw99.oldobsidian.matchers.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

//A weird mix between a Set<Conversion> and a Map<Object, Set<IConversion>>
//Is used to have hashing for IConversion liquid1 matching
//Also hosts event handling because it's easier
public class ConversionsSet {
	public static ConversionsSet INSTANCE = new ConversionsSet();
	public Map<IBlockState, Set<IConversion>> stateMatchers = new HashMap<>();
	public Map<Block, Set<IConversion>> blockMatchers = new HashMap<>();
	public Map<Integer, Set<IConversion>> oreMatchers = new Int2ObjectOpenHashMap<>();
	public Set<IConversion> miscMatchers = new HashSet<>();
	@SuppressWarnings("unchecked")
	public static Set<IConversion> EMPTY_SET = (Set<IConversion>)Collections.EMPTY_SET;


	public void handle(BlockEvent.NeighborNotifyEvent event){
		IBlockState state = event.getState();
		for (IConversion desc : stateMatchers.getOrDefault(state, EMPTY_SET)){
			if(desc.checkAndPerformConversion(event)){
				return;
			}
		}
		for (IConversion desc : blockMatchers.getOrDefault(state.getBlock(), EMPTY_SET)){
			if(desc.checkAndPerformConversion(event)){
				return;
			}
		}

		if(!oreMatchers.isEmpty()) {
			ItemStack stack = state.getBlock().getPickBlock(state, null, null, null, null);
			if(!stack.isEmpty()) {
				int[] oreIDs = OreDictionary.getOreIDs(stack);
				for (int id : oreIDs) {
					for (IConversion desc : oreMatchers.getOrDefault(id, EMPTY_SET)){
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

	public void add(IConversion conversion){
		if(!conversion.validate()){
			OldObsidian.LOGGER.warn("Trying to register an invalid conversion: {}", conversion.toString());
			return;
		}
		if(conversion.getLiquid1() instanceof CollectionMatcher){
			for (IBlockStateMatcher matcher: ((CollectionMatcher) conversion.getLiquid1()).internal){
				put(matcher, conversion);
			}
		} else {
			put(conversion.getLiquid1(), conversion);
		}
	}

	//FindMap, the return? It's just hard getting the key of an unknown type
	//Could also replace that with a static in the class itself, maybe. Put that computeIfAbsent in SimpleMatcher, even?
	public void put(IBlockStateMatcher matcher, IConversion conversion){
		if(matcher instanceof SimpleMatcher){
			if(matcher instanceof StateMatcher){
				stateMatchers.computeIfAbsent(((StateMatcher) matcher).state, key -> new HashSet<>()).add(conversion);
			} else if(matcher instanceof BlockMatcher){
				blockMatchers.computeIfAbsent(((BlockMatcher) matcher).block, key -> new HashSet<>()).add(conversion);
			} else if(matcher instanceof OredictMatcher) {
				oreMatchers.computeIfAbsent(((OredictMatcher) matcher).entryId, key -> new HashSet<>()).add(conversion);
			}
		} else {
			miscMatchers.add(conversion);
		}
	}

	public void addAll(Collection<IConversion> descriptionSet){
		for(IConversion description : descriptionSet){
			this.add(description);
		}
	}
}
