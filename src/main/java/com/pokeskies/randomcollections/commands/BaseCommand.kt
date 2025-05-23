package com.pokeskies.randomcollections.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.commands.subcommands.DebugCommand
import com.pokeskies.randomcollections.commands.subcommands.ReloadCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class BaseCommand {
    private val aliases = listOf("randomcollections", "rc")

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val rootCommands: List<LiteralCommandNode<CommandSourceStack>> = aliases.map {
            Commands.literal(it)
                .requires(Permissions.require("randomcollections.command.base", 2))
                .then(Commands.argument("collection", StringArgumentType.string())
                    .suggests { _, builder ->
                        SharedSuggestionProvider.suggest(RandomCollections.INSTANCE.collections.keys.stream(), builder)
                    }
                    .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes { ctx ->
                                execute(
                                    ctx,
                                    StringArgumentType.getString(ctx, "collection"),
                                    EntityArgument.getPlayers(ctx, "players"),
                                    IntegerArgumentType.getInteger(ctx, "amount")
                                )
                            }
                        )
                        .executes { ctx ->
                            execute(
                                ctx,
                                StringArgumentType.getString(ctx, "collection"),
                                EntityArgument.getPlayers(ctx, "players")
                            )
                        }
                    )
                    .executes { ctx ->
                        execute(
                            ctx,
                            StringArgumentType.getString(ctx, "collection"),
                            listOf(ctx.source.playerOrException)
                        )
                    }
                )
                .build()
        }

        val subCommands: List<LiteralCommandNode<CommandSourceStack>> = listOf(
            DebugCommand().build(),
            ReloadCommand().build(),
        )

        rootCommands.forEach { root ->
            subCommands.forEach { sub -> root.addChild(sub) }
            dispatcher.root.addChild(root)
        }
    }

    companion object {
        fun execute(
            ctx: CommandContext<CommandSourceStack>,
            collection: String,
            players: Collection<ServerPlayer>,
            amount: Int = 1
        ): Int {
            val rc = RandomCollections.INSTANCE.collections[collection]
            if (rc == null) {
                ctx.source.sendSystemMessage(Component.literal("Random Collection '$collection' was not found!")
                    .withStyle { it.withColor(ChatFormatting.RED) })
                return 1
            }

            players.forEach { player ->
                for (i in 0 until amount) {
                    val reward = rc.next()
                    reward.giveReward(player)
                }
            }

            ctx.source.sendSystemMessage(Component.literal("Gave $amount rewards to ${players.size} players from collection '$collection'")
                .withStyle { it.withColor(ChatFormatting.GREEN) })

            return 1
        }
    }
}
