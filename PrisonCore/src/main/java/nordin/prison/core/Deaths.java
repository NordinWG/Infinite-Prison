package nordin.prison.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Deaths implements Listener {

    private static final Random random = new Random();

    private final Map<String, List<String>> messages = Map.ofEntries(
        Map.entry("generic", List.of(
            "&e%player% &7messed up big time and fell into their own doom.",
            "&e%player% &7tripped over their ego and didn’t get back up.",
            "&e%player% &7choked on their own bad ideas and bit the dust.",
            "&e%player% &7was too darn clueless to keep breathing."
        )),
        Map.entry("pvp", List.of(
            "&e%player% &7got their butt whooped in a fight, ouch!",
            "&e%player% &7was clobbered in PvP like a total rookie.",
            "&e%player% &7fought so badly, their friends laughed and left.",
            "&e%player% &7was turned into a loser sandwich by a pro."
        )),
        Map.entry("arrow", List.of(
            "&e%player% &7caught an arrow in the backside and flopped over.",
            "&e%player% &7became a human pincushion, full of arrows.",
            "&e%player% &7dodged like a goof and ate a pointy stick.",
            "&e%player% &7got sniped and fell like a clumsy dork."
        )),
        Map.entry("fall", List.of(
            "&e%player% &7fell off a cliff like a bumbling idiot, splat!",
            "&e%player% &7thought they could fly, but gravity said, 'Nope!'",
            "&e%player% &7hit the ground so hard their helmet cracked.",
            "&e%player% &7forgot how to walk and went kersplat."
        )),
        Map.entry("lava", List.of(
            "&e%player% &7jumped into lava thinking it was a hot tub.",
            "&e%player% &7turned into a crispy mess, extra toasty.",
            "&e%player% &7became a human s’more, burned to a crisp.",
            "&e%player% &7mistook lava for soup and got roasted."
        )),
        Map.entry("drowning", List.of(
            "&e%player% &7drowned like a goof in a shallow puddle.",
            "&e%player% &7forgot how to swim and sank like a rock.",
            "&e%player% &7gulped too much water and flopped over.",
            "&e%player% &7sank to the bottom like a soggy biscuit."
        )),
        Map.entry("fire", List.of(
            "&e%player% &7got too cozy with a campfire and went up in smoke.",
            "&e%player% &7was toasted like a marshmallow with no skills.",
            "&e%player% &7played with fire and ended up as ashes.",
            "&e%player% &7caught fire and danced like a total numpty."
        )),
        Map.entry("explosion", List.of(
            "&e%player% &7blew up like a clown messing with dynamite.",
            "&e%player% &7went boom like a goof with a firecracker.",
            "&e%player% &7hugged a TNT block and became glittery bits.",
            "&e%player% &7stood in a blast zone like a total knucklehead."
        )),
        Map.entry("zombie", List.of(
            "&e%player% &7was munched by zombies like a screaming rookie.",
            "&e%player% &7became zombie snacks, worst picnic ever.",
            "&e%player% &7ran too slow and got chomped, what a dope."
        )),
        Map.entry("skeleton", List.of(
            "&e%player% &7was shot full of arrows like a goofy target.",
            "&e%player% &7got boned by a skeleton’s bow, total fail.",
            "&e%player% &7stood still like a dork and ate skeleton arrows."
        )),
        Map.entry("spider", List.of(
            "&e%player% &7was bitten by a spider and flopped like a klutz.",
            "&e%player% &7got stuck in a web and died like a goofball.",
            "&e%player% &7became spider chow, turned into bug leftovers."
        )),
        Map.entry("creeper", List.of(
            "&e%player% &7was blasted by a creeper’s sneaky boom-boom.",
            "&e%player% &7missed the hiss and got blown to bits, doofus.",
            "&e%player% &7partied with a creeper and lost their dang head."
        )),
        Map.entry("enderman", List.of(
            "&e%player% &7stared at an enderman and got yeeted to nowhere.",
            "&e%player% &7ticked off the tall weirdo and ate a fist.",
            "&e%player% &7was warped away like a clueless numbskull."
        )),
        Map.entry("dragon", List.of(
            "&e%player% &7got roasted by the Ender Dragon’s bad breath.",
            "&e%player% &7was chomped by the Ender Dragon, epic fail.",
            "&e%player% &7thought they could fistfight a dragon, lol."
        )),
        Map.entry("lightning", List.of(
            "&e%player% &7was zapped by lightning like a human conductor.",
            "&e%player% &7danced in a storm and got fried crispy.",
            "&e%player% &7held a metal rod too high, zap!"
        )),
        Map.entry("starvation", List.of(
            "&e%player% &7forgot to eat and starved like a dope.",
            "&e%player% &7ran out of snacks and just gave up.",
            "&e%player% &7thought hunger was just a vibe, wrong."
        )),
        Map.entry("anvil", List.of(
            "&e%player% &7got flattened by an anvil, cartoon style.",
            "&e%player% &7stood under an anvil like a total goof.",
            "&e%player% &7was squashed like a bug by falling iron."
        )),
        Map.entry("cactus", List.of(
            "&e%player% &7hugged a cactus and regretted it instantly.",
            "&e%player% &7thought cacti were cuddly, big mistake.",
            "&e%player% &7got pricked to death like a spiky pincushion."
        )),
        Map.entry("magic", List.of(
            "&e%player% &7was zapped by magic like a wannabe wizard.",
            "&e%player% &7messed with potions and got sparkled to death.",
            "&e%player% &7thought they could handle magic, nope."
        )),
        Map.entry("void", List.of(
            "&e%player% &7fell into the nothingness like a proper space cadet.",
            "&e%player% &7took a leap of faith… and missed."
        )),
        Map.entry("suffocation", List.of(
            "&e%player% &7got stuck in a wall like a clueless blockhead.",
            "&e%player% &7suffocated in a tight spot, what a dummy.",
            "&e%player% &7forgot how to breathe inside a block."
        )),
        Map.entry("poison", List.of(
            "&e%player% &7drank some bad potion and turned green.",
            "&e%player% &7got poisoned and flopped like a sick fish.",
            "&e%player% &7thought poison was juice and gulped it down."
        )),
        Map.entry("wither", List.of(
            "&e%player% &7withered away like a sad, spooky flower.",
            "&e%player% &7got smoked by the Wither’s bad vibes.",
            "&e%player% &7crumbled under the Wither’s evil glare."
        ))
    );

    public Deaths(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        e.setDeathMessage(null);

        String causeRaw = Optional.ofNullable(victim.getLastDamageCause())
            .map(dc -> dc.getCause().name().toLowerCase(Locale.ROOT))
            .orElse("generic");

        Entity killer = e.getEntity().getKiller();
        String key = "generic";

        if (killer instanceof Player) key = "pvp";
        else if (killer instanceof Arrow) key = "arrow";
        else if (killer instanceof Zombie) key = "zombie";
        else if (killer instanceof Skeleton) key = "skeleton";
        else if (killer instanceof Spider) key = "spider";
        else if (killer instanceof Creeper) key = "creeper";
        else if (killer instanceof Enderman) key = "enderman";
        else if (killer instanceof EnderDragon) key = "dragon";

        switch (causeRaw) {
            case "fall":
                key = "fall";
                break;
            case "fire":
            case "fire_tick":
                key = "fire";
                break;
            case "lava":
                key = "lava";
                break;
            case "drowning":
                key = "drowning";
                break;
            case "entity_explosion":
            case "block_explosion":
                key = "explosion";
                break;
            case "starvation":
                key = "starvation";
                break;
            case "void":
                key = "void";
                break;
            case "suffocation":
                key = "suffocation";
                break;
            case "poison":
                key = "poison";
                break;
            case "wither":
                key = "wither";
                break;
            case "lightning":
                key = "lightning";
                break;
            default:
        }

        List<String> pool = messages.getOrDefault(key, messages.get("generic"));
        String rawMsg = pool.get(random.nextInt(pool.size())).replace("%player%", victim.getName());

        Component msg = LegacyComponentSerializer.legacyAmpersand().deserialize(rawMsg);
        Bukkit.broadcast(msg);

        String discordMsg = ChatColor.stripColor(rawMsg).replaceAll("&.", "");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordsrv broadcast " + discordMsg + "");
    }
}