/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.user;

import net.william278.huskchat.event.PlayerLocationChangeEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit玩家位置实现
 * Bukkit player location implementation
 */
public class BukkitPlayerLocation implements PlayerLocationChangeEvent.PlayerLocation {

    private final String server;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public BukkitPlayerLocation(@NotNull String server, @NotNull Location location) {
        this.server = server;
        this.world = location.getWorld() != null ? location.getWorld().getName() : "unknown";
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public BukkitPlayerLocation(@NotNull String server, @NotNull String world, 
                               double x, double y, double z, float yaw, float pitch) {
        this.server = server;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @NotNull
    public static BukkitPlayerLocation from(@NotNull String server, @NotNull Location location) {
        return new BukkitPlayerLocation(server, location);
    }

    @NotNull
    @Override
    public String getServer() {
        return server;
    }

    @NotNull
    @Override
    public String getWorld() {
        return world;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    /**
     * 转换为Bukkit Location对象
     * Convert to Bukkit Location object
     *
     * @param world Bukkit世界对象 / Bukkit world object
     * @return Bukkit Location对象 / Bukkit Location object
     */
    @NotNull
    public Location toBukkitLocation(@NotNull World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return String.format("BukkitPlayerLocation{server='%s', world='%s', x=%.2f, y=%.2f, z=%.2f, yaw=%.2f, pitch=%.2f}",
                server, world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BukkitPlayerLocation)) return false;
        
        BukkitPlayerLocation other = (BukkitPlayerLocation) obj;
        return server.equals(other.server) &&
               world.equals(other.world) &&
               Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0 &&
               Float.compare(yaw, other.yaw) == 0 &&
               Float.compare(pitch, other.pitch) == 0;
    }

    @Override
    public int hashCode() {
        int result = server.hashCode();
        result = 31 * result + world.hashCode();
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        result = 31 * result + Float.hashCode(yaw);
        result = 31 * result + Float.hashCode(pitch);
        return result;
    }
}
