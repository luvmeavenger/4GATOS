package com.gatos.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FourGatos implements ClientModInitializer {
    public static boolean fly = false;
    public static boolean esp = false;
    public static boolean autoTotem = true;
    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        // 'X' Key for Menu
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("4Gatos Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "4Gatos"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (menuKey.wasPressed()) client.setScreen(new GatosMenu());

            // 1. Flight
            client.player.getAbilities().allowFlying = fly;

            // 2. ESP (Simple Glowing)
            for (Entity entity : client.world.getEntities()) {
                if (entity != client.player) {
                    entity.setGlowing(esp);
                }
            }

            // 3. Auto-Totem
            if (autoTotem && client.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                for (int i = 0; i < 36; i++) {
                    if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i < 9 ? i + 36 : i, 45, SlotActionType.SWAP, client.player);
                        break;
                    }
                }
            }
        });
    }

    public static class GatosMenu extends Screen {
        public GatosMenu() { super(Text.literal("4Gatos Menu")); }

        @Override
        protected void init() {
            int x = this.width / 2 - 75;
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Fly: " + (fly ? "ON" : "OFF")), b -> {
                fly = !fly;
                this.clearAndInit();
            }).dimensions(x, 40, 150, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.literal("ESP: " + (esp ? "ON" : "OFF")), b -> {
                esp = !esp;
                this.clearAndInit();
            }).dimensions(x, 70, 150, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.literal("Auto-Totem: " + (autoTotem ? "ON" : "OFF")), b -> {
                autoTotem = !autoTotem;
                this.clearAndInit();
            }).dimensions(x, 100, 150, 20).build());
        }

        @Override
        public boolean shouldPause() { return false; }
    }
}
