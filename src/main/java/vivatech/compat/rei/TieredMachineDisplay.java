package vivatech.compat.rei;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vivatech.api.recipe.ProcessingRecipe;
import vivatech.api.tier.Tier;

public abstract class TieredMachineDisplay<T extends ProcessingRecipe> implements RecipeDisplay {
	protected final T recipe;
	protected final List<List<ItemStack>> input;
	List<ItemStack> output;
	
	public TieredMachineDisplay(T recipe) {
		this.recipe = recipe;
		input = recipe.getPreviewInputs().stream()
                .map(ingredient -> Arrays.asList(ingredient.getStackArray()))
                .collect(Collectors.toList());
		output = ImmutableList.of(recipe.getOutput());
	}
	
	@Override
	public Optional<Identifier> getRecipeLocation() {
		return Optional.ofNullable(recipe).map(ProcessingRecipe::getId);
	}
	
	@Override
	public List<List<ItemStack>> getInput() {
		return input;
	}

	@Override
	public List<ItemStack> getOutput() {
		return output;
	}
	
	@Override
    public List<List<ItemStack>> getRequiredItems() {
        return input;
    }
	
	public Tier getMinTier() {
		return recipe.getMinTier();
	}
	
	public int getEnergyCost() {
		return recipe.getEnergyCost();
	}
}
