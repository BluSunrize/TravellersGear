package travellersgear;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import travellersgear.common.CommonProxy;
import travellersgear.common.blocks.BlockArmorStand;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.items.ItemTravellersGear;
import travellersgear.common.network.TGPacketPipeline;
import travellersgear.common.util.CloakColourizationRecipe;
import travellersgear.common.util.ComparableItemStack;
import travellersgear.common.util.TGCreativeTab;
import travellersgear.common.util.TGEventHandler;
import travellersgear.common.util.Utils;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = TravellersGear.MODID, name = TravellersGear.MODNAME, version = TravellersGear.VERSION)
public class TravellersGear
{
	public static final String MODID = "TravellersGear";
	public static final String MODNAME = "Traveller's Gear";
	public static final String VERSION = "1.6";
	public static final Logger logger = LogManager.getLogger(MODID);
	public final TGPacketPipeline packetPipeline = new TGPacketPipeline();

	@Instance(MODID)
	public static TravellersGear instance = new TravellersGear();	

	@SidedProxy(clientSide="travellersgear.client.ClientProxy", serverSide="travellersgear.common.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		initItems();
		proxy.preInit(event);
	}
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		BAUBLES = Loader.isModLoaded("Baubles");
		MARI = Loader.isModLoaded("Mariculture");
		TCON = Loader.isModLoaded("TConstruct");
		THAUM = Loader.isModLoaded("Thaumcraft");
		NEI = Loader.isModLoaded("NotEnoughItems");

		GameRegistry.addRecipe(new CloakColourizationRecipe());

		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new ItemTravellersGear.WeightedRandomTitleScroll());
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new ItemTravellersGear.WeightedRandomTitleScroll());
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new ItemTravellersGear.WeightedRandomTitleScroll());
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new ItemTravellersGear.WeightedRandomTitleScroll());
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(new ItemTravellersGear.WeightedRandomTitleScroll());

		int[] dyeColours = {0xffffff, 0xD87F33, 0xB24CD8 , 0x6699D8   , 0xE5E533, 0x7FCC19, 0xF27FA5, 0x4C4C4C, 0x999999   , 0x4C7F99, 0x7F3FB2, 0x334CB2, 0x664C33, 0x667F33, 0x993333, 0x191919};
		for(int d=0;d<dyeColours.length;d++)
			GameRegistry.addRecipe(new ShapedOreRecipe(Utils.getColouredItem(new ItemStack(simpleGear,1,0),dyeColours[d]), " s ","www","www", 's',Items.string, 'w',new ItemStack(Blocks.wool,1,d)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(simpleGear,1,2), "gg ","g g"," gg", 'g',"nuggetGold"));
		if(!OreDictionary.getOres("nuggetSilver").isEmpty())
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(simpleGear,1,3), "ss ","s s"," ss", 's',"nuggetSilver"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(simpleGear,1,1), " l ","l l"," i ", 'i',"ingotIron", 'l',Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(simpleGear,1,4), "ll ","ill"," i ", 'i',(!OreDictionary.getOres("nuggetIron").isEmpty()?"nuggetIron":"ingotIron"), 'l',Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(simpleGear,1,5), " l ","lil"," l ", 'i',(!OreDictionary.getOres("nuggetIron").isEmpty()?"nuggetIron":"ingotIron"), 'l',Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(simpleGear,1,6), " l ","pbp"," l ", 'b',Items.enchanted_book, 'p',Items.paper, 'l',"gemLapis"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(armorStand), "sfs"," f ","ppp", 'p',"slabWood", 'f',Blocks.fence, 's',"stickWood"));

		OreDictionary.registerOre("baubleRingGold",new ItemStack(simpleGear,1,2));
		OreDictionary.registerOre("baubleRingSilver",new ItemStack(simpleGear,1,3));
		OreDictionary.registerOre("baubleBeltIron",new ItemStack(simpleGear,1,1));
		OreDictionary.registerOre("travelgearCloakBase",new ItemStack(simpleGear,1,0));

		if(THAUM)
		{
			Item blankBauble = GameRegistry.findItem("Thaumcraft", "ItemBaubleBlanks");
			if(blankBauble!=null)
			{
				OreDictionary.registerOre("baubleAmuletGold",new ItemStack(blankBauble,1,0));
				OreDictionary.registerOre("baubleRingGold",new ItemStack(blankBauble,1,1));
				OreDictionary.registerOre("baubleBeltGold",new ItemStack(blankBauble,1,2));
			}
		}

		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		MinecraftForge.EVENT_BUS.register(new TGEventHandler());
		FMLCommonHandler.instance().bus().register(new TGEventHandler());
		packetPipeline.initialise();
	}
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		packetPipeline.postInitialise();

		ImmutableList<FMLInterModComms.IMCMessage> messages = FMLInterModComms.fetchRuntimeMessages(this);
		for (FMLInterModComms.IMCMessage message : messages)
		{
			if(message.key.startsWith("registerTravellersGear_"))
			{
				ItemStack stack = message.getItemStackValue();
				int slot = Integer.parseInt(message.key.substring("registerTravellersGear_".length()));
				addItemToTravelersGear(stack,slot);
			}
		}
	}
	
	public static HashMap<ComparableItemStack, Object[]> additionalTravelersGear = new HashMap();
	
	public static boolean BAUBLES;
	public static boolean MARI;
	public static boolean TCON;
	public static boolean THAUM;
	public static boolean NEI;

	public static ItemTravellersGear simpleGear;
	public static Block armorStand;
	static void initItems()
	{
		simpleGear = (ItemTravellersGear) new ItemTravellersGear().setUnlocalizedName("TravellersGear.simpleGear");
		GameRegistry.registerItem(simpleGear, "simpleGear");
		armorStand = new BlockArmorStand().setBlockName("TravellersGear.armorstand");
		GameRegistry.registerBlock(armorStand, "armorstand");
		GameRegistry.registerTileEntity(TileEntityArmorStand.class, "TravellersGear.ArmorStand");
	}
	static void addItemToTravelersGear(ItemStack stack, int slot)
	{
		Object[] data = new Object[4];
		data[0] = slot;
		try{
			data[1] = stack.getItem().getClass().getMethod("onTravelGearTick", EntityPlayer.class,ItemStack.class);
			data[2] = stack.getItem().getClass().getMethod("onTravelGearEquip", EntityPlayer.class,ItemStack.class);
			data[3] = stack.getItem().getClass().getMethod("onTravelGearUnequip", EntityPlayer.class,ItemStack.class);
		}catch(Exception e){}
		additionalTravelersGear.put(new ComparableItemStack(stack), data);
	}

	public static CreativeTabs creativeTab = new TGCreativeTab();
}