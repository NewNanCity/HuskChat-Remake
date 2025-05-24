# HuskChat Remake Enhanced Example Plugin

This is a complete example plugin demonstrating the new player status integration features of HuskChat Remake.

## Plugin Features

- Health-based chat restrictions
- Location-based regional chat
- Combat status chat restrictions
- Player status monitoring and management
- Command execution API demonstration
- Custom death/respawn messages

## Complete Code

### plugin.yml

```yaml
name: HuskChatEnhancedExample
version: 2.0.0
main: com.example.enhanced.HuskChatEnhancedPlugin
api-version: 1.19
depend: [HuskChat]

commands:
  playerstatus:
    description: View or set player status
    usage: /playerstatus [player] [status] [value]
    permission: huskchat.enhanced.status
  
  nearbychat:
    description: Send message to nearby players
    usage: /nearbychat <radius> <message>
    permission: huskchat.enhanced.nearby
    
  combatmode:
    description: Toggle combat mode
    usage: /combatmode [on|off]
    permission: huskchat.enhanced.combat

permissions:
  huskchat.enhanced.status:
    description: Manage player status
    default: op
  huskchat.enhanced.nearby:
    description: Use nearby chat
    default: true
  huskchat.enhanced.combat:
    description: Toggle combat mode
    default: true
  huskchat.enhanced.lowhealth.bypass:
    description: Bypass low health chat restrictions
    default: false
```

### Main Plugin Class

