package io.github.cottonmc.gui.client;

import java.io.IOException;

import io.github.cottonmc.gui.CottonScreenController;
import io.github.cottonmc.gui.widget.WPanel;
import io.github.cottonmc.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Nameable;

public class CottonScreen<T extends CottonScreenController> extends ContainerScreen<T> {
	protected CottonScreenController container;
	public static final int PADDING = 8;
	protected WWidget lastResponder = null;
	
	public CottonScreen(T container, PlayerEntity player) {
		super(container, player.inventory, new TranslatableTextComponent(""));
		this.container = container;
		super.width = 18*9;
		super.height = 18*9;
		this.width = 18*9;
		this.height = 18*9;
	}
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "containerWidth" and "containerHeight" are the width and height of the panel to render
	 * * "left" and "top" are *actually* self-explanatory
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	
	/*
	 * These methods are called frequently and empty, meaning they're probably *meant* for subclasses to override to
	 * provide core GUI functionality.
	 */
	
	
	@Override
	public void initialize(MinecraftClient minecraftClient_1, int screenWidth, int screenHeight) {
		//container.validate();
		super.initialize(minecraftClient_1, width, height);
		
		WPanel basePanel = container.getRootPanel();
		if (basePanel!=null) {
			width = basePanel.getWidth();
			height = basePanel.getHeight();
		}
		
		left = (screenWidth  / 2) - (width / 2);
		top =  (screenHeight / 2) - (height / 2);
	}
	
	//Will probably re-activate for animation!
	//@Override
	//public void updateScreen() {
	//	System.out.println("updateScreen");
	//}
	
	@Override
	public void onClosed() {
		super.onClosed();
	}
	
	@Override
	public boolean isPauseScreen() {
		//...yeah, we're going to go ahead and override that.
		return false;
	}
	
	/*
	 * While these methods are implemented in GuiScreen, chances are we'll be shadowing a lot of the GuiScreen methods
	 * in order to implement our own button protocol and more advanced features.
	 */
	
	@Override
	public boolean keyReleased(int int_1, int int_2, int int_3) {
		// TODO Auto-generated method stub
		return super.keyReleased(int_1, int_2, int_3);
	}
	
	
	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if (MinecraftClient.getInstance().options.keyInventory.matchesKey(keyCode, keyCode));
		
		return super.charTyped(typedChar, keyCode);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		//lastResponder = container.doMouseDown(containerX, containerY, mouseButton);
		return result;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) { //Testing shows that STATE IS ACTUALLY BUTTON
		boolean result = super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		/*
		WWidget responder = container.doMouseUp(containerX, containerY, mouseButton);
		if (responder!=null && responder==lastResponder) container.doClick(containerX, containerY, mouseButton);*/
		lastResponder = null;
		return result;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double unknown_1, double unknown_2) {
		boolean result = super.mouseDragged(mouseX, mouseY, mouseButton, unknown_1, unknown_2);
		
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		//container.doMouseDrag(containerX, containerY, mouseButton);
		return result;
	}
	
	/*
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}*/
	
	/*
	 * We'll probably wind up calling some of this manually, but they do useful things for us so we may leave
	 * them unharmed.
	 */
	/*
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		
		WPanel basePanel = container.getRootPanel();
		if (basePanel!=null) {
			xSize = basePanel.getWidth();
			ySize = basePanel.getHeight();
		}
		left = (width  / 2) - (xSize / 2);
		top =  (height / 2) - (ySize / 2);
		
	}
	*/
	
	
	/*
	 * SPECIAL FUNCTIONS: Where possible, we want to draw everything based on *actual GUI state and composition* rather
	 * than relying on pre-baked textures that the programmer then needs to carefully match up their GUI to.
	 */
	
	private int multiplyColor(int color, float amount) {
		int a = color & 0xFF000000;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8  & 255) / 255.0F;
		float b = (color       & 255) / 255.0F;
		
		r = Math.min(r*amount, 1.0f);
		g = Math.min(g*amount, 1.0f);
		b = Math.min(b*amount, 1.0f);
		
		int ir = (int)(r*255);
		int ig = (int)(g*255);
		int ib = (int)(b*255);
		
		return    a |
				(ir << 16) |
				(ig <<  8) |
				 ib;
	}
	
	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		WPanel root = this.container.getRootPanel();
		if (root==null) return;
		
		if (container.shouldDrawPanel()) {
			int panelColor = container.getColor();
			int shadowColor = multiplyColor(panelColor, 0.50f);
			int hilightColor = multiplyColor(panelColor, 1.25f);
			
			ScreenDrawing.drawGuiPanel(
					left - PADDING,
					top - PADDING,
					width + ((PADDING - 1) * 2),
					height + ((PADDING - 1) * 2),

					shadowColor, panelColor, hilightColor, 0xFF000000);
		}
		
		if (this.cursorDragSlots != null && root != null) {
			root.paintBackground(left, top);
		}
		
		//TODO: Change this to a label that lives in the rootPanel instead?
		if (container instanceof Nameable) {
			TextComponent name = ((Nameable)container).getDisplayName();
			fontRenderer.draw(name.getFormattedText(), left, top, container.getTitleColor());
		}
	}
	
	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		if (cursorDragSlots != null && this.container.getRootPanel() != null) {
			this.container.getRootPanel().paintForeground(left, top, mouseX, mouseY);
		}
	}
	
	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(partialTicks, mouseX, mouseY);
		
		super.draw(mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(mouseX, mouseY);
	}
	
}
