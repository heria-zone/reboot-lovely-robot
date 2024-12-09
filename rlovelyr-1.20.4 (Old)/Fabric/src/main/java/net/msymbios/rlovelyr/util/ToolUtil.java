package net.msymbios.rlovelyr.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.util.enums.ToolType;
import net.msymbios.rlovelyr.util.internal.ToolContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A utility class for working with tools.
 */
public class ToolUtil {

    // -- Variables --

    /**
     * The list of tools that are classed as weapons.
     */
    private static final List<Item> WEAPONS = new ArrayList<>();

    /**
     * The list of tools that are classed as pickaxes.
     */
    private static final List<Item> PICKAXES = new ArrayList<>();

    /**
     * The list of tools that are classed as axes.
     */
    private static final List<Item> AXES = new ArrayList<>();

    /**
     * The list of tools that are classed as hoes.
     */
    private static final List<Item> HOES = new ArrayList<>();

    /**
     * The list of all items that are classed as tools.
     */
    private static final List<Item> TOOLS = new ArrayList<>();

    // -- Methods --

    /**
     * Initialises the lists of tools.
     */
    public static void init() {
        WEAPONS.clear();
        PICKAXES.clear();
        AXES.clear();
        HOES.clear();

        WEAPONS.addAll(findAllWeapons());
        //PICKAXES.addAll(Registries.ITEM.stream().filter(item -> item.isCorrectToolForDrops(item.getDefaultStack(), Blocks.COAL_ORE.getDefaultState())).toList());
        //AXES.addAll(Registries.ITEM.stream().filter(item -> item.isCorrectToolForDrops(item.getDefaultStack(), Blocks.OAK_LOG.getDefaultState())).toList());
        //HOES.addAll(Registries.ITEM.stream().filter(item -> item.isCorrectToolForDrops(item.getDefaultStack(), Blocks.HAY_BLOCK.getDefaultState())).toList());

        TOOLS.addAll(WEAPONS);
        TOOLS.addAll(PICKAXES);
        TOOLS.addAll(AXES);
        TOOLS.addAll(HOES);
    } // init ()

    /**
     * @return a list of all the items that are classed as weapons.
     */
    private static List<Item> findAllWeapons() {
        List<Item> weapons = Registries.ITEM.stream().filter(item -> item instanceof SwordItem).collect(Collectors.toList());
        //weapons.addAll(TinkersConstructProxy.instance.findAllWeapons());
        return weapons;
    } // findAllWeapons ()

    /**
     * @return true if the given item is a weapon.
     */
    public static boolean isWeapon(ItemStack stack) {
        return isWeapon(stack.getItem());
    }

    /**
     * @return true if the given item is a weapon.
     */
    public static boolean isWeapon(Item item) {
        return WEAPONS.contains(item);
    }

    /**
     * @return true if the given item is a pickaxe.
     */
    public static boolean isPickaxe(ItemStack stack) {
        return isPickaxe(stack.getItem());
    }

    /**
     * @return true if the given item is a pickaxe.
     */
    public static boolean isPickaxe(Item item) {
        return PICKAXES.contains(item);
    }

    /**
     * @return true if the given item is a axe.
     */
    public static boolean isAxe(ItemStack stack) {
        return isAxe(stack.getItem());
    }

    /**
     * @return true if the given item is a axe.
     */
    public static boolean isAxe(Item item) {
        return AXES.contains(item);
    }

    /**
     * @return true if the given item is a hoe.
     */
    public static boolean isHoe(ItemStack stack) {
        return isHoe(stack.getItem());
    }

    /**
     * @return true if the given item is a hoe.
     */
    public static boolean isHoe(Item item) {
        return HOES.contains(item);
    }

    /**
     * @return true if the given item is a tool.
     */
    public static boolean isTool(ItemStack stack) {
        return isTool(stack.getItem());
    }

    /**
     * @return true if the given item is a tool.
     */
    public static boolean isTool(Item item) {
        return TOOLS.contains(item);
    }

    /**
     * @return true if the given item is a tool from Tinkers' Construct.
     */
    //public static boolean isTinkersTool(ItemStack stack) {return isTinkersTool(stack.getItem());}

    /**
     * @return true if the given item is a tool from Tinkers' Construct.
     */
    //public static boolean isTinkersTool(Item item) {return TinkersConstructProxy.instance.isTinkersTool(item);}

    /**
     * @return true if the given tool is in a useable state (e.g. Tinkers' tools aren't broken).
     */
    public static boolean isUseableTool(ItemStack stack) {
        if (!isTool(stack)) return false;
        //if (isTinkersTool(stack)) return TinkersConstructProxy.instance.isToolBroken(stack);
        return true;
    }

