package travellersgear.client;

import net.minecraft.nbt.NBTTagCompound;

public class ToolDisplayInfo
{
	public int slot;
	public float[] translation;
	public float[] rotation;
	public float[] scale;
	public boolean rotateWithHead;
	public boolean hideWhenEquipped;

	public ToolDisplayInfo(int slot, float[] translation, float[] rotation, float[] scale)
	{
		this.slot=slot;
		this.translation=translation;
		this.rotation=rotation;
		this.scale = scale;
		rotateWithHead = false;
		hideWhenEquipped = false;
	}

	
	
	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("slot", slot);
		if(translation!=null&&translation.length>2)
		{
			tag.setFloat("x", translation[0]);
			tag.setFloat("y", translation[1]);
			tag.setFloat("z", translation[2]);
		}
		if(rotation!=null&&rotation.length>2)
		{
			tag.setFloat("rX", rotation[0]);
			tag.setFloat("rY", rotation[1]);
			tag.setFloat("rZ", rotation[2]);
		}
		if(scale!=null&&scale.length>2)
		{
			tag.setFloat("sX", scale[0]);
			tag.setFloat("sY", scale[1]);
			tag.setFloat("sZ", scale[2]);
		}
		tag.setBoolean("rotateWithHead", rotateWithHead);
		tag.setBoolean("hideWhenEquipped", hideWhenEquipped);
		return tag;
	}

	public static ToolDisplayInfo readFromNBT(NBTTagCompound tag)
	{
		int s = tag.getInteger("slot");
		float[] t = {tag.getFloat("x"),tag.getFloat("y"),tag.getFloat("z")};
		float[] r = {tag.getFloat("rX"),tag.getFloat("rY"),tag.getFloat("rZ")};
		float[] sc = {tag.getFloat("sX"),tag.getFloat("sY"),tag.getFloat("sZ")};
		boolean b0 = tag.getBoolean("rotateWithHead");
		boolean b1 = tag.getBoolean("hideWhenEquipped");
		ToolDisplayInfo tdi = new ToolDisplayInfo(s,t,r,sc);
		tdi.rotateWithHead=b0;
		tdi.hideWhenEquipped=b1;
		return tdi;
	}
}