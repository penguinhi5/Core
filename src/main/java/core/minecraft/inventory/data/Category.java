package core.minecraft.inventory.data;

/**
 * Represents a category of items.
 */
public class Category {

    private int _categoryID;
    private String _categoryName;

    /**
     * Creates a new Category.
     *
     * @param categoryID the inventory categoryID
     * @param categoryName the name of the category
     */
    public Category(int categoryID, String categoryName)
    {
        _categoryID = categoryID;
        _categoryName = categoryName;
    }

    /**
     * Returns the categoryID this inventory falls under.
     *
     * @return this inventory's categoryID
     */
    public int getCategoryID()
    {
        return _categoryID;
    }

    /**
     * Returns the name of this category.
     *
     * @return this category's name
     */
    public String getCategoryName()
    {
        return _categoryName;
    }
}
