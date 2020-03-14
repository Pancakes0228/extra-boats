package com.markus1002.extraboats.item;

import java.util.List;
import java.util.function.Predicate;

import com.markus1002.extraboats.entity.item.boat.ChestBoatEntity;
import com.markus1002.extraboats.entity.item.boat.ModBoatEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeItem;

public class ChestBoatItem extends Item implements IForgeItem
{
	private static final Predicate<Entity> field_219989_a = EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith);
	private final ModBoatEntity.BoatType type;

	public ChestBoatItem(ModBoatEntity.BoatType typeIn, Item.Properties properties)
	{
		super(properties);
		this.type = typeIn;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
		if (raytraceresult.getType() == RayTraceResult.Type.MISS)
		{
			return new ActionResult<>(ActionResultType.PASS, itemstack);
		}
		else
		{
			Vec3d vec3d = playerIn.getLook(1.0F);
			double d0 = 5.0D;
			List<Entity> list = worldIn.getEntitiesInAABBexcluding(playerIn, playerIn.getBoundingBox().expand(vec3d.scale(5.0D)).grow(1.0D), field_219989_a);
			if (!list.isEmpty())
			{
				Vec3d vec3d1 = playerIn.getEyePosition(1.0F);

				for(Entity entity : list)
				{
					AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow((double)entity.getCollisionBorderSize());
					if (axisalignedbb.contains(vec3d1))
					{
						return new ActionResult<>(ActionResultType.PASS, itemstack);
					}
				}
			}

			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK)
			{
				ChestBoatEntity chestboatentity = new ChestBoatEntity(worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
				chestboatentity.setModBoatType(this.type);
				chestboatentity.rotationYaw = playerIn.rotationYaw;
				if (!worldIn.func_226665_a__(chestboatentity, chestboatentity.getBoundingBox().grow(-0.1D)))
				{
					return new ActionResult<>(ActionResultType.FAIL, itemstack);
				}
				else
				{
					if (!worldIn.isRemote)
					{
						worldIn.addEntity(chestboatentity);
					}

					if (!playerIn.abilities.isCreativeMode)
					{
						itemstack.shrink(1);
					}

					playerIn.addStat(Stats.ITEM_USED.get(this));
					return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
				}
			}
			else
			{
				return new ActionResult<>(ActionResultType.PASS, itemstack);
			}
		}
	}
	
    public int getBurnTime(ItemStack itemStack)
    {
        return 500;
    }
}