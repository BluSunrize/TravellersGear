package travellersgear.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import travellersgear.api.ITravellersGear;
import travellersgear.common.items.ItemTravellersGear;

public class ModelSimpleGear extends ModelBiped
{
	public ModelSimpleGear(EntityLivingBase entity, ItemStack stack)
	{
		super(.01F, 0, 64, 32);
		this.bipedBody.isHidden=true;
		this.bipedHead.isHidden=true;
		this.bipedHeadwear.isHidden=true;
		this.bipedLeftLeg.isHidden=true;
		this.bipedRightLeg.isHidden=true;

		int slot = ((ITravellersGear)stack.getItem()).getSlot(stack);
		float sizeMod = slot==1?.5f:.125f;
		if(slot==1 && entity.getEquipmentInSlot(3)!=null)
			sizeMod += .625f;

		int u = slot==1||slot==2?40: 0;
		int v = slot==1?16: 24;
		int yOff = slot==2?7:0;

		this.boxList.clear();
		if(slot>0)
		{
			this.bipedRightArm = new ModelRenderer(this, u, v);
			this.bipedRightArm.addBox(-3.0F, -2.0F+yOff, -2.0F, 4, 4, 4, sizeMod);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.bipedLeftArm = new ModelRenderer(this, u, v);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F+yOff, -2.0F, 4, 4, 4, sizeMod);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		}
		else
		{
			this.bipedBody = new ModelRenderer(this, u, v);
			this.bipedBody.addBox(-4.0F, 8.5F, -2.0F, 8, 4, 4, sizeMod);
			this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		}
	}

	static ModelBiped[] modelMap = new ModelBiped[ItemTravellersGear.subNames.length];
	public static ModelBiped getModel(EntityLivingBase entity, ItemStack stack)
	{
		if(stack==null || !(stack.getItem() instanceof ITravellersGear))
			return null;
		int slot = ((ITravellersGear)stack.getItem()).getSlot(stack);
		if(slot==0)
			return new ModelCloak(stack.getItem().getColorFromItemStack(stack, 0));
		//		if(modelMap[stack.getItemDamage()]==null)
		//			modelMap[stack.getItemDamage()] =
		return new ModelSimpleGear(entity,stack);
		//		return modelMap[stack.getItemDamage()];
	}
}