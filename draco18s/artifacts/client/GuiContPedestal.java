package draco18s.artifacts.client;

import org.lwjgl.opengl.GL11;

import draco18s.artifacts.entity.TileEntityDisplayPedestal;
import draco18s.artifacts.inventory.ContainerPedestal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiContPedestal extends GuiContainer {

	public GuiContPedestal(Container par1Container) {
		super(par1Container);
	}

	public GuiContPedestal (InventoryPlayer inventoryPlayer, TileEntityDisplayPedestal tileEntity) {
		//the container is instanciated and passed to the superclass for handling
		super(new ContainerPedestal(inventoryPlayer, tileEntity));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		//draw text and stuff here
		//the parameters for drawString are: string, x, y, color
		fontRenderer.drawString("Display Pedestal", 8, 6, 4210752);
		//draws "Inventory" or your regional equivalent
		fontRenderer.drawString(StatCollector.translateToLocal("Inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		//draw your Gui here, only thing you need to change is the path
		ResourceLocation texture = new ResourceLocation("artifacts:textures/gui/pedestal.png");
		this.mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
