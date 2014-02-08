package draco18s.artifacts.components;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import draco18s.artifacts.api.interfaces.IArtifactComponent;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ComponentLightning implements IArtifactComponent {

	public ComponentLightning() {
	}
	
	@Override
	public String getRandomTrigger(Random rand, boolean isArmor) {
		if(isArmor) return "";
		String str = "";
		switch(rand.nextInt(3)) {
			case 0:
				str = "itemInteractionForEntity";
				break;
			case 1:
				str = "hitEntity";
				break;
			case 2:
				str = "onItemRightClick";
				break;
			case 3:
				str = "onDroppedByPlayer";
				break;
		}
		return str;
	}

	@Override
	public ItemStack attached(ItemStack i, Random rand, int[] eff) {
		return i;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		return false;
	}

	@Override
	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
		return 0;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,	EntityPlayer par3EntityPlayer) {
		ByteArrayOutputStream bt = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bt);
		try
		{
			//System.out.println("Building packet...");
			out.writeInt(5);
			out.writeInt(par3EntityPlayer.entityId);
			out.writeInt(par3EntityPlayer.inventory.currentItem);
			//out.writeFloat(par3EntityPlayer.getHealth()+1);
			Packet250CustomPayload packet = new Packet250CustomPayload("Artifacts", bt.toByteArray());
			Player player = (Player)par3EntityPlayer;
			//System.out.println("Sending packet..." + player);
			PacketDispatcher.sendPacketToServer(packet);
			//par1ItemStack.damageItem(1, par3EntityPlayer);
			par1ItemStack.stackTagCompound.setInteger("onItemRightClickDelay", 5);
		}
		catch (IOException ex)
		{
			System.out.println("couldnt send packet!");
		}
		return par1ItemStack;
	}

	@Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
		if(par3EntityLivingBase.worldObj.isRemote) {
			ByteArrayOutputStream bt = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bt);
			try
			{
				if(par3EntityLivingBase instanceof Player) {
					//System.out.println("Building packet...");
					out.writeInt(5);
					out.writeInt(par3EntityLivingBase.entityId);
					EntityPlayer par3EntityPlayer = (EntityPlayer) par3EntityLivingBase;
					out.writeInt(par3EntityPlayer.inventory.currentItem);
					//out.writeFloat(par3EntityPlayer.getHealth()+1);
					Packet250CustomPayload packet = new Packet250CustomPayload("Artifacts", bt.toByteArray());
					Player player = (Player)par3EntityLivingBase;
					//System.out.println("Sending packet..." + player);
					PacketDispatcher.sendPacketToServer(packet);
					//par1ItemStack.damageItem(1, par3EntityLivingBase);
					return true;
				}
				System.out.println("hitEntity par3Entity is not Player");
				return false;
			}
			catch (IOException ex)
			{
				System.out.println("couldnt send packet!");
			}
		}
		return false;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLivingBase par7EntityLivingBase) {
		return false;
	}

	@Override
	public boolean canHarvestBlock(Block par1Block, ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, EntityLivingBase par3EntityLivingBase) {
		if(par2EntityPlayer.worldObj.isRemote) {
			ByteArrayOutputStream bt = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bt);
			try
			{
				//System.out.println("Building packet...");
				out.writeInt(5);
				out.writeInt(par2EntityPlayer.entityId);
				//EntityPlayer par3EntityPlayer = (EntityPlayer) par3EntityLivingBase;
				out.writeInt(par2EntityPlayer.inventory.currentItem);
				//out.writeFloat(par3EntityPlayer.getHealth()+1);
				Packet250CustomPayload packet = new Packet250CustomPayload("Artifacts", bt.toByteArray());
				//System.out.println("Sending packet..." + player);
				PacketDispatcher.sendPacketToServer(packet);
				//par1ItemStack.damageItem(1, par2EntityPlayer);
				return true;
			}
			catch (IOException ex)
			{
				System.out.println("couldnt send packet!");
			}
		}
		return false;
	}

	//works great
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		
	}
	
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, String trigger, boolean advTooltip) {
		par3List.add(StatCollector.translateToLocal("effect.Causes lightning") + " " + StatCollector.translateToLocal("tool."+trigger));
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean advTooltip) {
		par3List.add(StatCollector.translateToLocal("effect.Causes lightning"));
	}

	@Override
	public String getPreAdj(Random rand) {
		String str = "";
		switch(rand.nextInt(2)) {
			case 0:
				str = "Shocking";
				break;
			case 1:
				str = "Electric";
				break;
		}
		return str;
	}

	@Override
	public String getPostAdj(Random rand) {
		String str = "";
		switch(rand.nextInt(2)) {
			case 0:
				str = "of Thunder";
				break;
			case 1:
				str = "of Lightning";
				break;
		}
		return str;
	}

	@Override
	public int getTextureBitflags() {
		return 152;
	}

	@Override
	public int getNegTextureBitflags() {
		return 321;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem, String type) {
		return false;
	}

	@Override
	public void onHeld(ItemStack par1ItemStack, World par2World,Entity par3Entity, int par4, boolean par5) {
		
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack, boolean worn) { }

	@Override
	public void onTakeDamage(ItemStack itemStack, LivingHurtEvent event, boolean isWornArmor) {	}

	@Override
	public void onDeath(ItemStack itemStack, LivingDeathEvent event, boolean isWornArmor) {	}
}
