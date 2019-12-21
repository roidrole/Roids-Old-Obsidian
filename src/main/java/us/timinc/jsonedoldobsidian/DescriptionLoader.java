package us.timinc.jsonedoldobsidian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import us.timinc.jsonedoldobsidian.description.ConversionDescription;

public class DescriptionLoader {
	private List<ConversionDescription> gameObjects = new ArrayList<>();
	private Gson gson;

	public DescriptionLoader() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.create();
		try {
			loadGameObjects();
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
	}

	private void loadGameObjects() throws FileSystemException {
		File globalDir = new File("oldobsidian");
		if (!globalDir.exists() && !globalDir.mkdirs()) {
			throw new FileSystemException("Unable to create gameobjects directory.");
		}
		searchDirectory(globalDir);
	}

	private void searchDirectory(File dir) {
		File[] dirFiles = dir.listFiles();

		if (dirFiles == null) {
			return;
		}

		Arrays.stream(dirFiles).filter(File::isDirectory).forEach(this::searchDirectory);
		Arrays.stream(dirFiles).filter(x -> x.getName().endsWith(".json")).forEach(this::addObjectsFrom);
	}

	private void addObjectsFrom(File file) {
		try {
			gameObjects.addAll(Arrays.asList(gson.fromJson(new FileReader(file), ConversionDescription[].class)));
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<ConversionDescription> getConversions() {
		return gameObjects.stream().filter(ConversionDescription.class::isInstance)
				.map(e -> (ConversionDescription) e).collect(Collectors.toList());
	}
}
