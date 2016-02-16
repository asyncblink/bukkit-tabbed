package com.keenant.tabbed.tablist.table;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.BlankTabItem;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.tablist.CustomTabList;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@ToString
public class TableTabList extends CustomTabList {
    @Getter private final int columns;
    @Getter private final int rows;

    public TableTabList(Tabbed tabbed, Player player, int columns) {
        super(tabbed, player);
        this.columns = columns;
        this.rows = getMinRows(columns);
        reset();
    }

    @Override
    public int getMaxItems() {
        return this.columns * this.rows;
    }

    @Override
    public TableTabList enable() {
        super.enable();
        reset();
        return this;
    }

    @Override
    public TableTabList disable() {
        super.disable();
        return this;
    }

    public boolean contains(TabCell cell) {
        return contains(getIndex(cell));
    }

    public TabItem get(TabCell cell) {
        return get(getIndex(cell));
    }

    @Override
    public TabItem get(int index) {
        if (contains(index))
            return super.get(index);
        return new BlankTabItem(true);
    }

    public TabItem set(int column, int row, TabItem item) {
        return set(new TabCell(column, row), item);
    }

    public TabItem set(TabCell cell, TabItem item) {
        validateCell(cell);

        TabItem previousItem = this.get(cell);

        updateTable(cell, item);

        return previousItem;
    }

    public void set(Map<TabCell,TabItem> items) {
        for (Entry<TabCell,TabItem> entry : items.entrySet())
            validateCell(entry.getKey());
        updateTable(items);
    }

    protected void updateTable(TabCell cell, TabItem newItem) {
        update(getIndex(cell), newItem);
    }

    protected void updateTable(Map<TabCell,TabItem> items) {
        Map<Integer,TabItem> map = new HashMap<>();
        for (Entry<TabCell,TabItem> entry : items.entrySet())
            map.put(getIndex(entry.getKey()), entry.getValue());
        update(map);
    }

    private void reset() {
        Map<TabCell,TabItem> items = new HashMap<>();
        for (int x = 0; x < this.columns; x++) {
            for (int y = 0; y < this.rows; y++) {
                TabCell cell = new TabCell(x, y);
                TabItem item = new BlankTabItem(true);
                items.put(cell, item);
            }
        }
        updateTable(items);
        clear();
    }

    private static int getMinRows(int columns) {
        if (columns == 1)
            return 1;
        else if (columns == 2)
            return 11;
        else if (columns == 3)
            return 14;
        else if (columns == 4)
            return 20;
        else
            throw new RuntimeException("invalid column count " + columns);
    }

    private int getIndex(TabCell cell) {
        return cell.getRow() + this.rows * cell.getColumn();
    }

    private void validateCell(TabCell cell) {
        Preconditions.checkArgument(cell.getRow() >= 0 && cell.getRow() < this.rows, "row not in allowed range");
        Preconditions.checkArgument(cell.getColumn() >= 0 && cell.getColumn() < this.columns, "column not in allowed range");
    }
}