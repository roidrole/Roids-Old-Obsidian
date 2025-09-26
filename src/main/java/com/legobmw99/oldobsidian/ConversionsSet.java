package com.legobmw99.oldobsidian;

import com.legobmw99.oldobsidian.matchers.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

//A weird mix between a Set<Conversion> and a Map<Object, Set<ConversionDescription>>
//Is used to have hashing for ConversionDescription liquid1 matching
//Also hosts event handling because it's easier
public class ConversionsSet {
	public static ConversionsSet INSTANCE = new ConversionsSet();
	public Map<IBlockState, Set<ConversionDescription>> stateMatchers = new HashMap<>();
	public Map<Block, Set<ConversionDescription>> blockMatchers = new HashMap<>();
	public Map<Integer, Set<ConversionDescription>> oreMatchers = new Int2ObjectOpenHashMap<>();
	public Set<ConversionDescription> miscMatchers = new HashSet<>();
	@SuppressWarnings("unchecked")
	public static Set<ConversionDescription> EMPTY_SET = (Set<ConversionDescription>)Collections.EMPTY_SET;


	public void handle(BlockEvent.NeighborNotifyEvent event){
		IBlockState state = event.getState();
		for (ConversionDescription desc : stateMatchers.getOrDefault(state, EMPTY_SET)){
			if(desc.checkAndPerformConversion(event)){
				return;
			}
		}
		for (ConversionDescription desc : blockMatchers.getOrDefault(state.getBlock(), EMPTY_SET)){
			if(desc.checkAndPerformConversion(event)){
				return;
			}
		}

		if(!oreMatchers.isEmpty()) {
			ItemStack stack = state.getBlock().getPickBlock(state, null, null, null, null);
			if(!stack.isEmpty()) {
				int[] oreIDs = OreDictionary.getOreIDs(stack);
				for (int id : oreIDs) {
					for (ConversionDescription desc : oreMatchers.getOrDefault(id, EMPTY_SET)){
						if(desc.checkAndPerformConversion(event)){
							return;
						}
					}
				}
			}
		}

		for(ConversionDescription desc : miscMatchers){
			if(desc.liquid1.matches(state)) {
				if(desc.checkAndPerformConversion(event)){
					return;
				}
			}
		}
	}

	public void add(ConversionDescription description){
		if(description.liquid1 instanceof CollectionMatcher){
			for (IBlockStateMatcher matcher: ((CollectionMatcher) description.liquid1).internal){
				put(matcher, description);
			}
		} else {
			put(description.liquid1, description);
		}
	}

	//FindMap, the return? It's just hard getting the key of an unknown type
	//Could also replace that with a static in the class itself, maybe. Put that computeIfAbsent in SimpleMatcher, even?
	public void put(IBlockStateMatcher matcher, ConversionDescription description){
		if(matcher instanceof SimpleMatcher){
			if(matcher instanceof StateMatcher){
				stateMatchers.computeIfAbsent(((StateMatcher) matcher).state, key -> new HashSet<>()).add(description);
			} else if(matcher instanceof BlockMatcher){
				blockMatchers.computeIfAbsent(((BlockMatcher) matcher).block, key -> new HashSet<>()).add(description);
			} else if(matcher instanceof OredictMatcher) {
				oreMatchers.computeIfAbsent(((OredictMatcher) matcher).entryId, key -> new HashSet<>()).add(description);
			}
		} else {
			miscMatchers.add(description);
		}
	}

	public void addAll(Set<ConversionDescription> descriptionSet){
		for(ConversionDescription description : descriptionSet){
			this.add(description);
		}
	}
}
