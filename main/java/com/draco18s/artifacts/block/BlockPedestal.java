package com.draco18s.artifacts.block;

import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import com.draco18s.artifacts.DragonArtifacts;
import com.draco18s.artifacts.entity.TileEntityDisplayPedestal;
import com.draco18s.artifacts.network.PacketHandlerClient;
import com.draco18s.artifacts.network.SPacketGeneral;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlockPedestal extends BlockContainer {
	public static BlockContainer instance;

	public BlockPedestal() {
		super(Material.rock);
		setCreativeTab(DragonArtifacts.tabGeneral);
		setResistance(10F);
		setStepSound(Block.soundTypeGlass);
		setHardness(5.0F);
        setLightOpacity(0);
		setBlockBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 1.0F, 0.8125F);
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("artifacts:pedestal_icon");
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDisplayPedestal();
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack)
    {
		if(!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntityDisplayPedestal && entityLivingBase instanceof EntityPlayer) {
				TileEntityDisplayPedestal ted = (TileEntityDisplayPedestal)te;
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(entityLivingBase.getCommandSenderName());
				ted.owner = player.getPersistentID();

				try
				{
					PacketBuffer out = new PacketBuffer(Unpooled.buffer());
					out.writeInt(PacketHandlerClient.PEDESTAL);
					out.writeInt(x);
					out.writeInt(y);
					out.writeInt(z);
					out.writeLong(ted.owner.getLeastSignificantBits());
					out.writeLong(ted.owner.getMostSignificantBits());
					SPacketGeneral packet = new SPacketGeneral("Artifacts", out);
					DragonArtifacts.packetPipeline.sendToAllAround(packet, new TargetPoint(world.provider.dimensionId, x, y, z, 32));
				}
				catch (Exception ex)
				{
					System.out.println("couldnt send packet!");
					ex.printStackTrace();
				}	
				
				ted.rotation = ((int) ((MathHelper.floor_double((double)((player.rotationYaw) * 16.0F / 360.0F) + 0.5D) & 15) / 4)) * 90;
			}
		}
    }

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			player.openGui(DragonArtifacts.instance, 0, world, x, y, z);
			return true;
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, par6);
	}

	private void dropItems(World world, int x, int y, int z){
		Random rand = new Random();

		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world,
						x + rx, y + ry, z + rz,
						new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z)
    {
		TileEntity te = world.getTileEntity(x, y, z);
		TileEntityDisplayPedestal ted = (TileEntityDisplayPedestal)te;
		/*if(!world.isRemote) {
			System.out.println("owner?" + par1EntityPlayer.username.equals(ted.owner));
			System.out.println(" - " + ted.owner);
		}*/
		if(world.isRemote) {
			//System.out.println(ted.owner);
		}
		if((ted.owner.getLeastSignificantBits() == player.getUniqueID().getLeastSignificantBits() &&
				ted.owner.getMostSignificantBits() == player.getUniqueID().getMostSignificantBits()) ||
				ted.owner == null || ted.owner.equals(new UUID(0,0))) {
			//if(world.isRemote)
				//System.out.println("breaking");
			float f = this.getBlockHardness(world, x, y, z);
			return ForgeHooks.blockStrength(this, player, world, x, y, z);
		}
		else {
			return 0;
		}
    }
}