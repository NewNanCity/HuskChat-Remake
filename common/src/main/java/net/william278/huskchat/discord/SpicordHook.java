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

package net.william278.huskchat.discord;

import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.event.PlayerLocationChangeEvent;
import net.william278.huskchat.event.PlayerStatusChangeEvent;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spicord.Spicord;
import org.spicord.api.addon.SimpleAddon;
import org.spicord.bot.DiscordBot;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SpicordHook implements DiscordHook {

    private final Addon addon;

    public SpicordHook(@NotNull HuskChat plugin) {
        this.addon = new Addon(plugin);

        // Register addon
        if (Spicord.getInstance().getAddonManager().registerAddon(addon)) {
            plugin.log(Level.INFO, "Registered HuskChat Spicord addon");
        } else {
            plugin.log(Level.SEVERE, "Unable to register HuskChat Spicord addon");
        }
    }

    @Override
    public void postMessage(@NotNull ChatMessage message) {
        if (message.getSender() instanceof SpicordOnlineUser) {
            return;
        }
        CompletableFuture.runAsync(() -> this.addon.sendMessage(message));
    }

    public static class SpicordOnlineUser extends OnlineUser {
        private final Settings.DiscordSettings settings;
        private final User discordUser;
        private final Message context;

        private SpicordOnlineUser(@NotNull HuskChat plugin, @NotNull User discordUser, @NotNull Message context) {
            super("", UUID.nameUUIDFromBytes(discordUser.getId().getBytes()), plugin);
            this.discordUser = discordUser;
            this.context = context;
            this.settings = plugin.getSettings().getDiscord();
        }

        @NotNull
        @Override
        public String getName() {
            return settings.getSpicord().getUsernameFormat()
                    .replaceAll(
                            "%discord_handle%",
                            getDiscriminatorString()
                                    .map(discriminator -> String.format("%s%s", discordUser.getName(), discriminator))
                                    .orElse(discordUser.getName())
                    );
        }

        private Optional<String> getDiscriminatorString() {
            try {
                return Optional.of(Integer.parseInt(discordUser.getDiscriminator()))
                        .flatMap(d -> d > 0
                                ? Optional.of(String.format("#%04d", d))
                                : Optional.empty()
                        );
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        @Override
        public int getPing() {
            return 0;
        }

        @NotNull
        @Override
        public String getServerName() {
            return String.format("#%s", context.getChannel().getName());
        }

        @Override
        public int getPlayersOnServer() {
            return 0;
        }

        @Override
        public boolean hasPermission(@Nullable String node, boolean allowByDefault) {
            return true;
        }

        @Override
        public void sendMessage(@NotNull Component component) {
            try {
                //todo in the future, it'd be nice to send this message ephemerally and delete the original message
                // if someone better at me at JDA wants to do this, feel free to make a PR
                context.reply(DiscordSerializer.INSTANCE.serialize(component)).queue();
            } catch (Throwable e) {
                plugin.log(Level.WARNING, "Unable to send contextual reply via Spicord to Discord user", e);
            }
        }

        @NotNull
        @Override
        public Audience getAudience() {
            throw new UnsupportedOperationException("Discord players cannot be used as an audience");
        }

        // ========== PlayerInfo 接口实现 / PlayerInfo interface implementation ==========

        @Override
        public double getHealth() {
            return 20.0; // Discord用户总是满血
        }

        @Override
        public double getMaxHealth() {
            return 20.0;
        }

        @Override
        public int getFoodLevel() {
            return 20; // Discord用户总是满饱食度
        }

        @Override
        public int getExperienceLevel() {
            return 0;
        }

        @Override
        @NotNull
        public PlayerLocationChangeEvent.PlayerLocation getLocation() {
            return new PlayerLocationChangeEvent.PlayerLocation() {
                @Override
                @NotNull
                public String getServer() {
                    return "discord";
                }

                @Override
                @NotNull
                public String getWorld() {
                    return "discord";
                }

                @Override
                public double getX() {
                    return 0;
                }

                @Override
                public double getY() {
                    return 0;
                }

                @Override
                public double getZ() {
                    return 0;
                }

                @Override
                public float getYaw() {
                    return 0;
                }

                @Override
                public float getPitch() {
                    return 0;
                }
            };
        }

        @Override
        @NotNull
        public PlayerInfo.GameMode getGameMode() {
            return PlayerInfo.GameMode.SPECTATOR; // Discord用户默认观察者模式
        }

        @Override
        public boolean isOnline() {
            return true; // Discord用户总是在线
        }

        @Override
        public boolean isSneaking() {
            return false;
        }

        @Override
        public boolean isFlying() {
            return false;
        }

        @Override
        public boolean isVanished() {
            return false;
        }

        @Override
        public boolean isMuted() {
            return false; // Discord用户不能被游戏内禁言
        }

        @Override
        public boolean isInCombat() {
            return false;
        }

        @Override
        public boolean isAway() {
            return false; // Discord用户不会离开
        }

        @Override
        @NotNull
        public Optional<Object> getStatus(@NotNull PlayerStatusChangeEvent.StatusType statusType) {
            return Optional.empty(); // Discord用户没有特殊状态
        }

        @Override
        @NotNull
        public Map<PlayerStatusChangeEvent.StatusType, Object> getAllStatuses() {
            return Map.of(); // Discord用户没有状态
        }

        @Override
        @Nullable
        public String getIpAddress() {
            return null; // Discord用户没有IP地址
        }

        @Override
        @Nullable
        public String getClientBrand() {
            return "Discord";
        }

        @Override
        public int getProtocolVersion() {
            return 0;
        }

        @Override
        @Nullable
        public String getLocale() {
            return "en_US"; // Discord用户默认英语
        }

        @Override
        public long getFirstJoinTime() {
            return discordUser.getTimeCreated().toEpochSecond() * 1000;
        }

        @Override
        public long getLastLoginTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long getSessionTime() {
            return 0; // Discord用户没有会话时间概念
        }

        @Override
        public long getTotalOnlineTime() {
            return 0; // Discord用户没有在线时间概念
        }

    }

    private static class Addon extends SimpleAddon {

        private final HuskChat plugin;
        private final Settings.DiscordSettings settings;
        private DiscordBot bot;

        private Addon(@NotNull HuskChat plugin) {
            super("HuskChat", "huskchat", "William278", plugin.getVersion().toString());
            this.plugin = plugin;
            this.settings = plugin.getSettings().getDiscord();
        }

        private void sendMessage(@NotNull ChatMessage message) {
            final Optional<Long> discordChannelId = Optional.ofNullable(
                    settings.getSpicord().getReceiveChannelMap().get(message.getChannel().getId())
            ).flatMap(id -> {
                try {
                    return Optional.of(Long.parseLong(id.trim()));
                } catch (NumberFormatException e) {
                    plugin.log(Level.WARNING, "Invalid Discord channel ID found in Spicord channel send map");
                    return Optional.empty();
                }
            });
            if (discordChannelId.isEmpty()) {
                return;
            }
            if (bot == null || bot.getJda() == null) {
                plugin.log(Level.WARNING, "No active bots found to dispatch message! " +
                        "Have you added \"huskchat\" to the \"addons:\" section of a bot in your Spicord config?");
                return;
            }

            final JDA jda = bot.getJda();
            final GuildChannel channel = jda.getGuildChannelById(discordChannelId.get());
            if (!(channel instanceof GuildMessageChannel guildChannel)) {
                plugin.log(Level.WARNING, "Unable to find Discord channel with ID " + discordChannelId.get());
                return;
            }

            // Check if the bot has permission to send messages to the channel
            if (!guildChannel.canTalk()) {
                plugin.log(Level.WARNING, "Unable to send message to Discord channel with ID "
                        + discordChannelId.get() + " (no permission)");
                return;
            }

            // Send the message
            this.dispatchMessage(message, guildChannel);
        }

        private void dispatchMessage(@NotNull ChatMessage message, @NotNull GuildMessageChannel channel) {
            final Format format = settings.getFormatStyle();
            channel.sendMessage(new MessageCreateBuilder()
                    // Disable mentions
                    .setAllowedMentions(List.of())

                    // Embedded formatting
                    .setEmbeds(format == Format.EMBEDDED ? List.of(new EmbedBuilder()
                            .setDescription(message.getMessage())
                            .setColor(0x00fb9a)
                            .setFooter(
                                    String.format("%s • %s",
                                            message.getSender().getName(),
                                            message.getSender().getServerName()
                                    ),
                                    String.format("https://minotar.net/avatar/%s/64",
                                            message.getSender().getUuid()
                                    )
                            )
                            .setTimestamp(OffsetDateTime.now())
                            .build()) : List.of())

                    // Inline formatting
                    .setContent(format == Format.INLINE ? String.format("### %s\n%s",
                            message.getSender().getName(), message.getMessage()) : null)

                    .build()
            ).queue();
        }

        @Override
        public void onLoad(DiscordBot bot) {
            this.bot = bot;
            plugin.log(Level.INFO, "Loaded HuskChat Spicord addon");
        }

        @Override
        public void onMessageReceived(@NotNull DiscordBot bot, @NotNull MessageReceivedEvent event) {
            // Don't send messages from bots (or myself)
            if (event.getAuthor().isBot()) {
                return;
            }

            // Get the channel ID, send an in-game message.
            final Optional<Channel> serverChannel = Optional.ofNullable(
                    settings.getSpicord().getSendChannelMap().get(event.getGuildChannel().getId())
            ).flatMap(plugin.getChannels()::getChannel);
            if (serverChannel.isEmpty()) {
                return;
            }

            new ChatMessage(
                    serverChannel.get(),
                    new SpicordOnlineUser(plugin, event.getAuthor(), event.getMessage()),
                    event.getMessage().getContentRaw(),
                    plugin
            ).dispatch();
        }

        @Override
        public void onShutdown(@NotNull DiscordBot bot) {
            plugin.log(Level.INFO, "Shutting down HuskChat Spicord addon...");
        }

    }


}
