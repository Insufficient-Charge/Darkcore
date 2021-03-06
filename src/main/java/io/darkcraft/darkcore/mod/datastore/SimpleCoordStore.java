package io.darkcraft.darkcore.mod.datastore;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.nbt.Mapper;
import io.darkcraft.darkcore.mod.nbt.NBTConstructor;
import io.darkcraft.darkcore.mod.nbt.NBTHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTProperty.SerialisableType;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

@NBTSerialisable(createNew = true)
public class SimpleCoordStore
{
	private static final Mapper<SimpleCoordStore> MAPPER = NBTHelper.getMapper(SimpleCoordStore.class, SerialisableType.WORLD);

	@NBTProperty(name = "w")
	public final int	world;
	@NBTProperty
	public final int	x;
	@NBTProperty
	public final int	y;
	@NBTProperty
	public final int	z;

	public SimpleCoordStore(TileEntity te)
	{
		world = WorldHelper.getWorldID(te);
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
	}

	@NBTConstructor({"w", "x", "y", "z"})
	public SimpleCoordStore(int win, int xin, int yin, int zin)
	{
		world = win;
		x = xin;
		y = yin;
		z = zin;
	}

	public SimpleCoordStore(World w, int xin, int yin, int zin)
	{
		world = WorldHelper.getWorldID(w);
		x = xin;
		y = yin;
		z = zin;
	}

	public SimpleCoordStore(EntityPlayer player)
	{
		world = WorldHelper.getWorldID(player);
		x = (int) Math.floor(player.posX);
		y = (int) Math.floor(player.posY);
		z = (int) Math.floor(player.posZ);
	}

