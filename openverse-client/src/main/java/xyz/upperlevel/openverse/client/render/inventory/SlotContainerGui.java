package xyz.upperlevel.openverse.client.render.inventory;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.ulge.gui.Gui;

public class SlotContainerGui extends Gui {
    private SlotGui[] slots;

    @Getter
    private final int horizontalSlots;
    @Getter
    private final int verticalSlots;
    @Getter
    @Setter
    private int slotPadding = 10;

    public SlotContainerGui(int horizontalSlots, int verticalSlots) {
        this.horizontalSlots = horizontalSlots;
        this.verticalSlots = verticalSlots;
        slots = new SlotGui[horizontalSlots * verticalSlots];
    }


    @Override
    public void reloadLayout(int parX, int parY, int parW, int parH) {
        super.reloadLayout(parX, parY, parW, parH);
        int slotWidth = getWidth() / horizontalSlots - slotPadding * 2;
        int slotHeight = getHeight() / verticalSlots - slotPadding * 2;
        // Left here for debugging purposes
        /*System.out.println("PARE SIZE: {" + parX + ", " + parY + ", " + parW + ", " + parH + "}");
        System.out.println("THIS SIZE: {" + getBounds() + "} -> w: " + getWidth() + ", h: " + getHeight());
        System.out.println("SLOT SIZE: {" + slotWidth + ", " + slotHeight + "}");*/
        for (SlotGui slot : slots) {
            if (slot != null) {
                slot.setSize(slotWidth, slotHeight);
            }
        }

        for (int x = 0; x < horizontalSlots; x++) {
            for (int y = 0; y < verticalSlots; y++) {
                int i = index(x, y);
                if (slots[i] == null) continue;
                slots[i].reloadLayout(
                        getRealX() + slotPadding + x * (slotPadding * 2 + slotWidth),
                        getRealY() + slotPadding + y * (slotPadding * 2 + slotHeight),
                        slotWidth,
                        slotHeight
                );
            }
        }
    }

    public void setSlot(int x, int y, SlotGui slot) {
        int index = index(x, y);
        if (slots[index] != null) {
            removeChild(slots[index]);
        }
        if (slot != null) {
            addChild(slot);
        }
        slots[index] = slot;
    }

    public SlotGui getSlot(int x, int y) {
        return slots[index(x, y)];
    }

    public int getCapacity() {
        return slots.length;
    }

    private int index(int x, int y) {
        return y * horizontalSlots + x;
    }
}
