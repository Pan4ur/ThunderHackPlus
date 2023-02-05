package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.events.PostWorldTick;
import com.mrzak34.thunderhack.modules.Feature;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Makes snapshots of {@link WorldClient#loadedEntityList} and
 * {@link WorldClient#playerEntities} so you can access them
 * on another thread.
 */
@SuppressWarnings("unused")
public class EntityProvider extends Feature
{
    private volatile List<EntityPlayer> players;
    private volatile List<Entity> entities;

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    public EntityProvider()
    {
        this.players  = Collections.emptyList();
        this.entities = Collections.emptyList();
    }

    @SubscribeEvent
    public void onPostTick(PostWorldTick e){
        update();
    }



    private void update()
    {
        if (mc.world != null)
        {
            setLists(new ArrayList<>(mc.world.loadedEntityList), new ArrayList<>(mc.world.playerEntities));
        }
        else
        {
            setLists(Collections.emptyList(),
                    Collections.emptyList());
        }
    }

    private void setLists(List<Entity> loadedEntities,
                          List<EntityPlayer> playerEntities)
    {
        entities = loadedEntities;
        players  = playerEntities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     *  @return copy of {@link WorldClient#loadedEntityList}
     */
    public List<Entity> getEntities()
    {
        return entities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     * @return copy of {@link WorldClient#playerEntities}
     */
    public List<EntityPlayer> getPlayers()
    {
        return players;
    }

    public List<Entity> getEntitiesAsync()
    {
        return getEntities(!mc.isCallingFromMinecraftThread());
    }

    public List<EntityPlayer> getPlayersAsync()
    {
        return getPlayers(!mc.isCallingFromMinecraftThread());
    }

    public List<Entity> getEntities(boolean async)
    {
        return async ? entities : mc.world.loadedEntityList;
    }

    public List<EntityPlayer> getPlayers(boolean async)
    {
        return async ? players : mc.world.playerEntities;
    }

    public Entity getEntity(int id)
    {
        List<Entity> entities = getEntitiesAsync();
        if (entities != null)
        {
            return entities.stream()
                    .filter(e -> e != null && e.getEntityId() == id)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

}