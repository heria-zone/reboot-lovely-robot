package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
        if (InternalMetric.LOOT_ENCHANTMENT) {
            level = entity.getCurrentLevel() / InternalMetric.LOOT_ENCHANTMENT_LEVEL;
            if (level > InternalMetric.MAX_LOOT_ENCHANTMENT) {
                level = InternalMetric.MAX_LOOT_ENCHANTMENT;
            }
        }
        return level;
    } // getLootingLevel ()

    // -- Methods --
    public static void handleSetLevel(InternalEntity entity) {
        EntityAttributeInstance maxHealthAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        maxHealthAttribute.setBaseValue(entity.getHp());

        EntityAttributeInstance damageAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        assert damageAttribute != null;
        damageAttribute.setBaseValue(entity.getAttack());

        EntityAttributeInstance armorAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        assert armorAttribute != null;
        armorAttribute.setBaseValue(entity.getArmorLevel());

        EntityAttributeInstance armorToughnessAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        assert armorToughnessAttribute != null;
        armorToughnessAttribute.setBaseValue(entity.getArmorToughnessLevel());
    } // handleSetLevel ()

    public static boolean canLevelUp(InternalEntity entity) {
        return entity.getCurrentLevel() < entity.getMaxLevel();
    } // canLevelUp ()

    public static boolean canLevelUpFireProtection(InternalEntity entity) {
        return entity.getFireProtection() < InternalMetric.PROTECTION_LIMIT_FIRE;
    } // canLevelUpFireProtection ()

    public static boolean canLevelUpFallProtection(InternalEntity entity) {
        return entity.getFallProtection() < InternalMetric.PROTECTION_LIMIT_FALL;
    } // canLevelUpFallProtection ()

    public static boolean canLevelUpBlastProtection(InternalEntity entity) {
        return entity.getBlastProtection() < InternalMetric.PROTECTION_LIMIT_BLAST;
    } // canLevelUpBlastProtection ()

    public static boolean canLevelUpProjectileProtection(InternalEntity entity) {
        return entity.getProjectileProtection() < InternalMetric.PROTECTION_LIMIT_PROJECTILE;
    } // canLevelUpProjectileProtection ()

    public static int getNextExp(InternalEntity entity) {
        return InternalMetric.EXPERIENCE_BASE + entity.getCurrentLevel() * InternalMetric.EXPERIENCE_MULTIPLIER;
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
            InternalParticle.LevelUp(entity);
        }

        entity.setExp(exp);
        if(oldLevel != entity.getCurrentLevel()) {
            if(!entity.world.isClient) {
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
            PlayerEntity player = (PlayerEntity)entity.getOwner();
            player.sendMessage(Text.of(message), overlay);
        }
    } // commandDebug ()

    public static void commandDebug(InternalEntity entity, MutableText message, boolean overlay) {
        if(entity.getOwner() != null) {
            PlayerEntity player = (PlayerEntity)entity.getOwner();
            player.sendMessage(message, overlay);
        }
    } // commandDebug ()

} // Class InternalLogic