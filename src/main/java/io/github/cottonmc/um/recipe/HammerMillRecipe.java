package io.github.cottonmc.um.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.cottonmc.um.block.UMBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class HammerMillRecipe extends SimpleProcessingRecipe {
	public static final Serializer SERIALIZER = new Serializer();

	protected final ItemStack extraResult;
	protected final float extraChance;
	protected int duration;
	
	public HammerMillRecipe(Identifier id, Ingredient ingredient, ItemStack result, int energy, int duration) {
		this(id, ingredient, result, ItemStack.EMPTY, 0.0f, energy, duration);
	}
	
	public HammerMillRecipe(Identifier id, Ingredient ingredient, ItemStack result, ItemStack extraResult, float extraChance, int energy, int duration) {
		super(id, ingredient, result, energy, duration);
		this.extraResult = extraResult;
		this.extraChance = extraChance;
	}

	public ItemStack getExtraOutput() {
		return extraResult;
	}

	public float getExtraOutputChance() {
		return extraChance;
	}

	public RecipeSerializer<SimpleProcessingRecipe> getSerializer() {
			return SERIALIZER;
		}
	
	public static class Serializer extends SimpleProcessingRecipe.Serializer {
		@Override
		public HammerMillRecipe read(Identifier id, JsonObject json) {
			JsonElement ingredientElem = json.get("ingredient");
			if (ingredientElem==null) throw new JsonSyntaxException("Recipe must have an ingredient.");
			Ingredient input = UMRecipes.getIngredient(ingredientElem, "ingredient");
			
			JsonElement resultElem = json.get("result");
			if (resultElem==null) throw new JsonSyntaxException("Recipe must have a result.");
			ItemStack result = UMRecipes.getItemStack(resultElem, "result");
			
			ItemStack extraResult = ItemStack.EMPTY;
			if (json.has("extra_chance")) extraResult = UMRecipes.getItemStack(json.get("extra_chance"), "extra_chance");
			float randomChance = JsonHelper.getFloat(json, "random_chance", 0.0f);
			
			int energy = JsonHelper.getInt(json, "energy", 0);
			int processTime = JsonHelper.getInt(json, "duration");
			
			System.out.println("Recipe '"+id+"' read successfully.");
			
			return new HammerMillRecipe(id, input, result, extraResult, randomChance, energy, processTime);
		}

		@Override
		public HammerMillRecipe read(Identifier id, PacketByteBuf buffer) {
			Ingredient ingredient = Ingredient.fromPacket(buffer);
			ItemStack result = buffer.readItemStack();
			ItemStack extraResult = buffer.readItemStack();
			float randomChance = buffer.readFloat();
			int energy = buffer.readInt();
			int processTime = buffer.readInt();
			
			return new HammerMillRecipe(id, ingredient, result, extraResult, randomChance, energy, processTime);
		}

		@Override
		public void write(PacketByteBuf buffer, SimpleProcessingRecipe base) {
			HammerMillRecipe recipe = (HammerMillRecipe) base;
			recipe.ingredient.write(buffer);
			buffer.writeItemStack(recipe.result);
			buffer.writeItemStack(recipe.extraResult);
			buffer.writeFloat(recipe.extraChance);
			buffer.writeInt(recipe.energy);
			buffer.writeInt(recipe.duration);
		}
	}
}
