package com.github.lunatrius.schematica.handler;

import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.chunk.SchematicContainer;
import com.github.lunatrius.schematica.world.schematic.SchematicFormat;
import com.mrzak34.thunderhack.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.Queue;

public class QueueTickHandler {
    public static final QueueTickHandler INSTANCE = new QueueTickHandler();

    private final Queue<SchematicContainer> queue = new ArrayDeque<SchematicContainer>();

    private QueueTickHandler() {}

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        // TODO: find a better way... maybe?
        try {
            final EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null && player.connection != null && !player.connection.getNetworkManager().isLocalChannel()) {
                processQueue();
            }
        } catch (final Exception e) {
            Reference.logger.error("Something went wrong...", e);
        }
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        processQueue();
    }

    private void processQueue() {
        if (this.queue.size() == 0) {
            return;
        }

        final SchematicContainer container = this.queue.poll();
        if (container == null) {
            return;
        }

        if (container.hasNext()) {
            if (container.isFirst()) {
                Command.sendMessage("Начинаю сохранять схему! Кол-во чанков: " + container.chunkCount + " Название файла: " + container.file.getName() );
            }

            container.next();
        }

        if (container.hasNext()) {
            this.queue.offer(container);
        } else {
            SchematicFormat.writeToFileAndNotify(container.file, container.format, container.schematic, container.player);
        }
    }

    public void queueSchematic(final SchematicContainer container) {
        this.queue.offer(container);
    }
}
