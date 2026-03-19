/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.item;

import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.fabricmc.fabric.impl.item.DefaultItemComponentImpl;

@Mixin(DataComponentInitializers.class)
public abstract class DataComponentInitializersMixin {
	@ModifyReturnValue(method = "runInitializers", at = @At(value = "RETURN"))
	private static Map<ResourceKey<?>, DataComponentMap.Builder> cowponents$runDefaultEntityComponents(Map<ResourceKey<?>, DataComponentMap.Builder> original, @Local(argsOnly = true) HolderLookup.Provider lookupProvider) {
		for (Item item : BuiltInRegistries.ITEM) {
			ResourceKey<Item> key = item.builtInRegistryHolder().key();
			DataComponentMap originalMap = item.builtInRegistryHolder().areComponentsBound()
					? item.builtInRegistryHolder().components() : null;
			DataComponentMap.Builder builder = original.computeIfAbsent(key, resourceKey -> DataComponentMap.builder());
			DefaultItemComponentImpl.modifyItemComponents(originalMap, builder, lookupProvider, item);
		}

		return original;
	}
}
