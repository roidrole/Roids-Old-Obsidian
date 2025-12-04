package com.legobmw99.oldobsidian;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import com.legobmw99.oldobsidian.conversions.ConversionDescription;
import com.legobmw99.oldobsidian.conversions.IConversion;
import com.legobmw99.oldobsidian.matchers.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ConversionParser {
	public static Collection<IConversion> miscMatchers = new ArrayList<>();

	public void read(JsonReader in) throws IOException {
		switch(in.peek()){
			case BEGIN_ARRAY: {
				in.beginArray();
				while(in.hasNext()){
					add(readObject(in));
				}
				break;
			}
			case BEGIN_OBJECT: {
				add(readObject(in));
				break;
			}
			default: throw new MalformedJsonException("Conversion Json files must be an object or an array of objects");
		}
	}

	public static ConversionDescription readObject(JsonReader in) throws IOException {
		in.beginObject();
		ConversionDescription output = new ConversionDescription();
		while(in.hasNext()) {
			switch (in.nextName()) {
				case "liquid1":
				case "liquids1": {
					output.liquid1 = readParameter(in);
					break;
				}
				case "liquid2":
				case "liquids2": {
					output.liquid2 = readParameter(in);
					break;
				}
				case "dust":
				case "dusts": {
					output.dust = readParameter(in);
					break;
				}
				case "result": {
					output.result = PlaintextId.getBlockStateFrom(in.nextString());
					break;
				}
				case "checkDustPower":{
					in.nextBoolean();
					break;
				}
				default: throw new MalformedJsonException("Unrecognized JSON key. Accepted keys are : liquid1, liquids1, liquid2, liquids2, dust, dusts, checkDustPower, result");
			}
		}
		return output;
	}

	public static IBlockStateMatcher readParameter(JsonReader in) throws IOException {
		switch (in.peek()){
			case BEGIN_ARRAY:{
				in.beginArray();
				Collection<IBlockStateMatcher> output = new ArrayList<>();
				while (in.hasNext()) {
					output.add(readParameter(in));
				}
				in.endArray();
				return new CollectionMatcher(output);
			}
			case STRING:{
				String parameter = in.nextString();
				if(parameter.startsWith("ore:")){
					return new OredictMatcher(parameter.substring(4));
				}
				String[] splitParameter = parameter.split(":");
				if(splitParameter.length > 1) {
					ResourceLocation resLoc = new ResourceLocation(splitParameter[0], splitParameter[1]);
					if (ForgeRegistries.BLOCKS.containsKey(resLoc)) {
						if(splitParameter.length == 2) {
							return new BlockMatcher(ForgeRegistries.BLOCKS.getValue(resLoc));
						} else if (splitParameter.length == 3){
							return new StateMatcher(ForgeRegistries.BLOCKS.getValue(resLoc).getStateFromMeta(Integer.parseInt(splitParameter[2])));
						}
					}
					//If no special character apart from : and _, log a warning
					if(parameter.chars().allMatch(i ->
						(i == 58 || i == 95) //The ':' and '_' char
					 || (i > 64 && i < 91)  // lowercase ASCII
					 || (i > 96 && i < 123)) // uppercase ASCII
					){
						OldObsidian.LOGGER.warn("{} is not a valid block, using RegEx matching. From file: {}", parameter, in.toString());
					}
					return new RegexMatcher(parameter);
				}
			}
			case BOOLEAN:{
				if(in.nextBoolean()){
					return TrueMatcher.INSTANCE;
				}
				throw new MalformedJsonException("false is not a valid parameter type");
			}
		}
		throw new MalformedJsonException("Invalid parameter type");
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

	public void put(IBlockStateMatcher matcher, IConversion conversion){
		if(matcher instanceof SimpleMatcher){
			((SimpleMatcher<?>) matcher).addToMap(conversion);
		} else {
			miscMatchers.add(conversion);
		}
	}
}
