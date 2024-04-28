package com.Miglon.Entities;

import com.Miglon.DeerMod;
import com.Miglon.Entities.custom.DeerAttackGoal;
import com.Miglon.Entities.custom.DeerVariant;
import com.Miglon.Items.ModItems;
import com.Miglon.gen.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
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
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Random;
import java.util.UUID;


public class DeerEntity extends AbstractHorseEntity implements Angerable {
    private static final TrackedData<Integer> ANGER_TIME;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    public RevengeGoal revengeGoal;
    @Nullable
    private UUID angryAt;

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(ModItems.REINDEER_MOSS, Items.KELP);

    public DeerEntity(EntityType<? extends DeerEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals(){
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(3, new DeerAttackGoal(this, 1.2, true));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new TemptGoal(this, 1.25, Ingredient.ofItems(new ItemConvertible[]{ModItems.REINDEER_MOSS}), false));
        this.goalSelector.add(6, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector.add(1, revengeGoal = (new  RevengeGoal(this, new Class[0])));
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
        ANGER_TIME = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
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
        this.dataTracker.startTracking(HORN, randomHorn);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putInt("TypeHorn", this.getHornType());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.dataTracker.set(HORN, nbt.getInt("TypeHorn"));
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
        } else if (item.isOf(ModItems.REINDEER_MOSS)) {
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

    @Override
    public EntityView method_48926() {
        return null;
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


    /*HORNS*/
    public static final IntProperty AGE = Properties.AGE_7;
    public static final int MAX_AGE = 7;
    private static final int MAX_HORN = 3;
    private static final TrackedData<Integer> HORN =
            DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private final int randomHorn = new Random().nextInt(3);

    public void addHorns() {
        this.dataTracker.set(HORN, randomHorn);
    }

    public void removeHorns() {
        this.dataTracker.set(HORN, 0);
    }

    public int hasHorn() {
        return (int) dataTracker.get(HORN);
    }

    public int getHornType() { return dataTracker.get(HORN);}

    public int getMaxHorn() { return MAX_HORN; }

    protected void onGrowUp() {
        if (this.isBaby()) {
            this.removeHorns();
        } else {
            this.addHorns();
        }
    }

    /*AGE_HORN*/

   /* protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return MAX_AGE;
    }

    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return HORN.getDefaultState().with(this.getAgeProperty(), age);
    }

    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    public boolean hasRandomTicks(BlockState state) {
        return !this.isMature(state);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        float f;
        int i;
        if (world.getBaseLightLevel(pos, 0) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && random.nextInt((int)(25.0f / (f = CropBlock.getAvailableMoisture(this, world, pos))) + 1) == 0) {
            world.setBlockState(pos, this.withAge(i + 1), Block.NOTIFY_LISTENERS);
        }
    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int j;
        int i = this.getAge(state) + this.getGrowthAmount(world);
        if (i > (j = this.getMaxAge())) {
            i = j;
        }
        world.setBlockState(pos, this.withAge(i), Block.NOTIFY_LISTENERS);
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }*/

    /*VARIANTS*/
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        RegistryEntry<Biome> registryEntry = world.getBiome(this.getBlockPos());
        if (registryEntry.isIn(ModTags.Biomes.SPAWNS_COLD_VARIANT_DEERS)) {
            this.setVariant(DeerVariant.COLD);
        } else if (registryEntry.isIn(ModTags.Biomes.SPAWNS_ICE_VARIANT_DEERS)) {
            this.setVariant(DeerVariant.ICE);
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
}
