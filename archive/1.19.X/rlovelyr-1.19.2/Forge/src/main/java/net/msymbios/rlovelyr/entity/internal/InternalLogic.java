package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class InternalLogic {

    // -- Properties --
    public static int getHpValue(InternalEntity entity, int value) {
        return (value + entity.getCurrentLevel() * value / 50);
    } // getHpValue ()

    public static int getAttackValue(InternalEntity entity, int value) {
        return (value + entity.getCurrentLevel() * value / 50);
    } // getAttackValue ()

    public static int getDefenseValue(InternalEntity entity, int value) {
        return (value + entity.getCurrentLevel() * value / 50);
    } // getDefenseValue ()

    public static double getArmorValue (InternalEntity entity) {
        int armor = entity.getDefense();
        if (armor > 30) armor = 30;
        return armor;
    } // getArmorValue ()

    public static double getArmorToughnessValue (InternalEntity entity) {
        double armor = entity.getArmorLevel();
        double armor_tou = 0;
        if (armor > 30) armor_tou = armor - 30;
        return armor_tou;
    } // getArmorToughnessValue ()

    public static int getLootingLevel(InternalEntity entity) {
        int level = 0;
        if (InternalMetric.LOOT_ENCHANTMENT.get()) {
            level = entity.getCurrentLevel() / InternalMetric.LOOT_ENCHANTMENT_LEVEL.get();
            if (level > InternalMetric.MAX_LOOT_ENCHANTMENT.get()) {
                level = InternalMetric.MAX_LOOT_ENCHANTMENT.get();
            }
        }
        return level;
    } // getLootingLevel ()

    // -- Methods --
    public static void handleSetLevel(InternalEntity entity) {
        Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(entity.getHp());
        Objects.requireNonNull(entity.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(entity.getAttack());
        Objects.requireNonNull(entity.getAttribute(Attributes.ARMOR)).setBaseValue(entity.getArmorLevel());
        Objects.requireNonNull(entity.getAttribute(Attributes.ARMOR_TOUGHNESS)).setBaseValue(entity.getArmorToughnessLevel());
    } // handleSetLevel ()

    public static boolean canLevelUp(InternalEntity entity) {
        return entity.getCurrentLevel() < entity.getMaxLevel();
    } // canLevelUp ()

    public static boolean canLevelUpFireProtection(InternalEntity entity) {
        return entity.getFireProtection() < InternalMetric.PROTECTION_LIMIT_FIRE.get();
    } // canLevelUpFireProtection ()

    public static boolean canLevelUpFallProtection(InternalEntity entity) {
        return entity.getFallProtection() < InternalMetric.PROTECTION_LIMIT_FALL.get();
    } // canLevelUpFallProtection ()

    public static boolean canLevelUpBlastProtection(InternalEntity entity) {
        return entity.getBlastProtection() < InternalMetric.PROTECTION_LIMIT_BLAST.get();
    } // canLevelUpBlastProtection ()

    public static boolean canLevelUpProjectileProtection(InternalEntity entity) {
        return entity.getProjectileProtection() < InternalMetric.PROTECTION_LIMIT_PROJECTILE.get();
    } // canLevelUpProjectileProtection ()

    public static int getNextExp(InternalEntity entity) {
        return InternalMetric.EXPERIENCE_BASE.get() + entity.getCurrentLevel() * InternalMetric.EXPERIENCE_MULTIPLIER.get();
    } // getNextExp ()

    public static void addExp (InternalEntity entity, int value) {
        int addExp = value;
        int exp = entity.getExp();
        String customName = "";
        try {customName = entity.getCustomName().getString();}
        catch (Exception ignored) {}

        // if they have a name they earn more exp
        if(!customName.isEmpty()) addExp = addExp * 3 / 2;
        exp += addExp;

        int oldLevel = entity.getCurrentLevel();
        while (exp >= getNextExp(entity)) {
            exp -= getNextExp(entity);
            entity.setCurrentLevel(entity.getCurrentLevel() + 1);
            //InternalParticle.LevelUp(entity);
        }

        entity.setExp(exp);
        if(oldLevel != entity.getCurrentLevel()) {
            if(!entity.level.isClientSide) {
                try {
                    final LivingEntity owner = entity.getOwner();
                    if (owner == null) return;
                    entity.displayGeneralMessage(entity.getNotification(), true);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    // UTILITY
    public static void commandDebug(InternalEntity entity, String message, boolean overlay) {
        if(entity.getOwner() != null) {
            Player player = (Player) entity.getOwner();
            player.displayClientMessage(Component.nullToEmpty(message), overlay);
        }
    } // commandDebug ()

    public static void commandDebug(InternalEntity entity, MutableComponent message, boolean overlay) {
        if(entity.getOwner() != null) {
            Player player = (Player) entity.getOwner();
            player.displayClientMessage(message, overlay);
        }
    } // commandDebug ()

} // Class InternalLogic