    /**
     * @return the default attack speed of the given tool.
     */
    public static float getDefaultToolAttackSpeed(ItemStack stack) {
        return getToolAttackSpeed(stack, null);
    }

    /**
     * @param entity the target entity.
     * @return the attack speed of the given tool.
     */
    public static float getToolAttackSpeed(ItemStack stack, LivingEntity entity) {
        if (isUseableTool(stack)) {
            Multimap<EntityAttribute, EntityAttributeModifier> multimap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);

            for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                EntityAttributeModifier attributemodifier = entry.getValue();
                //UUID baseAttackSpeedAttributeId = ObfuscationReflectionHelper.getPrivateValue(Item.class, Items.ACACIA_BOAT, "f_41375_");

                /*if (attributemodifier.getId() == baseAttackSpeedAttributeId) {
                    // Add on 4.0f as this seems to be the default value the player has
                    // This is why the item tooltips say +1.6f instead of -2.4f for example
                    return (float) attributemodifier.getValue() + 4.0f;
                }*/
            }
        }

        return 4.0f;
    }

    /**
     * @return the default base damage of the given tool.
     */
    public static float getDefaultToolBaseDamage(ItemStack stack) {
        return getToolBaseDamage(stack, null);
    }

    /**
     * @param entity the target entity.
     * @return the base damage of the given tool.
     */
    public static float getToolBaseDamage(ItemStack stack, LivingEntity entity) {
        if (isUseableTool(stack)) {
            Multimap<EntityAttribute, EntityAttributeModifier> multimap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);

            for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                EntityAttributeModifier attributemodifier = entry.getValue();
                /*UUID baseAttackDamageAttributeId = ObfuscationReflectionHelper.getPrivateValue(Item.class, Items.ACACIA_BOAT, "f_41374_");

                if (attributemodifier.getId() == baseAttackDamageAttributeId) {
                    // The tooltip the player sees includes the player's +1.0 damage as well.
                    // But for mod compatibility reasons, we need to leave that off.
                    return (float) attributemodifier.getValue();
                }*/
            }
        }

        return 0.0f;
    }

    /**
     * @return the additional damage of the given tool from its enchantments on the given creature type.
     */
    public static float getToolEnchantmentDamage(ItemStack stack, EntityGroup mobType) {
        return EnchantmentHelper.getAttackDamage(stack, mobType);
    }

    /**
     * @return the knockback level of the given tool.
     */
    public static float getToolKnockbackLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.KNOCKBACK, stack);
    }

    /**
     * @return the fire aspect level of the given tool.
     */
    public static float getToolFireAspectLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack);
    }

    /**
     * @return the harvest speed for the given tool against stone, including enchantments.
     */
    public static float getDefaultToolMiningSpeedWithEnchantments(ItemStack stack) {
        return getDefaultToolMiningSpeed(stack) + getToolEnchantmentHarvestSpeed(stack);
    }

    /**
     * @return the harvest speed for the given tool against wood, including enchantments.
     */
    public static float getDefaultToolWoodcuttingSpeedWithEnchantments(ItemStack stack) {
        return getDefaultToolWoodcuttingSpeed(stack) + getToolEnchantmentHarvestSpeed(stack);
    } // getDefaultToolWoodcuttingSpeedWithEnchantments ()

    /**
     * @return the harvest speed for the given tool against crops, including enchantments.
     */
    public static float getDefaultToolFarmingSpeedWithEnchantments(ItemStack stack) {
        return getDefaultToolFarmingSpeed(stack) + getToolEnchantmentHarvestSpeed(stack);
    } // getDefaultToolFarmingSpeedWithEnchantments ()

    /**
     * @return the harvest speed for the given tool against the given block state, including enchantments.
     */
    public static float getToolHarvestSpeedWithEnchantments(ItemStack stack, BlockState blockState) {
        return getToolHarvestSpeed(stack, blockState) + getToolEnchantmentHarvestSpeed(stack);
    } // getToolHarvestSpeedWithEnchantments ()

    /**
     * @return the harvest speed for the given tool against stone (for reference a wooden pickaxe is 2.0f and diamond pickaxe is 8.0f).
     */
    public static float getDefaultToolMiningSpeed(ItemStack stack) {
        return getToolHarvestSpeed(stack, Blocks.STONE.getDefaultState());
    } // getDefaultToolMiningSpeed ()

    /**
     * @return the harvest speed for the given tool against wood.
     */
    public static float getDefaultToolWoodcuttingSpeed(ItemStack stack) {
        return getToolHarvestSpeed(stack, Blocks.OAK_LOG.getDefaultState());
    } // getDefaultToolWoodcuttingSpeed ()

    /**
     * @return the harvest speed for the given tool against crops.
     */
    public static float getDefaultToolFarmingSpeed(ItemStack stack) {
        return getToolHarvestSpeed(stack, Blocks.HAY_BLOCK.getDefaultState());
    } // getDefaultToolFarmingSpeed ()

    /**
     * @return the harvest speed of the given stack against the given block state.
     */
    public static float getToolHarvestSpeed(ItemStack stack, BlockState blockState) {
        if (isUseableTool(stack)) {
            //if (isTinkersTool(stack)) {
              //  if (canToolHarvest(stack, blockState)) return TinkersConstructProxy.instance.getToolHarvestSpeed(stack, blockState);
            //} else {
                return stack.getMiningSpeedMultiplier(blockState);
           // }
        }

        return 0.0f;
    } // getToolHarvestSpeed ()

    /**
     * @return the default attack/mining/woodcutting/farming speed for the given tool and tool type.
     */
    public static float getDefaultToolSpeed(ItemStack stack, ToolType toolType) {
        return switch (toolType) {
            case WEAPON -> getDefaultToolAttackSpeed(stack);
            case PICKAXE -> getDefaultToolMiningSpeed(stack);
            case AXE -> getDefaultToolWoodcuttingSpeed(stack);
            case HOE -> getDefaultToolFarmingSpeed(stack);
            default -> 0.0f;
        };
    } // getDefaultToolSpeed ()

    /**
     * @return the attack/mining/woodcutting/farming speed for the given tool and tool type.
     */
    public static float getToolHarvestSpeed(ItemStack stack, ToolContext context) {
        return switch (context.toolType) {
            case WEAPON -> getToolAttackSpeed(stack, context.entity);
            case PICKAXE, AXE, HOE -> getToolHarvestSpeed(stack, context.blockState);
            default -> 0.0f;
        };
    } // getToolHarvestSpeed ()

    /**
     * @return the harvest speed for the given tool from only its enchantments.
     */
    public static float getToolEnchantmentHarvestSpeed(ItemStack stack) {
        int level = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
        if (level > 0) return level * level + 1.0f;
        return 0.0f;
    } // getToolEnchantmentHarvestSpeed ()

    /**
     * @return true if the given tool can harvest the given block.
     */
    public static boolean canToolHarvest(ItemStack stack, BlockState blockState) {
        return false;
        /*if (BlockUtil.isCrop(blockState.getBlock()) && ToolUtil.isHoe(stack)) {
            return true;
        }

        if (BlockUtil.isOre(blockState.getBlock()) && !ToolUtil.isPickaxe(stack)) {
            return false;
        } else if (BlockUtil.isLog(blockState.getBlock()) && !ToolUtil.isAxe(stack)) {
            return false;
        }

        /*if (isTinkersTool(stack)) {
            return TinkersConstructProxy.instance.canToolHarvest(stack, blockState);
        } else {
            return stack.getItem().isCorrectToolForDrops(stack, blockState);
        }

        return stack.getItem().isCorrectToolForDrops(stack, blockState);*/
    } // canToolHarvest

    /**
     * @return a list of all the enchantments on the given item.
     */
    public static List<Enchantment> findToolEnchantments(ItemStack stack) {
        List<Enchantment> enchantments = new ArrayList<>();
        NbtList listNBT = stack.getEnchantments();

        for (int i = 0; i < listNBT.size(); i++) {
            NbtCompound tag = listNBT.getCompound(i);
            Identifier enchantmentResource = Identifier.tryParse(tag.getString("id"));

            if (enchantmentResource != null) {
                enchantments.add(Registries.ENCHANTMENT.get(enchantmentResource));
            }
        }

        return enchantments;
    } // findToolEnchantments ()

    /**
     * Damages the given tool by the given amount.
     *
     * @param stack     the tool to damage.
     * @param blockling the blockling equipping the tool.
     * @param damage    the amount of damage to apply.
     * @return true if the stack was destroyed.
     */
    public static boolean damageTool(ItemStack stack, InterfaceEntity blockling, int damage) {
        ItemStack copiedStack = stack.copy();
        //if (blockling.getNaturalBlocklingType() == BlocklingType.DIAMOND || blockling.getBlocklingType() == BlocklingType.DIAMOND) copiedStack.enchant(Enchantments.UNBREAKING, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, copiedStack) + 1);
        boolean destroyed = copiedStack.damage(damage, blockling.getRandom(), null);
        //stack.setDamageValue(copiedStack.getDamageValue());
        return destroyed;
    } // damageTool ()

} // Class ToolUtil