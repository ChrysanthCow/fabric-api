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

package net.fabricmc.fabric.impl.item;

import java.util.function.Predicate;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

public class DefaultItemComponentImpl {
	public static void modifyItemComponents(DataComponentMap originalMap, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) {
		DefaultItemComponentEvents.MODIFY.invoker().modify(new ModifyContextImpl(originalMap, builder, registries, item));
	}

	static class ModifyContextImpl implements DefaultItemComponentEvents.ModifyContext {
		private final DataComponentMap originalMap;
		private final DataComponentMap.Builder builder;
		private final HolderLookup.Provider registryLookup;
		private final Item item;

		private ModifyContextImpl(DataComponentMap originalMap, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) {
			this.originalMap = originalMap;
			this.builder = builder;
			this.registryLookup = registries;
			this.item = item;
		}

		@Override
		public void modify(Predicate<Item> itemPredicate, DefaultItemComponentEvents.ModifyConsumer builderConsumer) {
			// Bind components early to make sure that mods that components off the item still function properly.
			item.builtInRegistryHolder().bindComponents(builder.build());

			if (itemPredicate.test(item)) {
				builderConsumer.modify(builder, registryLookup, item);
			}

			// Reset the bound components to the original map to ensure no breakages when applying the final components to the client.
			item.builtInRegistryHolder().bindComponents(originalMap);
		}
	}
}
