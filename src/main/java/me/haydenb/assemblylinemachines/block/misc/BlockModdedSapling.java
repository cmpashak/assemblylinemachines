package me.haydenb.assemblylinemachines.block.misc;

import java.util.function.Function;

import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.util.Lazy;

public class BlockModdedSapling extends SaplingBlock {

	private final PlantType plantType;

	public BlockModdedSapling(ResourceLocation featureLocation, PlantType plantType) {
		super(new AbstractTreeGrower() {

			private final Lazy<Function<ServerLevel, Holder<ConfiguredFeature<?, ?>>>> featureAccess = Lazy.of(() -> (sl) -> {
				return sl.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).getHolderOrThrow(ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, featureLocation));
			});

			@Override
			protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_204307_, boolean p_204308_) {
				return null;
			}

			@Override
			public boolean growTree(ServerLevel pLevel, ChunkGenerator pChunkGenerator, BlockPos pPos,
					BlockState pState, RandomSource pRandom) {
				var feature = featureAccess.get().apply(pLevel).value();
				pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 4);
				if(feature.place(pLevel, pChunkGenerator, pRandom, pPos)) {
					return true;
				}else {
					pLevel.setBlock(pPos, pState, 4);
					return false;
				}
			}

		}, Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
		this.plantType = plantType;
	}

	@Override
	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return plantType;
	}
}
