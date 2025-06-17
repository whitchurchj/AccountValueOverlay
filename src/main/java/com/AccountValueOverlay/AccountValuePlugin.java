package com.AccountValueOverlay;

import com.google.inject.Provides;
import javax.inject.Inject;

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

@Slf4j
@PluginDescriptor(
		name = "Account Value Overlay",
		description = "Adds an overlay showing your overall account value"
)
public class AccountValuePlugin extends Plugin
{
	private long TotalGeValue = 0;

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
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event){
		if (event.getContainerId() == InventoryID.BANK.getId()) {
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
		//if (event.getItemContainer() == client.getItemContainer(InventoryID.BANK)) {
		//	final Item[] items = client.getItemContainer(InventoryID.BANK).getItems();
		//	updateTotalGEValue(items);
		//}
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
			final Item[] items = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
			updateTotalWornValue(items);
		}
	}
/*
	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event){
			final Item[] bankItems = client.getItemContainer(InventoryID.BANK).getItems();
			updateTotalBankValue(bankItems);
			final Item[] invItems = client.getItemContainer(InventoryID.INVENTORY).getItems();
			updateTotalInventoryValue(invItems);

 			if (event.getOffer().getState() == GrandExchangeOfferState.BOUGHT){
				TotalGeValue += (long)event.getOffer().getPrice() * event.getOffer().getQuantitySold();
			} else if (event.getOffer().getState() == GrandExchangeOfferState.SOLD){

			} else if (event.getOffer().getState() == GrandExchangeOfferState.CANCELLED_BUY){

			} else if (event.getOffer().getState() == GrandExchangeOfferState.CANCELLED_SELL){

			} else if (event.getOffer().getState() == GrandExchangeOfferState.BUYING){

			} else if (event.getOffer().getState() == GrandExchangeOfferState.SELLING){

			}

			final Item[] items = client.getItemContainer(InventoryID.BANK).getItems();
			updateTotalGEValue(items);

	}
*/
	private long GetValueOfAllItems(Item[] items){
		long totalValue = 0;
		for (int i = 0; i < items.length; i++){
			totalValue += (long)items[i].getQuantity() * itemManager.getItemPrice(items[i].getId());
		}
		return totalValue;
	}

	private void updateTotalBankValue(Item[] items){
		overlay.updateBankValue(GetValueOfAllItems(items));
	}

	private void updateTotalInventoryValue(Item[] items){
		overlay.updateInventoryValue(GetValueOfAllItems(items));
	}

	private void updateTotalPotionValue(Item[] items){
		overlay.updatePotionValue(GetValueOfAllItems(items));
	}

	private void updateTotalGEValue(Item[] items){
		overlay.updateGeValue(GetValueOfAllItems(items));
	}

	private void updateTotalWornValue(Item[] items){
		overlay.updateWornValue(GetValueOfAllItems(items));
	}

	/*BankedItems calc(Item[] items) {
		long geTotal = 0;
		for (Item item : client.getItemContainer(InventoryID.BANK).getItems()) {
			final boolean isPlaceholder = itemManager.getItemComposition(item.getId()).getPlaceholderTemplateId() != -1;

			if (item.getId() != ItemID.BANK_FILLER || !isPlaceholder) {
				String name = itemManager.getItemComposition(item.getId()).getName();
				final int qty = item.getQuantity();
				geTotal += itemManager.getItemPrice(item.getId()) * qty;
			}
		}
		return new BankedItems(geTotal);
	}*/

	private int getHaValue(int itemId)
	{
		switch (itemId)
		{
			case ItemID.COINS_995:
				return 1;
			case ItemID.PLATINUM_TOKEN:
				return 1000;
			default:
				return itemManager.getItemComposition(itemId).getHaPrice();
		}
	}





	@Provides
	AccountValueConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AccountValueConfig.class);
	}


	}
