package io.github.reoseah.spacefactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SpaceFactoryConfig {
    @Getter
    @SerializedName("assembler.energy_capacity")
    private int assemblerEnergyCapacity = 100_000;
    @Getter
    @SerializedName("assembler.energy_consumption")
    private int assemblerEnergyConsumption = 100;

    @Getter
    @SerializedName("extractor.energy_capacity")
    private int extractorEnergyCapacity = 100_000;
    @Getter
    @SerializedName("extractor.energy_consumption")
    private int extractorEnergyConsumption = 150;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static SpaceFactoryConfig loadOrCreate(Path path) throws Exception {
        if (!Files.exists(path)) {
            SpaceFactoryConfig instance = new SpaceFactoryConfig();
            String json = GSON.toJson(instance);
            Files.writeString(path, json, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
            return instance;
        }

        return GSON.fromJson(Files.newBufferedReader(path), SpaceFactoryConfig.class);
    }
}
