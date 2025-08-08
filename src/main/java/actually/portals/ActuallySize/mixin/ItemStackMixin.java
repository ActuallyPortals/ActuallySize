package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.SetLevelExt;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> implements net.minecraftforge.common.extensions.IForgeItemStack, ItemDualityCounterpart {

    @Shadow public abstract Item getItem();

    @Shadow @javax.annotation.Nullable public abstract CompoundTag getTag();

    protected ItemStackMixin(Class<ItemStack> baseClass) { super(baseClass); }

    @Unique
    @Nullable Entity actuallysize$enclosedEntity;
    @Unique
    @Nullable Entity actuallysize$entityCounterpart;
    @Unique
    @Nullable ItemEntityDualityHolder actuallysize$dualityHolderCounterpart;
    @Unique
    @Nullable ItemStackLocation<? extends Entity> actuallysize$dualityStackLocation;

    @Override
    public @Nullable Entity actuallysize$getEntityCounterpart() {

        // Need to be an ASI Held Entity Item, of course
        if (!(getItem() instanceof ASIPSHeldEntityItem)) { return null; }

        // Return that
        return actuallysize$entityCounterpart;
    }

    @Override
    public @Nullable Entity actuallysize$readyEntityCounterpart(@NotNull Level world) {

        // Need to be an ASI Held Entity Item, of course
        if (!(getItem() instanceof ASIPSHeldEntityItem)) { return null; }

        /*
         * Attempt find already in world, but the find-by-UUID only exists on the server-side :B
         */
        if (actuallysize$entityCounterpart == null && world instanceof ServerLevel) {

            // Find it by the tag in the item
            CompoundTag myTag = getTag();
            if (myTag != null && myTag.contains(ASIPSHeldEntityItem.TAG_ENTITY_UUID)) {
                UUID uuid = myTag.getUUID(ASIPSHeldEntityItem.TAG_ENTITY_UUID);
                actuallysize$entityCounterpart = ((ServerLevel) world).getEntity(uuid);
                if (actuallysize$entityCounterpart == null) { actuallysize$entityCounterpart = world.getServer().getPlayerList().getPlayer(uuid); }
            }

            // Found it in the world? Nice!
            if (actuallysize$entityCounterpart != null) { return actuallysize$entityCounterpart; }
        }

        // Cannot regen if currently active
        if (actuallysize$isDualityActive()) { return actuallysize$getEntityCounterpart(); }
        //NBT//ActuallySizeInteractions.Log("ASI &6 REC &r Generating from ItemStackMixin.actuallysize$readyEntityCounterpart(Level)");

        // Otherwise, load
        ItemStack asItem = (ItemStack) (Object) this;
        if (asItem.getCount() < 1) { return null; }
        if (!asItem.hasTag()) { return null; }
        CompoundTag compoundTag = asItem.getTag();
        if (compoundTag == null) { return null; }
        Entity rebuilt = ASIPickupSystemManager.loadEntityFromTag(compoundTag.getCompound(ASIPSHeldEntityItem.TAG_ENTITY), world, UUID.randomUUID());
        if (rebuilt == null) { return null; }

        // Accept and return
        actuallysize$entityCounterpart = rebuilt;
        return actuallysize$entityCounterpart;
    }

    @Override
    public @Nullable UUID actuallysize$getEnclosedEntityUUID() {

        // Need to be an ASI Held Entity Item, of course
        if (!(getItem() instanceof ASIPSHeldEntityItem)) { return null; }

        // If already loaded, done
        if (actuallysize$enclosedEntity != null) { return actuallysize$enclosedEntity.getUUID(); }
        //NBT//ActuallySizeInteractions.Log("ASI &6 REC &r Generating from ItemStackMixin.actuallysize$getEnclosedEntityUUID()");

        // Otherwise, load
        ItemStack asItem = (ItemStack) (Object) this;
        if (asItem.getCount() < 1) { return null; }
        if (!asItem.hasTag()) { return null; }
        CompoundTag compoundTag = asItem.getTag();
        if (compoundTag == null) { return null; }
        CompoundTag entityTag = compoundTag.getCompound(ASIPSHeldEntityItem.TAG_ENTITY);
        if (!entityTag.hasUUID(Entity.UUID_TAG)) { return null; }
        return entityTag.getUUID(Entity.UUID_TAG);
    }

    @Override
    public @Nullable ItemEntityDualityHolder actuallysize$getItemEntityHolder() {
        return actuallysize$dualityHolderCounterpart;
    }

    @Override
    public @Nullable ItemStackLocation<? extends Entity> actuallysize$getItemStackLocation() {
        return actuallysize$dualityStackLocation;
    }

    @Override
    public void actuallysize$setItemStackLocation(@Nullable ItemStackLocation<? extends Entity> who) {

        // Need to be an ASI Held Entity Item, of course
        if ((who != null) && !(getItem() instanceof ASIPSHeldEntityItem)) { throw new UnsupportedOperationException("Only ASI Held Entity items may have holders. "); }

        // Set
        actuallysize$dualityStackLocation = who;
    }

    @Override
    public void actuallysize$setEntityCounterpart(@Nullable Entity who) {

        // Need to be an ASI Held Entity Item, of course
        if ((who != null) && !(getItem() instanceof ASIPSHeldEntityItem)) { throw new UnsupportedOperationException("Only ASI Held Entity items may have entity counterparts. "); }

        // Set
        actuallysize$entityCounterpart = who;
    }

    @Override
    public void actuallysize$setItemEntityHolder(@Nullable ItemEntityDualityHolder who) {

        // Need to be an ASI Held Entity Item, of course
        if ((who != null) && !(getItem() instanceof ASIPSHeldEntityItem)) { throw new UnsupportedOperationException("Only ASI Held Entity items may have holders. "); }

        // Set
        actuallysize$dualityHolderCounterpart = who;
    }


    @Override
    public boolean actuallysize$isDualityActive() {

        // Need to be an ASI Held Entity Item, of course
        if (!(getItem() instanceof ASIPSHeldEntityItem)) { return false; }

        // Get the Entity Counterpart
        Entity asEntity = actuallysize$getEntityCounterpart();

        /*
         * Not only must it exist, it must also be, you know, valid.
         */
        return asEntity != null && asEntity.isAddedToWorld() && asEntity.isAlive() && !asEntity.isRemoved() && actuallysize$getItemStackLocation() != null;
    }

    @Override
    public @Nullable Entity actuallysize$getEnclosedEntity(@NotNull Level world) {

        // Need to be an ASI Held Entity Item, of course
        if (!(getItem() instanceof ASIPSHeldEntityItem)) { return null; }
        //NBT//ActuallySizeInteractions.Log("ASI &6 REC &r Generating from ItemStackMixin.actuallysize$getEnclosedEntity(Level)");
        
        // Players do not count as enclosed entities
        if (((ASIPSHeldEntityItem) getItem()).isPlayer()) { return null; }

        // If already loaded, done
        if (actuallysize$enclosedEntity != null) {

            // Make sure the world is the one requested
            ((SetLevelExt) actuallysize$enclosedEntity).actuallysize$SetWorld(world);

            // Done
            return actuallysize$enclosedEntity;
        }

        // Otherwise, load
        ItemStack asItem = (ItemStack) (Object) this;
        if (asItem.getCount() < 1) { return null; }
        if (!asItem.hasTag()) { return null; }
        CompoundTag compoundTag = asItem.getTag();
        if (compoundTag == null) { return null; }
        Entity rebuilt = ASIPickupSystemManager.loadEntityFromTag(compoundTag.getCompound(ASIPSHeldEntityItem.TAG_ENTITY), world);
        if (rebuilt == null) { return null; }

        // Accept and return
        actuallysize$enclosedEntity = rebuilt;
        return actuallysize$enclosedEntity;
    }

    @Override
    public boolean actuallysize$hasEnclosedEntity(@NotNull Level world) {

        // Need to be an ASI Held Entity Item, of course
        if (!(getItem() instanceof ASIPSHeldEntityItem)) { return false; }

        // Must have an enclosed entity
        return actuallysize$getEnclosedEntity(world) != null;
    }
}
