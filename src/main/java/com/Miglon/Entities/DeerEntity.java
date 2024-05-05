package com.Miglon.Entities;

import com.Miglon.Blocks.ModBlocks;
import com.Miglon.DeerMod;
import com.Miglon.Entities.custom.DeerAttackGoal;
import com.Miglon.Entities.custom.DeerVariant;
import com.Miglon.gen.ModTags;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;


public class DeerEntity extends AbstractHorseEntity implements Angerable {
    private static final TrackedData<Integer> ANGER_TIME;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    public RevengeGoal revengeGoal;
    @Nullable
    private UUID angryAt;

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(ModBlocks.REINDEER_MOSS, Items.KELP);
    private int growAntlersTime;

    public DeerEntity(EntityType<? extends DeerEntity> entityType, World world) {
        super(entityType, world);
        this.growAntlersTime = this.random.nextInt(3000) + 3000;
        this.AGE = this.random.nextInt(3);
    }

    protected void initGoals(){
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(3, new DeerAttackGoal(this, 1.2, true));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new TemptGoal(this, 1.25, Ingredient.ofItems(new ItemConvertible[]{ModBlocks.REINDEER_MOSS}), false));
        this.goalSelector.add(6, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector.add(0, revengeGoal = (new  RevengeGoal(this, new Class[0])));
    }

    public static DefaultAttributeContainer.Builder createDeerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 18.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20000000298023224)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 6)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, 1);
    }

    static {
        ANGER_TIME = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    }


    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getWorld().isClient || !this.isAlive()) {
            return;
        }
        this.walkToParent();
        if (!this.getWorld().isClient && this.isAlive() && !this.isBaby() && --this.growAntlersTime <= 0) {
            if (this.isMature()) {
                this.dropItem(Items.STICK);
                this.emitGameEvent(GameEvent.ENTITY_PLACE);
                this.growAntlersTime = this.random.nextInt(3000) + 3000;
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.removeAntlers();
            }
            else {
                this.AGE++;
                this.growAntlersTime = this.random.nextInt(3000) + 3000;
                addAntlers();
            }
        }
    }

    /*SOUND*/

    protected SoundEvent getAmbientSound() {
        return this.isSubmergedIn(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
    }

    protected SoundEvent getSwimSound() {
        if (this.isOnGround()) {
            if (!this.hasPassengers()) {
                return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
            }

            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
            }

            if (this.soundTicks <= 5) {
                return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
            }
        }

        return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
    }

    protected void playSwimSound(float volume) {
        if (this.isOnGround()) {
            super.playSwimSound(0.3F);
        } else {
            super.playSwimSound(Math.min(0.1F, volume * 25.0F));
        }

    }

    protected void playJumpSound() {
        if (this.isTouchingWater()) {
            this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
        } else {
            super.playJumpSound();
        }

    }


    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height - (this.isBaby() ? 0.03125F : 0.28125F) * scaleFactor, 0.0F);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(ANTLERS, AGE);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putInt("TypeAntlers", this.getAntlersType());
        this.writeAngerToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.dataTracker.set(ANTLERS, nbt.getInt("TypeAntlers"));
        this.readAngerFromNbt(this.getWorld(), nbt);
    }

    protected float getBaseMovementSpeedMultiplier() {
        return 0.96F;
    }

    @Override
    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        boolean bl = false;
        float f = 0.0F;
        int i = 0;
        int j = 0;
        if (item.isOf(Items.KELP)) {
            f = 2.0f;
            i = 20;
            j = 3;
        } else if (item.isOf(ModBlocks.REINDEER_MOSS.asItem())) {
            f = 2.0F;
            i = 20;
            j = 3;
            if (!this.getWorld().isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(player);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            bl = true;
        }

        if (this.isBaby() && i > 0) {
            this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
            if (!this.getWorld().isClient) {
                this.growUp(i);
                bl = true;
            }
        }

        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper() && !this.getWorld().isClient) {
            this.addTemper(j);
            bl = true;
        }

        if (bl) {
            this.emitGameEvent(GameEvent.EAT);
        }

        return bl;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        DeerEntity deerEntity = DeerMod.DEER_ENTITY.create(world);
        if(deerEntity != null && entity instanceof DeerEntity){
            deerEntity.setVariant(this.getVariant());
        }
        return deerEntity;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        boolean bl = !this.isBaby() && this.isTame() && player.shouldCancelInteraction();
        if (!this.hasPassengers() && !bl) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                if (this.isBreedingItem(itemStack)) {
                    return this.interactHorse(player, itemStack);
                }

                if (!this.isTame()) {
                    this.playAngrySound();
                    return ActionResult.success(this.getWorld().isClient);
                }
            }

            return super.interactMob(player, hand);
        } else {
            return super.interactMob(player, hand);
        }
    }

    protected void updateSaddle() {
        if (!this.getWorld().isClient) {
            super.updateSaddle();
            this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }

    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!(other instanceof DeerEntity)) {
            return false;
        } else {
            return this.canBreed();
        }
    }

    /*ANGRY*/

    @Override
    public int getAngerTime() { return this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int angerTime) { this.dataTracker.set(ANGER_TIME, angerTime);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() { this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }


    /*ANTLERS*/
    public static final int MAX_AGE = 3;
    private static final TrackedData<Integer> ANTLERS =
            DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int AGE;

    public void addAntlers() {
        this.dataTracker.set(ANTLERS, AGE);
    }

    public void removeAntlers() {
        this.AGE = 0;
        this.dataTracker.set(ANTLERS, 0);
    }

    public int getAntlersType() { return dataTracker.get(ANTLERS);}

    protected void onGrowUp() {
        if (this.isBaby()) {
            this.removeAntlers();
        } else {
            this.addAntlers();
        }
    }

    /*age_antlers*/

    protected int getAge() { return AGE; }

    public int getMaxAge() { return MAX_AGE; }

    public final boolean isMature() {
        return this.getAge() >= this.getMaxAge();
    }

    /*VARIANTS*/
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        RegistryEntry<Biome> registryEntry = world.getBiome(this.getBlockPos());
        if (registryEntry.isIn(ModTags.Biomes.SPAWNS_CHERRY_VARIANT_DEERS)) {
            this.setVariant(DeerVariant.CHERRY);
        } else if (registryEntry.isIn(ModTags.Biomes.SPAWNS_COLD_VARIANT_DEERS)) {
            this.setVariant(DeerVariant.NORTH);
        } else {
            this.setVariant(DeerVariant.DEFAULT);
        }
        this.onGrowUp();

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public DeerVariant getVariant() {
        return DeerVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(DeerVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityView method_48926() {
        return null;
    }
}
