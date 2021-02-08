# BedrockMiner
A minecraft plugin that adds craftable bedrock, as well as a craftable bedrock pickaxe which can mine bedrock

https://www.spigotmc.org/resources/bedrockminer.81335/

## Config

```yaml
messages:
  prefix: "&7[&bBedrockMiner&7] &e"
  not-bedrock: "&eWarning! You can only break &6Bedrock &eusing this pickaxe"
  reload: "&aReloaded Config"
  no-permission:
    craft:
      pickaxe: "&cYou do not have permission to craft a &6Bedrock Pickaxe&e."
      bedrock: "&cYou do not have permission to craft &6Bedrock&e."
    use:
      pickaxe: "&cYou do not have permission to use a &6Bedrock Pickaxe&e."
      bedrock: "&cYou do not have permission to place &6Bedrock&e."
    command:
      give: "&eYou do not have permission to use that command."
      console: "&cYou must be a player to utilize these commands!"
      target-not-found: "&cThe selected target could not be found"
      invalid: "&cThat command is invalid. Usage: /bedrockminer give [target]"
      too-many-args: "&cCommand passed with excess arguments. Usage: /bedrockminer give [target]"
    break: "&eYou can not break bedrock blocks at that layer."
    # Setting the cooldown to "" will disable the message. %TICKS% is replaced by the ticks left and %SECONDS% is replaced by the seconds left (rounded up)
    cooldown: "&dYou need to wait before using that again. Time remaining: %TICKS% ticks (%SECONDS% seconds)."
    world: "&cBedrock Pickaxes are disabled in this world."

# Customize the pickaxe item meta (changing model data will make any pickaxes currently in circulation to not work)
pickaxe:
  name: "&6Bedrock Pickaxe"
  lore:
    - "Can be used to break bedrock"
  enchantments:
    - vanishing_curse:1
  custom-model-data: 6969696

# Customize the bedrock item meta
bedrock:
  name: "&6Bedrock"
  lore:
    - "Can be used to craft a bedrock pickaxe"
  custom-model-data: 6969697

# If enabled, drops bedrock blocks if the pickaxe used if enchanted with silk touch
silk-touch: true

# Level silk touch required to obtain bedrock
silk-touch-level: 1

# The amount of durability to remove from the pickaxe on each usage
durability: 100

# List of disabled layers
disabled-layers:
  - 0
  - 1
  - 126
  - 127

# Multiverse support
disabled-worlds:
  - example_world
  - world_nether

# Delay between breaking blocks (ticks)
break-delay: 10

# Whether or not to register the bedrock crafting recipe
bedrock-recipe: true

# Shaped crafting recipe for crafting bedrock
bedrock-shaped-recipe:
  - crying_obsidian, crying_obsidian, crying_obsidian
  - crying_obsidian, ancient_debris,  crying_obsidian
  - crying_obsidian, crying_obsidian, crying_obsidian

# Whether or not to register the pickaxe crafting recipe
pickaxe-recipe: true

# Forces players to use the custom bedrock item when making the pickaxe.
# Disable if your recipe doesn't use bedrock or if you have another bedrock source you would prefer.
require-custom-bedrock: true

# Shaped crafting recipe for crafting bedrock pickaxe
pickaxe-shaped-recipe:
  - bedrock, bedrock, bedrock
  - air,     stick,   air
  - air,     stick,   air

# Allow players to apply silk touch in an anvil
allow-anvil-silk: true

# Allow players to add other enchantments through an anvil
allow-anvil-other: false

# Allow players to rename the pickaxe (THIS FEATURE IS EXPERIMENTAL AND MAY BREAK)
allow-anvil-rename: false

# Allow players to merge pickaxe with other pickaxes to try and sneak mending/etc on it, or to prevent repair
allow-anvil-merge: false
```

## Issues/Requests
If you have any issues with the plugin, or you would like to request a new feature, create a new issue on this repository or DM me on discord at `Wowkster#0969`
