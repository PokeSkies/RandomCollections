# RandomCollections
[![GitHub release](https://img.shields.io/github/v/release/PokeSkies/RandomCollections?include_prereleases&label=Latest%20Release)](
<img height="50" src="https://camo.githubusercontent.com/a94064bebbf15dfed1fddf70437ea2ac3521ce55ac85650e35137db9de12979d/68747470733a2f2f692e696d6775722e636f6d2f6331444839564c2e706e67" alt="Requires Fabric Kotlin"/>

A Fabric server-sided reward pools mod! Create collections of loot that you can randomly distribute to players.

More information on configuration can be found on the [Wiki](https://github.com/PokeSkies/RandomCollections/wiki)!

## Features
- Create practically infinite reward collections *(idk, haven't tested that)*
- 3 different reward types (item, command console, command player)
- Placeholder Integrations (Impactor, PlaceholderAPI)

## Installation
1. Download the latest version of the mod from [Modrinth](https://modrinth.com/mod/skiesguis).
2. Download all required dependencies:
   - [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) 
   - [Fabric Permissions API](https://github.com/PokeSkies/fabric-permissions-api)
3. Download any optional dependencies:
   - [Impactor](https://modrinth.com/mod/impactor) **_(Placeholders)_**
   - [MiniPlaceholders](https://modrinth.com/plugin/miniplaceholders) **_(Placeholders)_**
   - [PlaceholderAPI]() **_(Placeholders)_**
4. Install the mod and dependencies into your server's `mods` folder.
5. Configure your collections in the `./config/randomcollections/config.json` file.

## Commands/Permissions
| Command                            | Description                                                               | Permission                       |
|------------------------------------|---------------------------------------------------------------------------|----------------------------------|
| /rc <collection> [player] [amount] | Gives the amount of rewards to the specified players from the listed pool | randomcollections.command.base   |
| /rc reload                         | Reload the Mod                                                            | randomcollections.command.reload |
| /rc debug                          | Toggle the debug mode for more insight into errors                        | randomcollections.command.debug  |

## Support
A community support Discord has been opened up for all Skies Development related projects! Feel free to join and ask questions or leave suggestions :)

<a class="discord-widget" href="https://discord.gg/cgBww275Fg" title="Join us on Discord"><img src="https://discordapp.com/api/guilds/1158447623989116980/embed.png?style=banner2"></a>
