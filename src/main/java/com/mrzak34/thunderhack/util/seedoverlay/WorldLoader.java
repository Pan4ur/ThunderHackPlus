package com.mrzak34.thunderhack.util.seedoverlay;

import com.mrzak34.thunderhack.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Random;

public class WorldLoader {

    public static IChunkGenerator ChunkGenerator;
    public static long seed = 44776655;
    public static boolean GenerateStructures = true;
    public static AwesomeWorld fakeworld;

    public static Random rand;

    public static void setup() {
        WorldSettings worldSettings = new WorldSettings(seed, GameType.SURVIVAL, GenerateStructures, false, WorldType.DEFAULT);
        WorldInfo worldInfo = new WorldInfo(worldSettings, "FakeWorld");
        worldInfo.setMapFeaturesEnabled(true);
        fakeworld = new AwesomeWorld(worldInfo);
        if (Util.mc.player.dimension == -1)
            ChunkGenerator = new ChunkGeneratorHell(fakeworld, fakeworld.getWorldInfo().isMapFeaturesEnabled(), seed);
        else
            ChunkGenerator = fakeworld.provider.createChunkGenerator();

    }

    public static Chunk CreateChunk(int x, int z, int dis) {


        Chunk Testchunk;
        if (dis == -1) {
            if (!(ChunkGenerator instanceof ChunkGeneratorHell))
                ChunkGenerator = new ChunkGeneratorHell(fakeworld, fakeworld.getWorldInfo().isMapFeaturesEnabled(), seed);
        }

        if (!fakeworld.isChunkGeneratedAt(x, z))
            Testchunk = ChunkGenerator.generateChunk(x, z);
        else Testchunk = fakeworld.getChunk(x, z);

        fakeworld.getChunkProvider().loadedChunks.put(ChunkPos.asLong(x, z), Testchunk);
        Testchunk.onLoad();
        populate(fakeworld.getChunkProvider(), ChunkGenerator, x, z);

        return Testchunk;
    }

    public static void populate(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, int x, int z) {
        Chunk chunk = chunkProvider.getLoadedChunk(x, z - 1);
        Chunk chunk1 = chunkProvider.getLoadedChunk(x + 1, z);
        Chunk chunk2 = chunkProvider.getLoadedChunk(x, z + 1);
        Chunk chunk3 = chunkProvider.getLoadedChunk(x - 1, z);

        if (chunk1 != null && chunk2 != null && chunkProvider.getLoadedChunk(x + 1, z + 1) != null) {
            Awesomepopulate(chunkGenrator, fakeworld, x, z);
        }

        if (chunk3 != null && chunk2 != null && chunkProvider.getLoadedChunk(x - 1, z + 1) != null) {
            Awesomepopulate(chunkGenrator, fakeworld, x - 1, z);
        }

        if (chunk != null && chunk1 != null && chunkProvider.getLoadedChunk(x + 1, z - 1) != null) {
            Awesomepopulate(chunkGenrator, fakeworld, x, z - 1);
        }

        if (chunk != null && chunk3 != null) {
            Chunk chunk4 = chunkProvider.getLoadedChunk(x - 1, z - 1);

            if (chunk4 != null) {
                Awesomepopulate(chunkGenrator, fakeworld, x - 1, z - 1);
            }
        }
    }


    private static void Awesomepopulate(IChunkGenerator overworldChunkGen, AwesomeWorld fakeworld, int x, int z) {
        Chunk testchunk = fakeworld.getChunk(x, z);
        if (testchunk.isTerrainPopulated()) {
            if (overworldChunkGen.generateStructures(testchunk, x, z)) {
                testchunk.markDirty();
            }
        } else {
            testchunk.checkLight();
            overworldChunkGen.populate(x, z);
            testchunk.markDirty();
        }
    }


    public static void event(PopulateChunkEvent.Populate event) {
        event.setResult(Event.Result.ALLOW);
    }

    public static void DecorateBiomeEvent(DecorateBiomeEvent.Decorate event) {
        event.setResult(Event.Result.ALLOW);
    }
}
