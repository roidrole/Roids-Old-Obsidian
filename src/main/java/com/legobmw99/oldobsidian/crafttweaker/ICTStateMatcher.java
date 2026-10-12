package com.legobmw99.oldobsidian.crafttweaker;

import com.legobmw99.oldobsidian.matchers.*;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

@ZenClass("mods.oldobsidian.StateMatcher")
@ZenRegister
public class ICTStateMatcher {
	public final IBlockStateMatcher internal;

	public ICTStateMatcher(IBlockStateMatcher internal) {
		this.internal = internal;
	}

	@ZenMethod
	public static ICTStateMatcher from(IBlockDefinition block){
		return new ICTStateMatcher(new BlockMatcher(CraftTweakerMC.getBlock(block)));
	}

	@ZenMethod
	public static ICTStateMatcher from(ICTStateMatcher[] matchers){
		Collection<IBlockStateMatcher> matchersNative = new ArrayList<>(matchers.length);
		for (ICTStateMatcher matcher : matchers) {
			matchersNative.add(matcher.internal);
		}
		return new ICTStateMatcher(new CollectionMatcher(matchersNative));
	}

	@ZenMethod
	public static ICTStateMatcher from(IOreDictEntry entry){
		return new ICTStateMatcher(new OredictMatcher(entry.getName()));
	}

	@ZenMethod
	public static ICTStateMatcher from(String regex){
		return new ICTStateMatcher(new RegexMatcher(regex));
	}

	@ZenMethod
	public static ICTStateMatcher from(IBlockState state){
		return new ICTStateMatcher(new StateMatcher(CraftTweakerMC.getBlockState(state)));
	}

	@ZenMethod
	public static ICTStateMatcher from(Predicate<IBlockState> predicate){
		return new ICTStateMatcher(state -> predicate.test(CraftTweakerMC.getBlockState(state)));
	}

	@ZenMethod
	public static ICTStateMatcher getTrueMatcher(){
		return new ICTStateMatcher(TrueMatcher.INSTANCE);
	}

	@ZenMethod
	public boolean matches(IBlockState state){
		return internal.matches(CraftTweakerMC.getBlockState(state));
	}
}