```java
package com.example.enhanced;

import net.william278.huskchat.api.HuskChatExtendedAPI;
import net.william278.huskchat.event.*;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HuskChatEnhancedPlugin extends JavaPlugin implements Listener {
    
    private HuskChatExtendedAPI huskChatAPI;
    private final Map<UUID, Long> combatTimers = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastRegions = new ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        try {
            // Get HuskChat API
            huskChatAPI = HuskChatExtendedAPI.getInstance();
            getLogger().info("Successfully connected to HuskChat Extended API");
            
            // Register event listeners
            registerEventListeners();
            registerBukkitEvents();
            
            // Start periodic tasks
            startPeriodicTasks();
            
            getLogger().info("HuskChat Enhanced Example Plugin enabled!");
        } catch (Exception e) {
            getLogger().severe("Failed to connect to HuskChat API: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private void registerEventListeners() {
        // Listen to chat messages - add health and location restrictions
        huskChatAPI.registerChatMessageListener(this::onChatMessage);
        
        // Listen to player health changes
        huskChatAPI.registerPlayerHealthChangeListener(this::onHealthChange);
        
        // Listen to player location changes
        huskChatAPI.registerPlayerLocationChangeListener(this::onLocationChange);
        
        // Listen to player status changes
        huskChatAPI.registerPlayerStatusChangeListener(this::onStatusChange);
        
        // Listen to player death
        huskChatAPI.registerPlayerDeathListener(this::onPlayerDeath);
        
        // Listen to player respawn
        huskChatAPI.registerPlayerRespawnListener(this::onPlayerRespawn);
        
        // Listen to command execution
        huskChatAPI.registerChatCommandListener(this::onChatCommand);
    }
    
    private void registerBukkitEvents() {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    private void startPeriodicTasks() {
        // Check combat status every second
        Bukkit.getScheduler().runTaskTimer(this, this::checkCombatStatus, 20L, 20L);
        
        // Check player statuses every 5 seconds
        Bukkit.getScheduler().runTaskTimer(this, this::checkPlayerStatuses, 100L, 100L);
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        String channelId = event.getChannelId();
        
        // Check chat conditions
        HuskChatExtendedAPI.ChatConditionResult result = huskChatAPI.checkChatConditions(sender, channelId);
        if (!result.isAllowed()) {
            event.setCancelled(true);
            sender.sendMessage("§cCannot send message: " + result.getReason());
            return;
        }
        
        // Add status prefix
        String statusPrefix = getStatusPrefix(sender);
        if (!statusPrefix.isEmpty()) {
            event.setMessage(statusPrefix + " " + event.getMessage());
        }
        
        // Location-based message handling
        if (channelId.equals("local")) {
            handleLocalChat(event);
        }
    }
    
    private void onHealthChange(PlayerHealthChangeEvent event) {
        OnlineUser player = event.getPlayer();
        double newHealth = event.getNewHealth();
        double maxHealth = event.getMaxHealth();
        
        // Check if entering low health state
        if (newHealth <= maxHealth * 0.2 && event.getPreviousHealth() > maxHealth * 0.2) {
            player.sendMessage("§c⚠ Warning: Your health is low, some channels may be restricted!");
            
            // Update status
            huskChatAPI.updatePlayerStatus(player, 
                PlayerStatusChangeEvent.StatusType.CUSTOM, 
                "low_health", 
                "Health dropped below 20%", 
                -1);
        }
        
        // Check if health recovered
        if (newHealth > maxHealth * 0.2 && event.getPreviousHealth() <= maxHealth * 0.2) {
            player.sendMessage("§aHealth recovered, chat restrictions lifted.");
            
            // Remove low health status
            huskChatAPI.updatePlayerStatus(player, 
                PlayerStatusChangeEvent.StatusType.CUSTOM, 
                null, 
                "Health recovered", 
                -1);
        }
    }
    
    private void onLocationChange(PlayerLocationChangeEvent event) {
        OnlineUser player = event.getPlayer();
        String newRegion = event.getNewLocation().getRegionId();
        String previousRegion = lastRegions.get(player.getUuid());
        
        if (previousRegion != null && !previousRegion.equals(newRegion)) {
            // Player entered new region
            player.sendMessage("§7You entered a new region: §b" + newRegion);
            
            // Check for region-specific channels
            String regionChannel = "region_" + newRegion.replace(":", "_");
            if (huskChatAPI.getChannels().contains(regionChannel)) {
                huskChatAPI.switchPlayerChannel(player, regionChannel, 
                    ChannelSwitchEvent.SwitchReason.API_CALL);
                player.sendMessage("§aAutomatically switched to region channel: " + regionChannel);
            }
        }
        
        lastRegions.put(player.getUuid(), newRegion);
    }
    
    private void onStatusChange(PlayerStatusChangeEvent event) {
        OnlineUser player = event.getPlayer();
        PlayerStatusChangeEvent.StatusType statusType = event.getStatusType();
        Object newValue = event.getNewValue();
        
        getLogger().info(String.format("Player %s status %s changed to: %s", 
            player.getUsername(), statusType.getKey(), newValue));
        
        // Execute specific logic based on status change
        switch (statusType) {
            case COMBAT:
                if ((Boolean) newValue) {
                    player.sendMessage("§cYou entered combat mode! Some chat features may be restricted.");
                } else {
                    player.sendMessage("§aYou left combat mode.");
                }
                break;
            case AWAY:
                if ((Boolean) newValue) {
                    player.sendMessage("§7You are now away.");
                } else {
                    player.sendMessage("§aWelcome back!");
                }
                break;
        }
    }
    
    private void onPlayerDeath(net.william278.huskchat.event.PlayerDeathEvent event) {
        OnlineUser player = event.getPlayer();
        OnlineUser killer = event.getKiller();
        
        // Custom death message
        if (killer != null) {
            String customMessage = String.format("§c%s was defeated by %s!", 
                player.getUsername(), killer.getUsername());
            event.setDeathMessage(customMessage);
        } else {
            String customMessage = String.format("§7%s died", player.getUsername());
            event.setDeathMessage(customMessage);
        }
        
        // Set death message channel
        event.setDeathMessageChannel("global");
        
        // Clear combat status
        huskChatAPI.updatePlayerStatus(player, 
            PlayerStatusChangeEvent.StatusType.COMBAT, 
            false, 
            "Player died", 
            -1);
    }
    
    private void onPlayerRespawn(net.william278.huskchat.event.PlayerRespawnEvent event) {
        OnlineUser player = event.getPlayer();
        
        // Send respawn message
        event.setSendRespawnMessage(true);
        event.setRespawnMessageChannel("global");
        
        // Delayed welcome back message
        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.sendMessage("§aYou have respawned! Health and status reset.");
        }, 20L);
    }
    
    private void onChatCommand(ChatCommandEvent event) {
        OnlineUser player = event.getPlayer();
        String command = event.getCommand();
        
        if (event.getPhase() == ChatCommandEvent.ExecutionPhase.PRE) {
            // Pre-execution checks
            if (!huskChatAPI.validateCommandPermission(player, command)) {
                event.setCancelled(true);
                player.sendMessage("§cYou don't have permission to execute this command!");
                return;
            }
            
            // Log command execution
            getLogger().info(String.format("Player %s executing command: %s %s", 
                player.getUsername(), command, String.join(" ", event.getArgs())));
        } else {
            // Post-execution handling
            if (event.isSuccessful()) {
                getLogger().info(String.format("Command executed successfully: %s", command));
            } else {
                getLogger().warning(String.format("Command execution failed: %s - %s", 
                    command, event.getFailureReason()));
            }
        }
    }
    
    // Bukkit event listeners
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player) {
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            
            // Set combat status
            combatTimers.put(player.getUniqueId(), System.currentTimeMillis());
            huskChatAPI.updatePlayerStatus(huskPlayer, 
                PlayerStatusChangeEvent.StatusType.COMBAT, 
                true, 
                "Entered combat", 
                15000); // 15 seconds combat status
        }
    }
    
    private void checkCombatStatus() {
        long currentTime = System.currentTimeMillis();
        combatTimers.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > 15000) { // 15 seconds combat timeout
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
                    huskChatAPI.updatePlayerStatus(huskPlayer, 
                        PlayerStatusChangeEvent.StatusType.COMBAT, 
                        false, 
                        "Combat timeout", 
                        -1);
                }
                return true;
            }
            return false;
        });
    }
    
    private void checkPlayerStatuses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            PlayerInfo info = huskChatAPI.getPlayerInfo(huskPlayer);
            
            // Check AFK status (example: 5 minutes no movement)
            // More complex AFK detection logic can be added here
        }
    }
    
    private String getStatusPrefix(OnlineUser player) {
        StringBuilder prefix = new StringBuilder();
        
        if (player.isInCombat()) {
            prefix.append("§c[Combat]");
        }
        if (player.isLowHealth()) {
            prefix.append("§4[Low HP]");
        }
        if (player.isAway()) {
            prefix.append("§7[Away]");
        }
        
        return prefix.toString();
    }
    
    private void handleLocalChat(ChatMessageEvent event) {
        OnlineUser sender = event.getSender();
        
        // Get nearby players
        List<OnlineUser> nearbyPlayers = huskChatAPI.getNearbyPlayers(sender, 50.0);
        
        // Distance-based message delivery logic can be implemented here
        // Actual implementation requires extending the channel system
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "playerstatus":
                return handlePlayerStatusCommand(sender, args);
            case "nearbychat":
                return handleNearbyChatCommand(sender, args);
            case "combatmode":
                return handleCombatModeCommand(sender, args);
            default:
                return false;
        }
    }
    
    private boolean handlePlayerStatusCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("huskchat.enhanced.status")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            // Show own status
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole cannot view status!");
                return true;
            }
            
            Player player = (Player) sender;
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            showPlayerStatus(sender, huskPlayer);
        } else if (args.length == 1) {
            // Show other player's status
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer " + args[0] + " is not online!");
                return true;
            }
            
            OnlineUser huskTarget = huskChatAPI.adaptPlayer(target);
            showPlayerStatus(sender, huskTarget);
        } else if (args.length >= 3) {
            // Set player status
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer " + args[0] + " is not online!");
                return true;
            }
            
            OnlineUser huskTarget = huskChatAPI.adaptPlayer(target);
            String statusKey = args[1];
            String value = args[2];
            
            PlayerStatusChangeEvent.StatusType statusType = 
                PlayerStatusChangeEvent.StatusType.fromKey(statusKey);
            
            huskChatAPI.updatePlayerStatus(huskTarget, statusType, value, 
                "Admin command", -1)
                .thenAccept(success -> {
                    if (success) {
                        sender.sendMessage("§aSuccessfully set player status!");
                    } else {
                        sender.sendMessage("§cFailed to set status!");
                    }
                });
        }
        
        return true;
    }
    
    private boolean handleNearbyChatCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /nearbychat <radius> <message>");
            return true;
        }
        
        try {
            double radius = Double.parseDouble(args[0]);
            String message = String.join(" ", 
                java.util.Arrays.copyOfRange(args, 1, args.length));
            
            OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
            List<OnlineUser> nearbyPlayers = huskChatAPI.getNearbyPlayers(huskPlayer, radius);
            
            String formattedMessage = String.format("§e[Nearby] %s: §f%s", 
                player.getName(), message);
            
            // Send to nearby players
            for (OnlineUser nearbyPlayer : nearbyPlayers) {
                nearbyPlayer.sendMessage(formattedMessage);
            }
            
            sender.sendMessage(String.format("§aMessage sent to %d nearby players", 
                nearbyPlayers.size()));
            
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid radius value!");
        }
        
        return true;
    }
    
    private boolean handleCombatModeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        OnlineUser huskPlayer = huskChatAPI.adaptPlayer(player);
        boolean currentCombat = huskPlayer.isInCombat();
        
        if (args.length == 0) {
            // Toggle status
            boolean newCombat = !currentCombat;
            huskChatAPI.updatePlayerStatus(huskPlayer, 
                PlayerStatusChangeEvent.StatusType.COMBAT, 
                newCombat, 
                "Manual toggle", 
                -1);
            
            sender.sendMessage(newCombat ? "§cCombat mode enabled" : "§aCombat mode disabled");
        } else {
            // Set specific status
            boolean newCombat = args[0].equalsIgnoreCase("on") || 
                               args[0].equalsIgnoreCase("true");
            
            huskChatAPI.updatePlayerStatus(huskPlayer, 
                PlayerStatusChangeEvent.StatusType.COMBAT, 
                newCombat, 
                "Manual set", 
                -1);
            
            sender.sendMessage(newCombat ? "§cCombat mode enabled" : "§aCombat mode disabled");
        }
        
        return true;
    }
    
    private void showPlayerStatus(CommandSender sender, OnlineUser player) {
        PlayerInfo info = huskChatAPI.getPlayerInfo(player);
        
        sender.sendMessage("§6=== " + player.getUsername() + "'s Status ===");
        sender.sendMessage("§7Health: §f" + String.format("%.1f/%.1f (%.1f%%)", 
            info.getHealth(), info.getMaxHealth(), info.getHealthPercentage()));
        sender.sendMessage("§7Food: §f" + info.getFoodLevel() + "/20 (" + 
            String.format("%.1f%%", info.getFoodPercentage()) + ")");
        sender.sendMessage("§7Experience Level: §f" + info.getExperienceLevel());
        sender.sendMessage("§7Game Mode: §f" + info.getGameMode().getName());
        sender.sendMessage("§7Location: §f" + info.getLocation().getRegionId());
        sender.sendMessage("§7Session Time: §f" + formatTime(info.getSessionTime()));
        
        sender.sendMessage("§7Status:");
        sender.sendMessage("  §7Combat: " + (info.isInCombat() ? "§cYes" : "§aNo"));
        sender.sendMessage("  §7Away: " + (info.isAway() ? "§cYes" : "§aNo"));
        sender.sendMessage("  §7Muted: " + (info.isMuted() ? "§cYes" : "§aNo"));
        sender.sendMessage("  §7Sneaking: " + (info.isSneaking() ? "§cYes" : "§aNo"));
        sender.sendMessage("  §7Flying: " + (info.isFlying() ? "§cYes" : "§aNo"));
        sender.sendMessage("  §7Vanished: " + (info.isVanished() ? "§cYes" : "§aNo"));
    }
    
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    @Override
    public void onDisable() {
        // Clean up resources
        if (huskChatAPI != null) {
            huskChatAPI.unregisterChatMessageListener(this::onChatMessage);
            huskChatAPI.unregisterPlayerHealthChangeListener(this::onHealthChange);
            huskChatAPI.unregisterPlayerLocationChangeListener(this::onLocationChange);
            huskChatAPI.unregisterPlayerStatusChangeListener(this::onStatusChange);
            huskChatAPI.unregisterPlayerDeathListener(this::onPlayerDeath);
            huskChatAPI.unregisterPlayerRespawnListener(this::onPlayerRespawn);
            huskChatAPI.unregisterChatCommandListener(this::onChatCommand);
        }
        
        getLogger().info("HuskChat Enhanced Example Plugin disabled!");
    }
}
```

## Feature Demonstration

### 1. Health-based Chat Restrictions
- When player health drops below 20%, certain channels are restricted
- Can be bypassed with permission `huskchat.enhanced.lowhealth.bypass`

### 2. Location-based Chat
- Players automatically switch to region channels when entering new areas
- `/nearbychat` command sends messages to nearby players

### 3. Combat Status Management
- Players automatically enter combat mode when attacked
- Certain chat features are restricted during combat
- `/combatmode` command manually toggles combat status

### 4. Player Status Monitoring
- `/playerstatus` command shows detailed player information
- Real-time monitoring of health, location, game mode, etc.

### 5. Custom Death/Respawn Messages
- Death messages customized based on death cause
- Welcome messages sent on respawn

## Configuration Suggestions

Add the following channel configuration to HuskChat config:

```yaml
channels:
  global:
    # Global channel configuration
    restrictions:
      low_health: false  # Don't restrict low health players
      combat: false      # Don't restrict combat players
  
  staff:
    # Staff channel configuration
    restrictions:
      low_health: true   # Restrict low health players
      combat: true       # Restrict combat players
```

This enhanced example demonstrates the powerful capabilities of HuskChat Remake's new features, providing developers with rich player status integration and chat management functionality.
