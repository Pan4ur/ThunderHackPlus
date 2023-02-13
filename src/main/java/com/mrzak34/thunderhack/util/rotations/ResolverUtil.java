package com.mrzak34.thunderhack.util.rotations;

import com.mrzak34.thunderhack.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.Vec3d;

import static com.mrzak34.thunderhack.util.Util.mc;


public class ResolverUtil {

    public static double backUpX, backUpY, backUpZ, serverX, serverY, serverZ, prevServerX, prevServerY, prevServerZ;


    public static void resolve(EntityOtherPlayerMP player) {
        backUpX = player.posX;
        backUpY = player.posY;
        backUpZ = player.posZ;
        Vec3d position = mc.player.getPositionVector();
        Vec3d target;
        Vec3d from = new Vec3d(prevServerX, prevServerY, prevServerZ);
        Vec3d to = new Vec3d(serverX, serverY, serverZ);
        if (position.distanceTo(from) > position.distanceTo(to)) {
            target = to;
        } else {
            target = from;
        }
        if(prevServerX != 0 &&  prevServerZ != 0 && prevServerY != 0 && serverY != 0 && serverX != 0 && serverZ != 0)
            player.setPosition(target.x, target.y, target.z);
    }


    public static void releaseResolver(EntityOtherPlayerMP player) {
        if (backUpY != -999) {
            player.setPosition(backUpX, backUpY, backUpZ);
            backUpY = -999;
        }
    }

    public static void reset() {
        backUpX = 0;
        backUpY = -999;
        backUpZ= 0;
        serverX= 0;
        serverY= 0;
        serverZ= 0;
        prevServerX= 0;
        prevServerY= 0;
        prevServerZ= 0;
    }
}
