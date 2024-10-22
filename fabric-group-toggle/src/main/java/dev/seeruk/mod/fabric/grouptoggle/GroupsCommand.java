package dev.seeruk.mod.fabric.grouptoggle;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RequiredArgsConstructor
public class GroupsCommand {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(literal("groups")
                .executes(GroupsCommand::onGroupsCommand)
                .then(literal("toggle")
                    .then(argument("groupName", StringArgumentType.string())
                        .executes(GroupsCommand::onGroupsToggleCommand)
                    )
                )
            );
        });
    }

    private static int onGroupsCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();

        var mod = GroupToggleMod.getInstance();
        var luckPerms = LuckPermsProvider.get();

        var audience = mod.getAdventure().audience(source).audience();
        var user = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);

        var allGroups = mod.getConfig().groups;

        var userGroups = user.getNodes(NodeType.INHERITANCE).stream()
            .map(InheritanceNode::getGroupName)
            .collect(Collectors.toSet());

        var response = new StringBuilder();

        response.append("<bold>Your Available Groups:</bold> <gray>(click to toggle)</gray>");

        allGroups.stream().sorted().forEach((allowedGroup) -> {
            var isInGroup = userGroups.contains(allowedGroup);
            var colour = isInGroup ? "green" : "red";
            var signal = isInGroup ? "✔" : "✘";
            response.append(String.format(
                "<br><%1$s>%2$s <click:run_command:'/groups toggle %3$s'><hover:show_text:'Toggle %3$s'>%3$s</hover></click><%1$s>",
                colour,
                signal,
                allowedGroup
            ));
        });

        audience.sendMessage(miniMessage.deserialize(response.toString()));

        return 1;
    }

    private static int onGroupsToggleCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();

        var mod = GroupToggleMod.getInstance();

        var audience = mod.getAdventure().audience(source).audience();
        var allGroups = mod.getConfig().groups;
        var chosenGroup = StringArgumentType.getString(context, "groupName");

        if (!allGroups.contains(chosenGroup)) {
            var message = new StringBuilder();

            message.append("<red>Group not found. Run ");
            message.append("<gold>");
            message.append("<click:suggest_command:/groups>");
            message.append("<hover:show_text:'Suggested command: /groups'>");
            message.append("/groups");
            message.append("</hover>");
            message.append("</click>");
            message.append("</gold>");
            message.append(" to list available groups</red>");

            audience.sendMessage(miniMessage.deserialize(message.toString()));
            return 0;
        }

        var luckPerms = LuckPermsProvider.get();

        var user = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);
        var userGroups = user.getNodes(NodeType.INHERITANCE).stream()
            .map(InheritanceNode::getGroupName)
            .collect(Collectors.toSet());

        var node = Node.builder("group." + chosenGroup).build();

        if (userGroups.contains(chosenGroup)) {
            user.data().remove(node);
        } else {
            user.data().add(node);
        }

        luckPerms.getUserManager().saveUser(user);

        // Show the updated groups using the top-level groups command
        onGroupsCommand(context);

        return 1;
    }
}
