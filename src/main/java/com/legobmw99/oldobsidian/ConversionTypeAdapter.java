package com.legobmw99.oldobsidian;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import com.legobmw99.oldobsidian.mcutil.PlaintextId;
import com.legobmw99.oldobsidian.parameters.CollectionParameter;
import com.legobmw99.oldobsidian.parameters.IMatchingParameter;
import com.legobmw99.oldobsidian.parameters.RegexParameter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConversionTypeAdapter extends TypeAdapter<Set<ConversionDescription>> {
	@Override
	public void write(JsonWriter out, Set<ConversionDescription> value) {
		throw new UnsupportedOperationException("Conversion can't be written");
	}

	@Override
	public Set<ConversionDescription> read(JsonReader in) throws IOException {
		Set<ConversionDescription> output;
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
				case "checkDustPower": {
					output.checkDustPower = in.nextBoolean();
					break;
				}
				case "result": {
					output.result = PlaintextId.getBlockStateFrom(in.nextString());
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

	public static IMatchingParameter readParameter(JsonReader in) throws IOException {
		switch (in.peek()){
			case BEGIN_ARRAY:{
				in.beginArray();
				Set<IMatchingParameter> dusts = new HashSet<>();
				while (in.hasNext()) {
					dusts.add(new RegexParameter(in.nextString()));
				}
				in.endArray();
				return new CollectionParameter(dusts);
			}
			case STRING:{
				String parameter = in.nextString();
				return new RegexParameter(parameter);
			}
		}
		throw new MalformedJsonException("Invalid parameter type");
	}
}
