package travellersgear.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import travellersgear.common.blocks.TileEntityArmorStand;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockRenderArmorStand implements ISimpleBlockRenderingHandler
{
	public static int renderID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		GL11.glPushMatrix();
		try{
			GL11.glScaled(.6875,.6875,.6875);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -.875F, -0.5F);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityArmorStand(), 0.0D, 0.0D, 0.0D, 0.0F);
		}catch(Exception e)
		{
			//						e.printStackTrace();
			//			System.out.println("Yay for critical errors >_>");
			GL11.glPopMatrix();
		}
		GL11.glEnable(32826);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelID)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return renderID;
	}

}