package io.github.reoseah.spacefactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Getter
@SuppressWarnings("FieldMayBeFinal")
public class SpaceFactoryConfig {
    @SerializedName("assembler.energy_capacity")
    private int assemblerEnergyCapacity = 100_000;
    @SerializedName("assembler.energy_consumption")
    private int assemblerEnergyConsumption = 100;

    @SerializedName("extractor.energy_capacity")
    private int extractorEnergyCapacity = 100_000;
    @SerializedName("extractor.energy_consumption")
    private int extractorEnergyConsumption = 150;

    @SerializedName("bedrock_miner.energy_capacity")
    private int bedrockMinerEnergyCapacity = 100_000;
    @SerializedName("bedrock_miner.energy_consumption")
    private int bedrockMinerEnergyConsumption = 100_000;
    @SerializedName("bedrock_miner.drill_supplies_duration")
    private int bedrockMinerDrillSuppliesDuration = 120 * 20;
    @SerializedName("bedrock_miner.drilling_duration")
    private int bedrockMinerDrillingDuration = 10 * 20;

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
