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
import net.minecraft.block.material.Material;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ComponentBreathing implements IArtifactComponent {

	public String getRandomTrigger(Random rand, boolean isArmor) {
		if(isArmor) {
			switch(rand.nextInt(2)) {
				case 0:
					return "onArmorTickUpdate";
				case 1:
					return "onTakeDamage";
			}
			return "";
		}
		String str = "";
		switch(rand.nextInt(5)) {
			case 0:
			case 1:
				str = "onItemRightClick";
				break;
			case 2:
			case 3:
				str = "hitEntity";
				break;
			case 4:
				str = "onHeld";
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
			out.writeInt(10);
			out.writeInt(par3EntityPlayer.entityId);
			out.writeInt(13);
			out.writeInt(1200);
			out.writeInt(0);
			Packet250CustomPayload packet = new Packet250CustomPayload("Artifacts", bt.toByteArray());
			PacketDispatcher.sendPacketToServer(packet);
			par1ItemStack.damageItem(1, par3EntityPlayer);
			par1ItemStack.stackTagCompound.setInteger("onItemRightClickDelay", 200);
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
			if(par2EntityLivingBase.hurtTime == 0) {
				ByteArrayOutputStream bt = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(bt);
				try
				{
					out.writeInt(10);
					out.writeInt(par3EntityLivingBase.entityId);
					out.writeInt(13);
					out.writeInt(300);
					out.writeInt(0);
					EntityPlayer player = (EntityPlayer)par3EntityLivingBase;
					Packet250CustomPayload packet = new Packet250CustomPayload("Artifacts", bt.toByteArray());
					PacketDispatcher.sendPacketToServer(packet);
				}
				catch (IOException ex)
				{
					System.out.println("couldnt send packet!");
				}
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
		return false;
	}

	//works great
	@Override
	public void onUpdate(ItemStack par1ItemStack, World world, Entity par3Entity, int par4, boolean par5) {
		
	}
	
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, String trigger, boolean advTooltip) {
		int time = 0;
		if(trigger.equals("when inflicting damage.")) {
			time = 15;
		}
		else if(trigger.equals("when used.")) {
			time = 60;
		}
		else if(trigger.equals("after taking damage.")) {
			trigger = "after taking drowning damage.";
			time = 2;
		}
		par3List.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("effect.Water Breathing"));
		par3List.add("  " + EnumChatFormatting.AQUA + StatCollector.translateToLocal("tool."+trigger) + " (" + time + StatCollector.translateToLocal("time.seconds") + ")");
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean advTooltip) {
		par3List.add(EnumChatFormatting.AQUA + "Water Breathing");
	}

	@Override
	public String getPreAdj(Random rand) {
		String str = "";
		switch(rand.nextInt(3)) {
			case 0:
				str = "Aerated";
				break;
			case 1:
				str = "Breathy";
				break;
			case 2:
				str = "Oxygenated";
				break;
		}
		return str;
	}

	@Override
	public String getPostAdj(Random rand) {
		String str = "";
		switch(rand.nextInt(2)) {
			case 0:
				str = "of Breathing";
				break;
			case 1:
				str = "of Fresh Air";
				break;
		}
		return str;
	}

	@Override
	public int getTextureBitflags() {
		return 2397;
	}

	@Override
	public int getNegTextureBitflags() {
		return 4738;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem, String type) {
		return false;
	}

	@Override
	public void onHeld(ItemStack par1ItemStack, World par2World,Entity par3Entity, int par4, boolean par5) {
		//onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		if(!par2World.isRemote) {
			if(par3Entity instanceof EntityLivingBase) {
				EntityLivingBase ent = (EntityLivingBase) par3Entity;
				ent.addPotionEffect(new PotionEffect(13, 10, 0));
			}
		}
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack, boolean worn) {
		if(worn)
			onHeld(itemStack, world, player, 0, true);
	}

	@Override
	public void onTakeDamage(ItemStack itemStack, LivingHurtEvent event, boolean isWornArmor) {
		if(isWornArmor && event.source == DamageSource.drown) {
			event.entityLiving.addPotionEffect(new PotionEffect(13, 40, 0));
		}
	}

	@Override
	public void onDeath(ItemStack itemStack, LivingDeathEvent event, boolean isWornArmor) {	}
}
