package net.msymbios.rlovelyr.entity.animation.condition;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ConditionalHold {

    // -- Variables --
    private static final String EMPTY_MAINHAND = "hold_mainhand:empty";
    private static final String EMPTY_OFFHAND = "hold_offhand:empty";
    private static final String EMPTY = "";
    private final int preSize;
    private final String idPre;
    private final String tagPre;
    private final String extraPre;
    private final List<Identifier> idTest = Lists.newArrayList();
    private final List<TagKey<Item>> tagTest = Lists.newArrayList();
    //private final List<UseAnim> extraTest = Lists.newArrayList();
    private final List<String> innerTest = Lists.newArrayList();

    // -- Constructor --
    public ConditionalHold(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            idPre = "hold_mainhand$";
            tagPre = "hold_mainhand#";
            extraPre = "hold_mainhand:";
            preSize = 14;
        } else {
            idPre = "hold_offhand$";
            tagPre = "hold_offhand#";
            extraPre = "hold_offhand:";
            preSize = 13;
        }
    } // Constructor ConditionalHold ()

    // -- Methods --

    /*public void addTest(String name) {
        if (name.length() <= preSize) {
            return;
        }
        String substring = name.substring(preSize);
        if (name.startsWith(idPre) && Identifier.isValid(substring)) {
            idTest.add(new Identifier(substring));
        }
        if (name.startsWith(tagPre) && Identifier.isValid(substring)) {
            ITagManager<Item> tags = Registries.ITEM.streamTags();
            if (tags == null) {
                return;
            }
            TagKey<Item> tagKey = tags.createTagKey(new Identifier(substring));
            tagTest.add(tagKey);
        }
        if (name.startsWith(extraPre)) {
            if (substring.equals(UseAnim.NONE.name().toLowerCase(Locale.US))) {
                return;
            }
            Arrays.stream(UseAnim.values()).filter(a -> a.name().toLowerCase(Locale.US).equals(substring)).findFirst().ifPresent(extraTest::add);
            innerTest.add(name);
        }
    }

    public String doTest(InternalEntity entity, Hand hand) {
        if (entity.getItemInHand(hand).isEmpty()) {
            return hand == InteractionHand.MAIN_HAND ? EMPTY_MAINHAND : EMPTY_OFFHAND;
        }
        String result = doIdTest(entity, hand);
        if (result.isEmpty()) {
            result = doTagTest(entity, hand);
            if (result.isEmpty()) {
                return doExtraTest(entity, hand);
            }
            return result;
        }
        return result;
    }

    private String doIdTest(InternalEntity entity, Hand hand) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemInHand = entity.getItemInHand(hand);
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(itemInHand.getItem());
        if (registryName == null) {
            return EMPTY;
        }
        if (idTest.contains(registryName)) {
            return idPre + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(InternalEntity entity, Hand hand) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemInHand = entity.getItemInHand(hand);
        ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
        if (tags == null) {
            return EMPTY;
        }
        return tagTest.stream().filter(itemInHand::is).findFirst().map(itemTagKey -> tagPre + itemTagKey.location()).orElse(EMPTY);
    }

    private String doExtraTest(InternalEntity entity, Hand hand) {
        if (extraTest.isEmpty() && innerTest.isEmpty()) {
            return EMPTY;
        }
        String innerName = InnerClassify.doClassifyTest(extraPre, entity, hand);
        if (StringUtils.isNotBlank(innerName) && this.innerTest.contains(innerName)) {
            return innerName;
        }
        UseAnim anim = entity.getItemInHand(hand).getUseAnimation();
        if (this.extraTest.contains(anim)) {
            return extraPre + anim.name().toLowerCase(Locale.US);
        }
        return EMPTY;
    }*/

} // Class ConditionalHold