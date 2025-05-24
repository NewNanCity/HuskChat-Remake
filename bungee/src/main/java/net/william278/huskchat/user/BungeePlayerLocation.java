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
import org.jetbrains.annotations.NotNull;

/**
 * BungeeCord implementation of PlayerLocation for proxy environment
 */
public class BungeePlayerLocation implements PlayerLocationChangeEvent.PlayerLocation {
    
    private final String server;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    private BungeePlayerLocation(@NotNull String server, @NotNull String world, 
                                double x, double y, double z, float yaw, float pitch) {
        this.server = server;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Create a proxy location with server and world information
     * Coordinates are not available in proxy environment
     *
     * @param server 服务器名称 / server name
     * @param world 世界名称 / world name
     * @return 位置对象 / location object
     */
    @NotNull
    public static BungeePlayerLocation from(@NotNull String server, @NotNull String world) {
        return new BungeePlayerLocation(server, world, 0, 0, 0, 0, 0);
    }

    /**
     * Create a proxy location with full coordinate information
     * Used when backend server provides detailed location data
     *
     * @param server 服务器名称 / server name
     * @param world 世界名称 / world name
     * @param x X坐标 / X coordinate
     * @param y Y坐标 / Y coordinate
     * @param z Z坐标 / Z coordinate
     * @param yaw 偏航角 / yaw angle
     * @param pitch 俯仰角 / pitch angle
     * @return 位置对象 / location object
     */
    @NotNull
    public static BungeePlayerLocation from(@NotNull String server, @NotNull String world,
                                           double x, double y, double z, float yaw, float pitch) {
        return new BungeePlayerLocation(server, world, x, y, z, yaw, pitch);
    }

    @Override
    @NotNull
    public String getServer() {
        return server;
    }

    @Override
    @NotNull
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

    @Override
    @NotNull
    public String getRegionId() {
        // In proxy environment, region is typically server:world
        return server + ":" + world;
    }

    @Override
    public double distanceTo(@NotNull PlayerLocationChangeEvent.PlayerLocation other) {
        // In proxy environment, distance calculation is limited
        if (!getRegionId().equals(other.getRegionId())) {
            return Double.MAX_VALUE; // Different servers/worlds
        }
        
        // Calculate 3D distance if coordinates are available
        double dx = x - other.getX();
        double dy = y - other.getY();
        double dz = z - other.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public boolean isSameWorld(@NotNull PlayerLocationChangeEvent.PlayerLocation other) {
        return server.equals(other.getServer()) && world.equals(other.getWorld());
    }

    @Override
    public boolean isSameServer(@NotNull PlayerLocationChangeEvent.PlayerLocation other) {
        return server.equals(other.getServer());
    }

    @Override
    public String toString() {
        return String.format("BungeePlayerLocation{server='%s', world='%s', x=%.2f, y=%.2f, z=%.2f, yaw=%.2f, pitch=%.2f}",
                server, world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BungeePlayerLocation other)) return false;
        
        return server.equals(other.server) &&
               world.equals(other.world) &&
               Double.compare(other.x, x) == 0 &&
               Double.compare(other.y, y) == 0 &&
               Double.compare(other.z, z) == 0 &&
               Float.compare(other.yaw, yaw) == 0 &&
               Float.compare(other.pitch, pitch) == 0;
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
