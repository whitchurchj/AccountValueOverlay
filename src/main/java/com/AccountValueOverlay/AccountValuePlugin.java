package com.AccountValueOverlay;

import com.google.inject.Provides;

import javax.inject.Inject;
import java.util.stream.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import java.util.Arrays;

@Slf4j
@PluginDescriptor(
        name = "Account Value Overlay",
        description = "Adds an overlay showing your overall account value"
)
public class AccountValuePlugin extends Plugin {
    private long[] GeSlots = new long[8];

    private boolean IsBankSynced;
    @Inject
    private ConfigManager configManager;
    @Inject
    private Client client;
    @Inject
    ClientThread clientThread;
    @Inject
    private ItemManager itemManager;
    @Inject
    private AccountValueConfig config;
    @Inject
    private ChatCommandManager commandManager;
    @Inject
    private AccountValueOverlay overlay;
    @Inject
    private OverlayManager overlayManager;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.BANK.getId()) {
            if (!overlay.IsBankSynced()) {
                overlay.UpdateBankSynced(true);
            }
            final Item[] items = client.getItemContainer(InventoryID.BANK).getItems();
            updateTotalBankValue(items);
        }
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            final Item[] items = client.getItemContainer(InventoryID.INVENTORY).getItems();
            updateTotalInventoryValue(items);
        }
		/*if (event.getContainerId() == ComponentID.BANK_POTIONSTORE_CONTENT) {
			final Item[] items = client.getItemContainer(ComponentID.BANK_POTIONSTORE_CONTENT).getItems();
			updateTotalPotionValue(items);
		}*/
        if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
            final Item[] items = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
            updateTotalWornValue(items);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        overlay.UpdateGeButton(isCollectButtonVisible());
    }

    public long GetTotalGeValue() {
        return LongStream.of(GeSlots).sum();
    }

    public boolean isCollectButtonVisible() {
        Widget w = client.getWidget(InterfaceID.GRAND_EXCHANGE, 6);
        if (w == null) {
            return false;
        }
        Widget[] children = w.getChildren();
        if(children == null) {
            return false;
        }
        return Arrays.stream(children).anyMatch(c -> !c.isHidden() && "Collect".equals(c.getText()));
    }

    @Subscribe
    public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event) {
        final Item[] items;

        GeSlots[event.getSlot()] = ((long)event.getOffer().getTotalQuantity() * event.getOffer().getPrice()) - event.getOffer().getSpent();

        overlay.UpdateGeButton(isCollectButtonVisible());

        updateTotalGEValue(GetTotalGeValue());
    }

    private long GetValueOfAllItems(Item[] items) {
        long totalValue = 0;
        for (int i = 0; i < items.length; i++) {
            totalValue += (long) items[i].getQuantity() * itemManager.getItemPrice(items[i].getId());
        }
        return totalValue;
    }

    private void updateTotalBankValue(Item[] items) {
        overlay.updateBankValue(GetValueOfAllItems(items));
    }

    private void updateTotalInventoryValue(Item[] items) {
        overlay.updateInventoryValue(GetValueOfAllItems(items));
    }

    private void updateTotalPotionValue(Item[] items) {
        overlay.updatePotionValue(GetValueOfAllItems(items));
    }

    private void updateTotalGEValue(long value) {
        overlay.updateGeValue(value);
    }

    private void updateTotalWornValue(Item[] items) {
        overlay.updateWornValue(GetValueOfAllItems(items));
    }

    private int getHaValue(int itemId) {
        switch (itemId) {
            case ItemID.COINS_995:
                return 1;
            case ItemID.PLATINUM_TOKEN:
                return 1000;
            default:
                return itemManager.getItemComposition(itemId).getHaPrice();
        }
    }


    @Provides
    AccountValueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AccountValueConfig.class);
    }


}
