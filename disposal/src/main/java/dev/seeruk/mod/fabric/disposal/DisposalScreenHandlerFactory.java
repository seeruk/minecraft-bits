package dev.seeruk.mod.fabric.disposal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.Text;

public class DisposalScreenHandlerFactory implements ScreenHandlerFactory, NamedScreenHandlerFactory {
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, player.getInventory(), new SimpleInventory(27));
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Disposal");
    }
}