	public SimpleCoordStore(SimpleDoubleCoordStore pos)
	{
		this(pos.world, (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
	}

	public SimpleCoordStore(World w, MovingObjectPosition hitPos)
	{
		this(w, hitPos.blockX, hitPos.blockY, hitPos.blockZ);
	}

	public SimpleDoubleCoordStore getCenter()
	{
		return new SimpleDoubleCoordStore(world, x + 0.5, y + 0.5, z + 0.5);
	}

	public SimpleCoordStore getNearby(ForgeDirection dir)
	{
		return getNearby(dir,1);
	}

	public SimpleCoordStore getNearby(ForgeDirection dir, int distance)
	{
		int oX = dir.offsetX * distance;
		int oY = dir.offsetY * distance;
		int oZ = dir.offsetZ * distance;
		return new SimpleCoordStore(world, x + oX, y + oY, z + oZ);
	}

	public static SimpleCoordStore[] getTouching(SimpleDoubleCoordStore pos, boolean includeCeilHeight)
	{
		SimpleCoordStore[] slots = new SimpleCoordStore[includeCeilHeight ? 8 : 4];
		int w = pos.world;
		slots[0] = new SimpleCoordStore(w, MathHelper.floor(pos.x), MathHelper.floor(pos.y), MathHelper.floor(pos.z));
		slots[1] = new SimpleCoordStore(w, MathHelper.floor(pos.x), MathHelper.floor(pos.y), MathHelper.ceil(pos.z));
		slots[2] = new SimpleCoordStore(w, MathHelper.ceil(pos.x), MathHelper.floor(pos.y), MathHelper.ceil(pos.z));
		slots[3] = new SimpleCoordStore(w, MathHelper.ceil(pos.x), MathHelper.floor(pos.y), MathHelper.floor(pos.z));
		if(includeCeilHeight)
		{
			slots[4] = new SimpleCoordStore(w, MathHelper.floor(pos.x), MathHelper.ceil(pos.y), MathHelper.floor(pos.z));
			slots[5] = new SimpleCoordStore(w, MathHelper.floor(pos.x), MathHelper.ceil(pos.y), MathHelper.ceil(pos.z));
			slots[6] = new SimpleCoordStore(w, MathHelper.ceil(pos.x), MathHelper.ceil(pos.y), MathHelper.ceil(pos.z));
			slots[7] = new SimpleCoordStore(w, MathHelper.ceil(pos.x), MathHelper.ceil(pos.y), MathHelper.floor(pos.z));
		}
		return slots;
	}

	public World getWorldObj()
	{
		return WorldHelper.getWorld(world);
	}

	@Override
	public String toString()
	{
		return "World " + world + ":" + x + "," + y + "," + z;
	}

	public String toSimpleString()
	{
		return x + "," + y + "," + z;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + world;
		result = (prime * result) + x;
		result = (prime * result) + y;
		result = (prime * result) + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (!(obj instanceof SimpleCoordStore)) return false;
		SimpleCoordStore other = (SimpleCoordStore) obj;
		if (world != other.world) return false;
		if ((x != other.x) || (y != other.y) || (z != other.z)) return false;
		return true;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return nbt;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		MAPPER.writeToNBT(nbt, this);
	}

	public void writeToNBT(NBTTagCompound nbt, String name)
	{
		MAPPER.writeToNBT(nbt, name, this);
	}

	public static SimpleCoordStore readFromNBT(NBTTagCompound nbt)
	{
		return MAPPER.createFromNBT(nbt);
	}

	public static SimpleCoordStore readFromNBT(NBTTagCompound nbt, String name)
	{
		return MAPPER.readFromNBT(nbt, name);
	}

	public ChunkCoordIntPair toChunkCoords()
	{
		return new ChunkCoordIntPair(x >> 4, z >> 4);
	}

	public ChunkCoordinates toChunkCoordinates()
	{
		return new ChunkCoordinates(x,y,z);
	}

	public TileEntity getTileEntity()
	{
		World w = getWorldObj();
		if (w != null) return w.getTileEntity(x, y, z);
		return null;
	}

	public Block getBlock()
	{
		World w = getWorldObj();
		if (w != null) if (!(w.getBlock(x, y, z) == Blocks.air)) return w.getBlock(x, y, z);
		return null;
	}

	public int getMetadata()
	{
		World w = getWorldObj();
		if (w != null) return w.getBlockMetadata(x, y, z);
		return 0;
	}

	public SimpleDoubleCoordStore travelTo(SimpleCoordStore dest, double amountTravelled, boolean newWorld)
	{
		if(Double.isNaN(amountTravelled) || Double.isInfinite(amountTravelled))
			return null;
		int newWorldID = newWorld ? dest.world : world;
		double nx = x + ((dest.x - x) * amountTravelled);
		double ny = y + ((dest.y - y) * amountTravelled);
		double nz = z + ((dest.z - z) * amountTravelled);
		return new SimpleDoubleCoordStore(newWorldID, nx, ny, nz);
	}

	public double distance(SimpleCoordStore destLocation)
	{
		if (destLocation == null) return 0;
		long xr = (x - destLocation.x);
		xr *= xr;
		long yr = (y - destLocation.y);
		yr *= yr;
		long zr = (z - destLocation.z);
		zr *= zr;
		return Math.sqrt(xr + yr + zr);
	}

	public long diagonalParadoxDistance(long sx, long sy, long sz)
	{
		return Math.abs(x - sx) + Math.abs(y - sy) + Math.abs(z - sz);
	}

	public long diagonalParadoxDistance(SimpleCoordStore dest)
	{
		return diagonalParadoxDistance(dest.x, dest.y, dest.z);
	}

	public long diagonalParadoxDistance(SimpleDoubleCoordStore dest)
	{
		return diagonalParadoxDistance(dest.iX, dest.iY, dest.iZ);
	}

	public void setMetadata(int meta, int notify)
	{
		getWorldObj().setBlockMetadataWithNotify(x, y, z, meta, notify);
	}

	public void setBlock(Block b, int metadata, int notify)
	{
		getWorldObj().setBlock(x, y, z, b, metadata, notify);
	}

	public void notifyBlock()
	{
		getWorldObj().notifyBlockOfNeighborChange(x, y, z, getBlock());
	}

	public void setToAir()
	{
		getWorldObj().setBlockToAir(x, y, z);
	}

	public AxisAlignedBB getAABB(int h)
	{
		return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+h, z+1);
	}

	public boolean isLoaded()
	{
		World w = getWorldObj();
		if(w == null) return false;
		return w.getChunkProvider().chunkExists(x >> 4, z >> 4);
	}

	public SimpleCoordStore translate(int _x, int _y, int _z)
	{
		return new SimpleCoordStore(world, x+_x, y+_y, z+_z);
	}

	public <T> T getTileEntity(Class<T> clazz)
	{
		TileEntity t = getTileEntity();
		if(clazz.isInstance(t))
			return (T) t;
		return null;
	}
}
