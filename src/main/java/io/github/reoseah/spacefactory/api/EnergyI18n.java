package io.github.reoseah.spacefactory.api;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class EnergyI18n {
    public static final Text ENERGY = Text.translatable("spacefactory.energy");
    private static final String ENERGY_AMOUNT_KEY = "spacefactory.energy.amount";
    private static final String ENERGY_AMOUNT_AND_CAPACITY_KEY = "spacefactory.energy.amount_and_capacity";
    private static final String ENERGY_AMOUNT_PER_TICK_KEY = "spacefactory.energy.amount_per_tick";
    private static final String ENERGY_INPUT_AMOUNT_PER_TICK_KEY = "spacefactory.energy.average_input_per_tick";
    private static final String ENERGY_AMOUNT_PER_USE_KEY = "spacefactory.energy.amount_per_use";
    private static final String AMOUNT_AND_AMOUNT_PER_TICK_KEY = "spacefactory.energy.amount_and_amount_per_tick";

    private static final DecimalFormat LARGE_AMOUNTS_FORMAT;

    static {
        LARGE_AMOUNTS_FORMAT = (DecimalFormat) DecimalFormat.getInstance(Locale.ROOT);
        LARGE_AMOUNTS_FORMAT.setGroupingUsed(true);
        LARGE_AMOUNTS_FORMAT.setGroupingSize(3);
        DecimalFormatSymbols symbols = LARGE_AMOUNTS_FORMAT.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        LARGE_AMOUNTS_FORMAT.setDecimalFormatSymbols(symbols);
    }

    private static String formatEnergy(long amount) {
        return amount < 10000 ? String.valueOf(amount) : LARGE_AMOUNTS_FORMAT.format(amount);
    }

    public static MutableText energy(long amount) {
        return Text.translatable(ENERGY_AMOUNT_KEY, formatEnergy(amount));
    }

    public static MutableText energyAndCapacity(long amount, long capacity) {
        return Text.translatable(ENERGY_AMOUNT_AND_CAPACITY_KEY, formatEnergy(amount), formatEnergy(capacity));
    }

    public static MutableText energyPerTick(long amount) {
        return Text.translatable(ENERGY_AMOUNT_PER_TICK_KEY, formatEnergy(amount));
    }

    public static MutableText averageInputPerTick(float amount) {
        return Text.translatable(ENERGY_INPUT_AMOUNT_PER_TICK_KEY, amount);
    }

    public static MutableText energyPerUse(long amount) {
        return Text.translatable(ENERGY_AMOUNT_PER_USE_KEY, formatEnergy(amount));
    }

    public static MutableText amountAndAmountPerTick(long amount, long change) {
        return Text.translatable(AMOUNT_AND_AMOUNT_PER_TICK_KEY, formatEnergy(amount), formatEnergy(change));
    }
}
