package com.sqiwy.menu.model;

import com.sqiwy.menu.provider.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by abrysov
 */

public class WeightBlockCM extends WeightBlock{
    public static final int TYPE_MEDIUM = 0;
	public static final int TYPE_TWO_SMALL = 1;
	public static final int TYPE_SMALL_2TEXT = 2;
    public static final int TYPE_1TEXT_1TEXT = 3;

	public enum Type {
        TYPE_MEDIUM,
		TYPE_TWO_SMALL,
        TYPE_SMALL_2TEXT,
        TYPE_1TEXT_1TEXT
	}

	public WeightBlockCM(int type, List<WeightItem> blockItems) {
		super(type, blockItems);
	}

    public static List<WeightBlockCM> buildBlocks(List<? extends WeightItem> items) {
        List<WeightBlockCM> blocks = new ArrayList<WeightBlockCM>();
        LinkedList<WeightItem> inItems = new LinkedList<WeightItem>(items);
        while (!inItems.isEmpty()) {
            WeightBlockCM block = composeBlock(inItems);
            if (block != null) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    private static WeightBlockCM composeBlock(LinkedList<WeightItem> items) {
        WeightItem firstItem = items.remove(0);
        switch (firstItem.getItemWeight()) {
            case DBHelper.PRODUCT_SIGNIFICANCE_BIG:
                return composeBigBlock(items, firstItem);
            case DBHelper.PRODUCT_SIGNIFICANCE_STANDARD:
                return composeStandardBlock(items, firstItem);
            case DBHelper.PRODUCT_SIGNIFICANCE_TEXT:
                return composeTextBlock(items, firstItem);
        }
        return null;
    }

    private static WeightBlockCM composeBigBlock(LinkedList<WeightItem> items, WeightItem firstItem) {
        return new WeightBlockCM(TYPE_MEDIUM, Arrays.asList(firstItem));
    }

    private static WeightBlockCM composeStandardBlock(LinkedList<WeightItem> items, WeightItem firstItem) {
        WeightItem secondItem = findItem(items, DBHelper.PRODUCT_SIGNIFICANCE_STANDARD);
        if (secondItem != null) {
            return new WeightBlockCM(TYPE_TWO_SMALL, Arrays.asList(firstItem, secondItem));
        }
        secondItem = findItem(items, DBHelper.PRODUCT_SIGNIFICANCE_TEXT);
        if (secondItem == null) {
            return new WeightBlockCM(TYPE_SMALL_2TEXT, Arrays.asList(firstItem));
        }
        WeightItem thirdItem = findItem(items, DBHelper.PRODUCT_SIGNIFICANCE_TEXT);
        if (thirdItem != null) {
            return new WeightBlockCM(TYPE_SMALL_2TEXT, Arrays.asList(firstItem, secondItem, thirdItem));
        }
        return new WeightBlockCM(TYPE_SMALL_2TEXT, Arrays.asList(firstItem, secondItem));
    }

    private static WeightBlockCM composeTextBlock(LinkedList<WeightItem> items, WeightItem firstItem) {
        WeightItem secondItem = findItem(items, DBHelper.PRODUCT_SIGNIFICANCE_TEXT);
        if (secondItem != null) {
            return new WeightBlockCM(TYPE_1TEXT_1TEXT, Arrays.asList(firstItem, secondItem));
        }
        return new WeightBlockCM(TYPE_1TEXT_1TEXT, Arrays.asList(firstItem));
    }

    private static WeightItem findItem(LinkedList<WeightItem> items, int significance) {
        for (ListIterator<WeightItem> it = items.listIterator(); it.hasNext();) {
            WeightItem item = it.next();
            if (item.getItemWeight() == significance) {
                it.remove();
                return item;
            }
        }
        return null;
    }
}
