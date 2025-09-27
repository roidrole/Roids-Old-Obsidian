package com.legobmw99.oldobsidian;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import com.legobmw99.oldobsidian.conversions.ConversionDescription;
import com.legobmw99.oldobsidian.conversions.IConversion;
import com.legobmw99.oldobsidian.matchers.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConversionTypeAdapter extends TypeAdapter<Set<IConversion>> {
	@Override
	public void write(JsonWriter out, Set<IConversion> value) {
		throw new UnsupportedOperationException("Conversion can't be written");
	}

	@Override
	public Set<IConversion> read(JsonReader in) throws IOException {
		Set<IConversion> output;
		switch(in.peek()){
			case BEGIN_ARRAY: {
				in.beginArray();
				output = new HashSet<>();
				while(in.hasNext()){
					output.add(readObject(in));
				}
				break;
			}
			case BEGIN_OBJECT: {
				output = Collections.singleton(readObject(in));
				break;
			}
			default: throw new MalformedJsonException("Conversion Json files must be an object or an array of objects");
		}
		return output;
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
		if(output.result == null || output.liquid1 == null || output.liquid2 == null || output.dust == null){
			throw new MalformedJsonException("Every entry of a conversion must contain the following keys : result, liquid1, liquid2, dust");
		}
		return output;
	}

	public static IBlockStateMatcher readParameter(JsonReader in) throws IOException {
		switch (in.peek()){
			case BEGIN_ARRAY:{
				in.beginArray();
				Set<IBlockStateMatcher> output = new HashSet<>();
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
}
