package com.AccountValueOverlay;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.*;

public class AccountValueOverlay extends Overlay {
    private final AccountValueConfig config;
    private long BankValue;
    private long PotionValue;
    private long ProjectedGeValue;
    private long InvValue;
    private long GeValue;
    private long WornValue;
    private boolean isCollectButtonVis;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public AccountValueOverlay(AccountValueConfig config) {
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        String bankValueTxt = formatNumber(BankValue);
        String potionValueTxt = formatNumber(PotionValue);
        String invValueTxt = formatNumber(InvValue);
        String geValueTxt = formatNumber(GeValue);
        String projectedGeValueTxt = formatNumber(ProjectedGeValue);
        String wornValueTxt = formatNumber(WornValue);
        String totalValueTxt = formatNumber(BankValue + InvValue + GeValue + WornValue);
        panelComponent.setPreferredSize(new Dimension(
                BankValue + InvValue + GeValue + WornValue == 0 ? 150 : graphics.getFontMetrics().stringWidth(QuantityFormatter.formatNumber(BankValue + InvValue + GeValue + WornValue)) + 100,
                0));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Account Value")
                .color(Color.GREEN)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Total:")
                .right(totalValueTxt)
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Stored")
                .color(Color.GREEN)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Bank:")
                .right(bankValueTxt)
                .build());
        if (isCollectButtonVis) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("GE (COLLECT):")
                    .leftColor(Color.RED)
                    .right(geValueTxt)
                    .build());
        } else {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("GE:")
                    .leftColor(Color.WHITE)
                    .right(geValueTxt)
                    .build());
        }
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Character")
                .color(Color.GREEN)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Inventory:")
                .right(invValueTxt)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Worn:")
                .right(wornValueTxt)
                .build());

        return panelComponent.render(graphics);
    }

    public void UpdateGeButton(boolean Value) {
        this.isCollectButtonVis = Value;
    }

    public void updateBankValue(long Value) {
        this.BankValue = Value;
    }

    public void updateInventoryValue(long Value) {
        this.InvValue = Value;
    }

    public void updatePotionValue(long Value) {
        this.PotionValue = Value;
    }

    public void updateGeValue(long Value) {
        this.GeValue = Value;
    }

    public void updateWornValue(long Value) {
        this.WornValue = Value;
    }

    private String formatNumber(long num) {

        return config.showPriceSuffix() ? QuantityFormatter.quantityToStackSize(num) : QuantityFormatter.formatNumber(num);

    }
}
