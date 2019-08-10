package core.minecraft.common.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;

/**
 * Item utilities.
 *
 * @author Preston Brown
 */
public class ItemUtil {

    private static Enchantment _glowEnchantment;

    static
    {
        try
        {
            Enchantment enchantment = new Enchantment(99) {

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public int getMaxLevel() {
                    return 0;
                }

                @Override
                public int getStartLevel() {
                    return 0;
                }

                @Override
                public EnchantmentTarget getItemTarget() {
                    return null;
                }

                @Override
                public boolean conflictsWith(Enchantment enchantment) {
                    return false;
                }

                @Override
                public boolean canEnchantItem(ItemStack itemStack) {
                    return false;
                }
            };
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            field.setAccessible(false);

            Enchantment.registerEnchantment(enchantment);
            _glowEnchantment = enchantment;
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            System.out.println("Failed to add glow to ItemStack");
        }
    }

    /**
     * Adds a dummy glow enchantment to the given ItemStack.
     *
     * @param itemStack the inventory that is receiving the glow enchantment
     */
    public static void addItemGlow(ItemStack itemStack)
    {
        if (_glowEnchantment != null)
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addEnchant(_glowEnchantment, 1, true);
            itemStack.setItemMeta(itemMeta);
        }
    }

    /**
     * Returns the display name of the ItemStack.
     *
     * If the item stack is null, null will be returned.
     *
     * @param item the ItemStack being analyzed
     * @return the display name of the item if it is not null, otherwise null will be returned
     */
    public static String getItemDisplayName(ItemStack item)
    {
        if (item == null)
        {
            return null;
        }

        // Returns the item's display name if the item has a custom name
        if (item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null)
        {
            return item.getItemMeta().getDisplayName();
        }

        // Returns the name of the item if it doesn't have a custom name
        return WordUtils.capitalize(item.getType().name().replace('_', ' ').toLowerCase());
    }
}
